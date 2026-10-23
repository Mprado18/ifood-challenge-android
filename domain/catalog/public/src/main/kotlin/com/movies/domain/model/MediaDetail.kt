package com.movies.domain.model

data class MediaDetail(
    val summary: MediaSummary,
    val tagline: String?,
    val genres: List<Genre>,
    val status: String?,
    val runtimeMinutes: Int?,
    val seasons: Int?,
    val episodes: Int?,
    val cast: List<CastMember>,
)
