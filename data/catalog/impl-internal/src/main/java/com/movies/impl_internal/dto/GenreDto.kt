package com.movies.impl_internal.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// DTO da resposta da API para genre.
@Serializable
internal data class GenreDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
)
