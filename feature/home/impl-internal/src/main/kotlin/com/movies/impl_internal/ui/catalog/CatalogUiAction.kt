package com.movies.impl_internal.ui.catalog

import com.movies.domain.model.MediaKey
import com.movies.domain.model.MediaSummary

internal sealed interface CatalogUiAction {
    data class FavoriteClicked(val media: MediaSummary) : CatalogUiAction
    data class MediaClicked(val key: MediaKey) : CatalogUiAction
}
