package com.movies.impl_internal.mapper

import com.movies.impl_internal.dto.CastDto
import com.movies.impl_internal.dto.GenreDto
import com.movies.impl_internal.dto.MovieDetailDto
import com.movies.impl_internal.dto.MovieDto
import com.movies.impl_internal.dto.PageResponseDto
import com.movies.impl_internal.dto.SearchItemDto
import com.movies.impl_internal.dto.TvDetailDto
import com.movies.impl_internal.dto.TvDto
import com.movies.impl_internal.common.RemotePage
import com.movies.domain.model.CastMember
import com.movies.domain.model.Genre
import com.movies.domain.model.MediaDetail
import com.movies.domain.model.MediaKey
import com.movies.domain.model.MediaSummary
import com.movies.domain.model.MediaType
import kotlinx.serialization.SerializationException

private const val IMAGE_BASE = "https://image.tmdb.org/t/p/"
private const val POSTER_SIZE = "w342"
private const val BACKDROP_SIZE = "w780"
private const val PROFILE_SIZE = "w185"

private fun imageUrl(
    size: String,
    path: String?,
): String? = path?.takeIf { it.isNotBlank() }?.let { "$IMAGE_BASE$size$it" }

private fun String?.orNullIfBlank(): String? = this?.takeIf { it.isNotBlank() }

internal fun PageResponseDto<MovieDto>.toMoviePage() = RemotePage(
    items = results.map { it.toSummary() },
    page = page,
    totalPages = totalPages,
)

internal fun PageResponseDto<TvDto>.toTvPage() = RemotePage(
    items = results.map { it.toSummary() },
    page = page,
    totalPages = totalPages,
)

internal fun PageResponseDto<SearchItemDto>.toSearchPage() = RemotePage(
    items = results.mapNotNull { it.toSummaryOrNull() },
    page = page,
    totalPages = totalPages,
)

internal fun MovieDto.toSummary() = MediaSummary(
    key = MediaKey(MediaType.MOVIE, id),
    title = title,
    overview = overview,
    posterUrl = imageUrl(POSTER_SIZE, posterPath),
    backdropUrl = imageUrl(BACKDROP_SIZE, backdropPath),
    voteAverage = voteAverage,
    voteCount = voteCount,
    releaseDate = releaseDate.orNullIfBlank(),
    genreIds = genreIds,
)

internal fun TvDto.toSummary() = MediaSummary(
    key = MediaKey(MediaType.TV, id),
    title = name,
    overview = overview,
    posterUrl = imageUrl(POSTER_SIZE, posterPath),
    backdropUrl = imageUrl(BACKDROP_SIZE, backdropPath),
    voteAverage = voteAverage,
    voteCount = voteCount,
    releaseDate = firstAirDate.orNullIfBlank(),
    genreIds = genreIds,
)

/** Retorna null para pessoas e tipos desconhecidos; lança [SerializationException] se filme/série vier sem título. */
internal fun SearchItemDto.toSummaryOrNull(): MediaSummary? {
    val type = MediaType.fromApiValue(mediaType) ?: return null
    val resolvedTitle = when (type) {
        MediaType.MOVIE -> title
        MediaType.TV -> name
    } ?: throw SerializationException("Item $mediaType/$id sem título")
    return MediaSummary(
        key = MediaKey(type, id),
        title = resolvedTitle,
        overview = overview,
        posterUrl = imageUrl(POSTER_SIZE, posterPath),
        backdropUrl = imageUrl(BACKDROP_SIZE, backdropPath),
        voteAverage = voteAverage,
        voteCount = voteCount,
        releaseDate = (if (type == MediaType.MOVIE) releaseDate else firstAirDate).orNullIfBlank(),
        genreIds = genreIds,
    )
}

internal fun GenreDto.toGenre(type: MediaType) = Genre(id, name, type)

private fun CastDto.toCastMember() = CastMember(
    id = id,
    name = name,
    character = character.orNullIfBlank(),
    profileUrl = imageUrl(PROFILE_SIZE, profilePath),
)

internal fun MovieDetailDto.toDetail() = MediaDetail(
    summary = MediaSummary(
        key = MediaKey(MediaType.MOVIE, id),
        title = title,
        overview = overview,
        posterUrl = imageUrl(POSTER_SIZE, posterPath),
        backdropUrl = imageUrl(BACKDROP_SIZE, backdropPath),
        voteAverage = voteAverage,
        voteCount = voteCount,
        releaseDate = releaseDate.orNullIfBlank(),
        genreIds = genres.map { it.id },
    ),
    tagline = tagline.orNullIfBlank(),
    genres = genres.map { it.toGenre(MediaType.MOVIE) },
    status = status,
    runtimeMinutes = runtime?.takeIf { it > 0 },
    seasons = null,
    episodes = null,
    cast = credits?.cast.orEmpty().map { it.toCastMember() },
)

internal fun TvDetailDto.toDetail() = MediaDetail(
    summary = MediaSummary(
        key = MediaKey(MediaType.TV, id),
        title = name,
        overview = overview,
        posterUrl = imageUrl(POSTER_SIZE, posterPath),
        backdropUrl = imageUrl(BACKDROP_SIZE, backdropPath),
        voteAverage = voteAverage,
        voteCount = voteCount,
        releaseDate = firstAirDate.orNullIfBlank(),
        genreIds = genres.map { it.id },
    ),
    tagline = tagline.orNullIfBlank(),
    genres = genres.map { it.toGenre(MediaType.TV) },
    status = status,
    runtimeMinutes = null,
    seasons = numberOfSeasons,
    episodes = numberOfEpisodes,
    cast = credits?.cast.orEmpty().map { it.toCastMember() },
)
