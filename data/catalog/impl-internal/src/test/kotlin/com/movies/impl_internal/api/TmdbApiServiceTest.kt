package com.movies.impl_internal.api

import com.movies.impl_internal.support.TmdbServerRule
import com.movies.impl_internal.support.fixture
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class TmdbApiServiceTest {
    @get:Rule
    val tmdb = TmdbServerRule()

    private fun enqueueFixtureMock(name: String) = tmdb.enqueueMock(MockResponse().setBody(fixture(name)))

    @Test
    fun `when requesting popular movies then params are sent and optional fields parsed`() = runTest {
        // Given
        enqueueFixtureMock("movie_popular.json")

        // When
        val page = tmdb.executor.execute { tmdb.api.popularMovies(page = 1) }

        // Then
        assertEquals("/3/movie/popular?page=1&language=pt-BR&region=BR", tmdb.server.takeRequest().path)
        assertEquals(3, page.totalPages)
        assertEquals(listOf(12, 28, 878), page.results[0].genreIds)
        assertNull(page.results[1].posterPath)
        assertEquals("", page.results[1].overview)
    }

    @Test
    fun `when requesting now playing then expected path is used`() = runTest {
        // Given
        enqueueFixtureMock("movie_popular.json")

        // When
        tmdb.executor.execute { tmdb.api.nowPlayingMovies(page = 2) }

        // Then
        assertEquals("/3/movie/now_playing?page=2&language=pt-BR&region=BR", tmdb.server.takeRequest().path)
    }

    @Test
    fun `when requesting top rated tv then expected path is used`() = runTest {
        // Given
        enqueueFixtureMock("tv_top_rated.json")

        // When
        val page = tmdb.executor.execute { tmdb.api.topRatedTv(page = 2) }

        // Then
        assertEquals("/3/tv/top_rated?page=2&language=pt-BR", tmdb.server.takeRequest().path)
        assertEquals("Game of Thrones", page.results.single().name)
    }

    @Test
    fun `when requesting popular tv then expected path is used`() = runTest {
        // Given
        enqueueFixtureMock("tv_top_rated.json")

        // When
        tmdb.executor.execute { tmdb.api.popularTv(page = 1) }

        // Then
        assertEquals("/3/tv/popular?page=1&language=pt-BR", tmdb.server.takeRequest().path)
    }

    @Test
    fun `when searching then query is encoded and all item types are kept`() = runTest {
        // Given
        enqueueFixtureMock("search_multi.json")

        // When
        val page = tmdb.executor.execute { tmdb.api.searchMulti(query = "star wars", page = 1) }

        // Then
        assertEquals(
            "/3/search/multi?query=star%20wars&page=1&language=pt-BR&include_adult=false",
            tmdb.server.takeRequest().path,
        )
        assertEquals(listOf("movie", "tv", "person"), page.results.map { it.mediaType }.take(3))
    }

    @Test
    fun `when requesting tv genres then expected path is used`() = runTest {
        // Given
        enqueueFixtureMock("genres.json")

        // When
        val genres = tmdb.executor.execute { tmdb.api.tvGenres() }.genres

        // Then
        assertEquals("/3/genre/tv/list?language=pt-BR", tmdb.server.takeRequest().path)
        assertEquals(listOf(28, 12), genres.map { it.id })
    }

    @Test
    fun `when requesting movie detail then credits are appended`() = runTest {
        // Given
        enqueueFixtureMock("movie_detail.json")

        // When
        val detail = tmdb.executor.execute { tmdb.api.movieDetail(11) }

        // Then
        assertEquals("/3/movie/11?language=pt-BR&append_to_response=credits", tmdb.server.takeRequest().path)
        assertEquals(121, detail.runtime)
        assertEquals("Mark Hamill", detail.credits!!.cast[0].name)
    }

    @Test
    fun `when requesting tv detail then seasons and episodes are parsed`() = runTest {
        // Given
        enqueueFixtureMock("tv_detail.json")

        // When
        val detail = tmdb.executor.execute { tmdb.api.tvDetail(1399) }

        // Then
        assertEquals(8, detail.numberOfSeasons)
        assertEquals(73, detail.numberOfEpisodes)
    }
}
