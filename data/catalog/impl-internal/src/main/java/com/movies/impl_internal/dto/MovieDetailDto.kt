package com.movies.impl_internal.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// DTO da resposta da API para movie detail.
@Serializable
internal data class MovieDetailDto(
    @SerialName("id") val id: Int,
    @SerialName("title") val title: String,
    @SerialName("overview") val overview: String = "",
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("backdrop_path") val backdropPath: String? = null,
    @SerialName("vote_average") val voteAverage: Double = 0.0,
    @SerialName("vote_count") val voteCount: Int = 0,
    @SerialName("release_date") val releaseDate: String? = null,
    @SerialName("genres") val genres: List<GenreDto> = emptyList(),
    @SerialName("runtime") val runtime: Int? = null,
    @SerialName("tagline") val tagline: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("credits") val credits: CreditsDto? = null,
)
