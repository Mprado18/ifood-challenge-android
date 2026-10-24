package com.movies.impl_internal.ui.home

import com.movies.domain.model.Category
import com.movies.domain.model.MediaKey

internal sealed interface HomeUiEffect {
    data class OpenDetail(val key: MediaKey) : HomeUiEffect
    data class OpenCategory(val category: Category, val genre: String?) : HomeUiEffect
    data class ShowMessage(val message: String) : HomeUiEffect
}
