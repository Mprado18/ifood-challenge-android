package com.movies.impl_internal.repository

import com.movies.domain.error.CatalogException
import com.movies.domain.model.MediaKey
import com.movies.domain.model.MediaType
import com.movies.impl_internal.support.CatalogEnvironment
import com.movies.impl_internal.support.fixture
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

// Testes unitários de detail.
@RunWith(RobolectricTestRunner::class)
class DetailRepositoryImplTest {
    @get:Rule
    val environment = CatalogEnvironment()
    private val tmdb get() = environment.tmdb
    private val graph get() = environment.graph

    private val repository get() = graph.details

    @Test
    fun `when detail is opened online then it is stored and available offline`() = runBlocking {
        // Given
        tmdb.routeJsonMock("/3/movie/11", fixture("movie_detail.json"))
        val keyStub = MediaKey(MediaType.MOVIE, 11)
        assertNull(repository.observeDetail(keyStub).first())

        // When
        val result = repository.refreshDetail(keyStub)
        tmdb.server.shutdown()

        // Then
        assertTrue(result.isSuccess)
        val detail = repository.observeDetail(keyStub).first()
        assertNotNull(detail)
        assertEquals(121, detail!!.runtimeMinutes)
        assertEquals("Mark Hamill", detail.cast.first().name)
        assertEquals("Star Wars", repository.observeSummary(keyStub).first()!!.title)
    }

    @Test
    fun `when detail is refreshed then favorites and other details are unaffected`() = runBlocking {
        // Given
        tmdb.routeJsonMock("/3/movie/11", fixture("movie_detail.json"))
        tmdb.routeJsonMock("/3/tv/1399", fixture("tv_detail.json"))
        val movieKeyStub = MediaKey(MediaType.MOVIE, 11)
        val tvKeyStub = MediaKey(MediaType.TV, 1399)
        repository.refreshDetail(movieKeyStub)
        graph.favorites.setFavorite(repository.observeSummary(movieKeyStub).first()!!, true)

        // When
        repository.refreshDetail(tvKeyStub)

        // Then
        assertEquals(8, repository.observeDetail(tvKeyStub).first()!!.seasons)
        assertNotNull(repository.observeDetail(movieKeyStub).first())
        assertEquals(setOf(movieKeyStub), graph.favorites.observeFavoriteKeys().first())
    }

    @Test
    fun `when detail fails then error is reported and nothing is stored`() = runBlocking {
        // Given
        val keyStub = MediaKey(MediaType.MOVIE, 5)
        tmdb.routeErrorMock("/3/movie/5", 401)

        // When
        val result = repository.refreshDetail(keyStub)

        // Then
        assertTrue(result.exceptionOrNull() is CatalogException.Unauthorized)
        assertNull(repository.observeDetail(keyStub).first())
    }
}
