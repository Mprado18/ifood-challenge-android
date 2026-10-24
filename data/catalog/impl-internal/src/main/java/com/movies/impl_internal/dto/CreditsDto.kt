package com.movies.impl_internal.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class CreditsDto(
    @SerialName("cast") val cast: List<CastDto> = emptyList(),
)
