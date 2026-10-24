package com.movies.impl_internal.api

import com.movies.domain.error.CatalogException
import com.movies.impl_internal.support.DispatchersStub
import com.movies.impl_internal.support.NetworkConfigStub
import com.movies.impl_internal.support.TmdbServerRule
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.SocketPolicy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

// Testes unitários de api executor.
class ApiExecutorTest {
    @get:Rule
    val tmdb = TmdbServerRule()

    private suspend fun executePopularTv() =
        runCatching { tmdb.executor.execute { tmdb.api.popularTv(1) } }.exceptionOrNull()

    @Test
    fun `when json is malformed then contract error is thrown`() = runTest {
        // Given
        tmdb.enqueueMock(MockResponse().setBody("{nao-e-json"))

        // When
        val error = executePopularTv()

        // Then
        assertTrue(error is CatalogException.Contract)
    }

    @Test
    fun `when movie has no title then contract error is thrown`() = runTest {
        // Given
        tmdb.enqueueMock(MockResponse().setBody("""{"page":1,"results":[{"id":1}]}"""))

        // When
        val error = runCatching { tmdb.executor.execute { tmdb.api.popularMovies(1) } }.exceptionOrNull()

        // Then
        assertTrue(error is CatalogException.Contract)
    }

    @Test
    fun `when http error occurs then it is translated to domain error`() = runTest {
        // Given
        suspend fun errorForMock(code: Int): Throwable? {
            tmdb.enqueueMock(MockResponse().setResponseCode(code).setBody("{}"))
            return executePopularTv()
        }

        // When
        val unauthorized = errorForMock(401)
        val forbidden = errorForMock(403)
        val rateLimited = errorForMock(429)
        val unavailable = errorForMock(503)
        val notFound = errorForMock(404)

        // Then
        assertTrue(unauthorized is CatalogException.Unauthorized)
        assertTrue(forbidden is CatalogException.Unauthorized)
        assertTrue(rateLimited is CatalogException.RateLimited)
        assertEquals(503, (unavailable as CatalogException.Server).code)
        assertTrue(notFound is CatalogException.Contract)
    }

    @Test
    fun `when connection fails then network error is thrown`() = runTest {
        // Given
        tmdb.enqueueMock(MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START))

        // When
        val error = executePopularTv()

        // Then
        assertTrue(error is CatalogException.Network)
    }

    @Test
    fun `when token is blank then it fails without calling server`() = runTest {
        // Given
        val unconfigured = ApiExecutor(NetworkConfigStub(tmdb.server.url("/").toString(), bearerToken = " "), DispatchersStub)

        // When
        val error = runCatching { unconfigured.execute { tmdb.api.popularTv(1) } }.exceptionOrNull()

        // Then
        assertTrue(error is CatalogException.MissingToken)
        assertEquals(0, tmdb.server.requestCount)
    }
}
