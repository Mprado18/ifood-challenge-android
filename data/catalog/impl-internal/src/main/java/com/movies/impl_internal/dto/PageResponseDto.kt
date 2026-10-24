package com.movies.impl_internal.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class PageResponseDto<T>(
    @SerialName("page") val page: Int,
    @SerialName("results") val results: List<T>,
    @SerialName("total_pages") val totalPages: Int = 1,
)
