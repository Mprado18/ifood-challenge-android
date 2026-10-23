package com.movies.testing

import com.movies.domain.model.CastMember
import com.movies.domain.model.Genre
import com.movies.domain.model.MediaDetail
import com.movies.domain.model.MediaKey
import com.movies.domain.model.MediaSummary
import com.movies.domain.model.MediaType

fun mediaSummary(
    id: Int = 1,
    type: MediaType = MediaType.MOVIE,
    title: String = "Título $id",
    overview: String = "Sinopse",
    posterUrl: String? = null,
    backdropUrl: String? = null,
    voteAverage: Double = 8.0,
    voteCount: Int = 10,
    releaseDate: String? = "2024-01-01",
    genreIds: List<Int> = emptyList(),
) = MediaSummary(
    key = MediaKey(type, id),
    title = title,
    overview = overview,
    posterUrl = posterUrl,
    backdropUrl = backdropUrl,
    voteAverage = voteAverage,
    voteCount = voteCount,
    releaseDate = releaseDate,
    genreIds = genreIds,
)

fun mediaDetail(
    summary: MediaSummary = mediaSummary(),
    cast: List<CastMember> = listOf(CastMember(2, "Mark Hamill", "Luke Skywalker", null)),
) = MediaDetail(
    summary = summary,
    tagline = "A galáxia te espera",
    genres = listOf(Genre(28, "Ação", summary.key.type)),
    status = "Released",
    runtimeMinutes = 121,
    seasons = null,
    episodes = null,
    cast = cast,
)
