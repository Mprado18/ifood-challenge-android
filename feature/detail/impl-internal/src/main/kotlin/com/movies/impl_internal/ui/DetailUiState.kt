package com.movies.impl_internal.ui

import com.movies.domain.model.MediaDetail
import com.movies.domain.model.MediaSummary

// Estado de UI da tela detail.
internal data class DetailUiState(
    val media: MediaSummary? = null,
    val detail: MediaDetail? = null,
    val isFavorite: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null,
    val isOffline: Boolean = false,
) {
    /** Sem nenhum dado local e com falha, só resta o erro bloqueante. */
    val isBlockingError: Boolean get() = media == null && !isLoading && error != null
}
