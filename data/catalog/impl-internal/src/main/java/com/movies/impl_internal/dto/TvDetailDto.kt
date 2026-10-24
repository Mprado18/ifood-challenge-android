package com.movies.impl_internal.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// DTO da resposta da API para tv detail.
@Serializable
internal data class TvDetailDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("overview") val overview: String = "",
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("backdrop_path") val backdropPath: String? = null,
    @SerialName("vote_average") val voteAverage: Double = 0.0,
    @SerialName("vote_count") val voteCount: Int = 0,
    @SerialName("first_air_date") val firstAirDate: String? = null,
    @SerialName("genres") val genres: List<GenreDto> = emptyList(),
    @SerialName("number_of_seasons") val numberOfSeasons: Int? = null,
    @SerialName("number_of_episodes") val numberOfEpisodes: Int? = null,
    @SerialName("tagline") val tagline: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("credits") val credits: CreditsDto? = null,
)
