package com.movies.impl_internal.mapper

import com.movies.impl_internal.dto.MovieDetailDto
import com.movies.impl_internal.dto.MovieDto
import com.movies.impl_internal.dto.PageResponseDto
import com.movies.impl_internal.dto.SearchItemDto
import com.movies.impl_internal.dto.TvDetailDto
import com.movies.impl_internal.dto.TvDto
import com.movies.impl_internal.support.fixture
import com.movies.domain.model.MediaKey
import com.movies.domain.model.MediaType
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test

class MediaDtoMapperTest {
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @Test
    fun `when mapping movie then images date and genres are mapped`() {
        // Given
        val dto = json.decodeFromString<PageResponseDto<MovieDto>>(fixture("movie_popular.json"))

        // When
        val page = dto.toMoviePage()

        // Then
        val full = page.items[0]
        assertEquals(MediaKey(MediaType.MOVIE, 11), full.key)
        assertEquals("https://image.tmdb.org/t/p/w342/poster11.jpg", full.posterUrl)
        assertEquals("1977-05-25", full.releaseDate)
        assertEquals(listOf(12, 28, 878), full.genreIds)
        assertEquals(3, page.totalPages)
        val sparse = page.items[1]
        assertNull(sparse.posterUrl)
        assertNull(sparse.releaseDate)
        assertEquals("", sparse.overview)
    }

    @Test
    fun `when mapping tv then tv key is used`() {
        // Given
        val dto = json.decodeFromString<PageResponseDto<TvDto>>(fixture("tv_top_rated.json"))

        // When
        val item = dto.toTvPage().items.single()

        // Then
        assertEquals(MediaKey(MediaType.TV, 1399), item.key)
        assertEquals("Game of Thrones", item.title)
        assertEquals("2011-04-17", item.releaseDate)
    }

    @Test
    fun `when mapping search then movies and tv keep distinct keys and people are skipped`() {
        // Given
        val dto = json.decodeFromString<PageResponseDto<SearchItemDto>>(fixture("search_multi.json"))

        // When
        val page = dto.toSearchPage()

        // Then
        assertEquals(listOf(MediaKey(MediaType.MOVIE, 11), MediaKey(MediaType.TV, 11)), page.items.map { it.key })
        assertEquals("Série com mesmo id", page.items[1].title)
    }

    @Test
    fun `when search item has no title then serialization error is thrown`() {
        // Given
        val itemStub = SearchItemDto(id = 1, mediaType = "movie")

        // When / Then
        assertThrows(SerializationException::class.java) { itemStub.toSummaryOrNull() }
    }

    @Test
    fun `when mapping movie detail then cast runtime and genres are mapped`() {
        // Given
        val dto = json.decodeFromString<MovieDetailDto>(fixture("movie_detail.json"))

        // When
        val detail = dto.toDetail()

        // Then
        assertEquals(121, detail.runtimeMinutes)
        assertNull(detail.seasons)
        assertNull(detail.summary.backdropUrl)
        assertEquals(listOf("Aventura", "Ação"), detail.genres.map { it.name })
        assertEquals("Mark Hamill", detail.cast[0].name)
        assertEquals("https://image.tmdb.org/t/p/w185/mark.jpg", detail.cast[0].profileUrl)
        assertNull(detail.cast[1].character)
        assertNull(detail.cast[1].profileUrl)
    }

    @Test
    fun `when mapping tv detail then seasons and episodes are mapped`() {
        // Given
        val dto = json.decodeFromString<TvDetailDto>(fixture("tv_detail.json"))

        // When
        val detail = dto.toDetail()

        // Then
        assertEquals(8, detail.seasons)
        assertEquals(73, detail.episodes)
        assertNull(detail.runtimeMinutes)
        assertNull(detail.tagline)
        assertEquals(MediaKey(MediaType.TV, 1399), detail.summary.key)
        assertNotNull(detail.cast.single().profileUrl)
    }
}
