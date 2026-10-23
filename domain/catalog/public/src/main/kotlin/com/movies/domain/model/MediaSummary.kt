package com.movies.domain.model

data class MediaSummary(
    val key: MediaKey,
    val title: String,
    val overview: String,
    val posterUrl: String?,
    val backdropUrl: String?,
    val voteAverage: Double,
    val voteCount: Int,
    val releaseDate: String?,
    val genreIds: List<Int>,
)
