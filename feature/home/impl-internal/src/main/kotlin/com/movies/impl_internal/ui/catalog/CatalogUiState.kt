package com.movies.impl_internal.ui.catalog

import com.movies.domain.model.Category
import com.movies.domain.model.MediaKey

internal data class CatalogUiState(
    val category: Category,
    val genre: String? = null,
    val favorites: Set<MediaKey> = emptySet(),
    val isOffline: Boolean = false,
)
