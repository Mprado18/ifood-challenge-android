package com.movies.impl_internal.repository

import com.movies.domain.model.MediaType
import com.movies.impl_internal.support.CatalogEnvironment
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

// Testes unitários de genre.
@RunWith(RobolectricTestRunner::class)
class GenreRepositoryImplTest {
    @get:Rule
    val environment = CatalogEnvironment()
    private val tmdb get() = environment.tmdb
    private val graph get() = environment.graph

    @Test
    fun `when genres are refreshed then they are stored per media type`() = runBlocking {
        // Given
        tmdb.routeJsonMock("/3/genre/movie/list", """{"genres":[{"id":28,"name":"Ação"}]}""")
        tmdb.routeJsonMock("/3/genre/tv/list", """{"genres":[{"id":28,"name":"Ação TV"},{"id":18,"name":"Drama"}]}""")

        // When
        val result = graph.genres.refreshGenres()

        // Then
        assertTrue(result.isSuccess)
        val genres = graph.genres.observeGenres().first()
        assertEquals(3, genres.size)
        assertEquals(setOf(MediaType.MOVIE, MediaType.TV), genres.map { it.mediaType }.toSet())
    }
}
