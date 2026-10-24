package com.movies.impl_internal.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// DTO da resposta da API para movie.
@Serializable
internal data class MovieDto(
    @SerialName("id") val id: Int,
    @SerialName("title") val title: String,
    @SerialName("overview") val overview: String = "",
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("backdrop_path") val backdropPath: String? = null,
    @SerialName("vote_average") val voteAverage: Double = 0.0,
    @SerialName("vote_count") val voteCount: Int = 0,
    @SerialName("release_date") val releaseDate: String? = null,
    @SerialName("genre_ids") val genreIds: List<Int> = emptyList(),
)
