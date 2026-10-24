package com.movies.impl_internal.ui.catalog

import com.movies.domain.model.MediaKey

internal sealed interface CatalogUiEffect {
    data class OpenDetail(val key: MediaKey) : CatalogUiEffect
    data class ShowMessage(val message: String) : CatalogUiEffect
}
