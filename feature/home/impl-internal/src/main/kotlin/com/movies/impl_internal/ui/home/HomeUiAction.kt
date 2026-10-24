package com.movies.impl_internal.ui.home

import com.movies.domain.model.Category
import com.movies.domain.model.MediaKey
import com.movies.domain.model.MediaSummary

internal sealed interface HomeUiAction {
    data class QueryChanged(val query: String) : HomeUiAction
    data class GenreToggled(val genre: String) : HomeUiAction
    data class FavoriteClicked(val media: MediaSummary) : HomeUiAction
    data class MediaClicked(val key: MediaKey) : HomeUiAction
    data class SeeAllClicked(val category: Category) : HomeUiAction
    data object Retry : HomeUiAction
}
