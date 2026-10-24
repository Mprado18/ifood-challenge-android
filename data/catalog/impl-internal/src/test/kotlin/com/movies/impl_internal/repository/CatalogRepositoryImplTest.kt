package com.movies.impl_internal.repository

import androidx.paging.testing.asSnapshot
import com.movies.domain.error.CatalogException
import com.movies.domain.model.Category
import com.movies.domain.model.Genre
import com.movies.domain.model.MediaKey
import com.movies.domain.model.MediaType
import com.movies.impl_internal.support.CatalogEnvironment
import com.movies.impl_internal.support.fixture
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

@RunWith(RobolectricTestRunner::class)
class CatalogRepositoryImplTest {
    @get:Rule
    val environment = CatalogEnvironment()
    private val tmdb get() = environment.tmdb
    private val graph get() = environment.graph

    private val repository get() = graph.catalog

    @Test
    fun `when category is refreshed then items are persisted and readable without network`() = runBlocking {
        // Given
        tmdb.routeJsonMock("/3/movie/popular?page=1", moviePageJson(1, 2, listOf(3, 1, 2)))

        // When
        val result = repository.refreshCategory(Category.POPULAR_MOVIES)
        tmdb.server.shutdown()

        // Then
        assertTrue(result.isSuccess)
        val preview = repository.observePreview(Category.POPULAR_MOVIES).first()
        assertEquals(listOf(3, 1, 2), preview.map { it.key.id })
    }

    @Test
    fun `when one category fails then others are unaffected`() = runBlocking {
        // Given
        tmdb.routeJsonMock("/3/movie/popular?page=1", moviePageJson(1, 1, listOf(1)))
        tmdb.routeJsonMock("/3/tv/popular?page=1", tvPageJson(listOf(1)))
        tmdb.routeErrorMock("/3/tv/top_rated?page=1", 500)

        // When
        repository.refreshCategory(Category.POPULAR_MOVIES)
        repository.refreshCategory(Category.POPULAR_TV)
        val failure = repository.refreshCategory(Category.TOP_RATED_TV)

        // Then
        assertTrue(failure.exceptionOrNull() is CatalogException.Server)
        assertEquals(1, repository.observePreview(Category.POPULAR_MOVIES).first().size)
        assertEquals(MediaType.TV, repository.observePreview(Category.POPULAR_TV).first().single().key.type)
        assertTrue(repository.observePreview(Category.TOP_RATED_TV).first().isEmpty())
    }

    @Test
    fun `when refresh fails then previous content is kept`() = runBlocking {
        // Given
        tmdb.routeJsonMock("/3/movie/popular?page=1", moviePageJson(1, 1, listOf(1, 2)))
        repository.refreshCategory(Category.POPULAR_MOVIES)
        tmdb.routeErrorMock("/3/movie/popular?page=1", 503)

        // When
        val result = repository.refreshCategory(Category.POPULAR_MOVIES)

        // Then
        assertTrue(result.isFailure)
        assertEquals(listOf(1, 2), repository.observePreview(Category.POPULAR_MOVIES).first().map { it.key.id })
    }

    @Test
    fun `when filtering by genre then it applies per media type`() = runBlocking {
        // Given
        tmdb.routeJsonMock(
            "/3/movie/popular?page=1",
            """{"page":1,"total_pages":1,"results":[
                {"id":1,"title":"Ação","genre_ids":[28]},{"id":2,"title":"Drama","genre_ids":[18]}]}""",
        )
        repository.refreshCategory(Category.POPULAR_MOVIES)
        val actionGenreStub = Genre(28, "Ação", MediaType.MOVIE)
        val tvOnlyGenreStub = Genre(10759, "Ação e Aventura", MediaType.TV)

        // When
        val filtered = repository.observePreview(Category.POPULAR_MOVIES, listOf(actionGenreStub)).first()
        val wrongType = repository.observePreview(Category.POPULAR_MOVIES, listOf(tvOnlyGenreStub)).first()

        // Then
        assertEquals(listOf(1), filtered.map { it.key.id })
        assertTrue(wrongType.isEmpty())
    }

    @Test
    fun `when scrolling then next page is loaded through remote mediator`() = runBlocking {
        // Given
        tmdb.routeJsonMock("/3/movie/popular?page=1", moviePageJson(1, 2, (1..20).toList()))
        tmdb.routeJsonMock("/3/movie/popular?page=2", moviePageJson(2, 2, (21..30).toList()))

        // When
        val items = repository.categoryPagingFlow(Category.POPULAR_MOVIES).asSnapshot { scrollTo(25) }

        // Then
        assertTrue(items.size > 20)
        assertEquals(21, items[20].key.id)
    }

    @Test
    fun `when searching online then remote results are persisted and people are skipped`() = runBlocking {
        // Given
        tmdb.routeJsonMock("/3/search/multi", fixture("search_multi.json"))

        // When
        val items = repository.searchPagingFlow("star").asSnapshot()

        // Then
        assertEquals(listOf(MediaKey(MediaType.MOVIE, 11), MediaKey(MediaType.TV, 11)), items.map { it.key })
    }

    @Test
    fun `when searching offline then stored titles are used`() = runBlocking {
        // Given
        tmdb.routeJsonMock("/3/movie/popular?page=1", moviePageJson(1, 1, listOf(1, 2)))
        repository.refreshCategory(Category.POPULAR_MOVIES)
        graph.connectivity.online.value = false

        // When
        val items = repository.searchPagingFlow("filme 2").asSnapshot()

        // Then
        assertEquals(listOf(2), items.map { it.key.id })
    }
}
