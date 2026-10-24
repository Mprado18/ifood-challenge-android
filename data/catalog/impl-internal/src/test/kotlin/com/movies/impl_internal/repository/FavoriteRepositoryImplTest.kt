package com.movies.impl_internal.repository

import com.movies.domain.model.Category
import com.movies.domain.model.MediaKey
import com.movies.domain.model.MediaType
import com.movies.impl_internal.support.CatalogEnvironment
import com.movies.impl_internal.support.moviePageJson
import com.movies.impl_internal.support.tvPageJson
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

// Testes unitários de favorite.
@RunWith(RobolectricTestRunner::class)
class FavoriteRepositoryImplTest {
    @get:Rule
    val environment = CatalogEnvironment()
    private val tmdb get() = environment.tmdb
    private val graph get() = environment.graph

    @Test
    fun `when favoriting then movie and tv are separate survive refresh and work offline`() = runBlocking {
        // Given
        tmdb.routeJsonMock("/3/movie/popular?page=1", moviePageJson(1, 1, listOf(7), genreId = 18))
        tmdb.routeJsonMock("/3/tv/popular?page=1", tvPageJson(listOf(7)))
        graph.catalog.refreshCategory(Category.POPULAR_MOVIES)
        graph.catalog.refreshCategory(Category.POPULAR_TV)
        val movieStub = graph.catalog.observePreview(Category.POPULAR_MOVIES).first().single()
        val tvStub = graph.catalog.observePreview(Category.POPULAR_TV).first().single()
        val favorites = graph.favorites

        // When: favorita o filme, a lista é atualizada pela rede e depois fica offline
        val result = favorites.setFavorite(movieStub, true)
        tmdb.routeJsonMock("/3/movie/popular?page=1", moviePageJson(1, 1, listOf(99), genreId = 18))
        graph.catalog.refreshCategory(Category.POPULAR_MOVIES)
        tmdb.server.shutdown()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(listOf("Filme 7"), favorites.observeFavorites().first().map { it.title })

        // When
        favorites.setFavorite(tvStub, true)

        // Then: filme e série com o mesmo id coexistem
        assertEquals(setOf(MediaKey(MediaType.MOVIE, 7), MediaKey(MediaType.TV, 7)), favorites.observeFavoriteKeys().first())

        // When
        favorites.setFavorite(movieStub, false)

        // Then
        assertEquals(setOf(MediaKey(MediaType.TV, 7)), favorites.observeFavoriteKeys().first())
    }
}
