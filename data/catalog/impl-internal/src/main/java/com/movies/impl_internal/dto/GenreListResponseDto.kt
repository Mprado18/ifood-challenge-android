package com.movies.impl_internal.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// DTO da resposta da API para genre list response.
@Serializable
internal data class GenreListResponseDto(
    @SerialName("genres") val genres: List<GenreDto>,
)
