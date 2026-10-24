package com.movies.impl_internal.common

import com.movies.domain.model.MediaSummary

internal const val FIRST_PAGE = 1

// Resultado de uma página remota (itens e total de páginas).
internal data class RemotePage(
    val items: List<MediaSummary>,
    val page: Int,
    val totalPages: Int,
) {
    val isLast: Boolean get() = page >= totalPages
}
