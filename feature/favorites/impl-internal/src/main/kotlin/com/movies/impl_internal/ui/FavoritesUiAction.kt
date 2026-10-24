package com.movies.impl_internal.ui

import com.movies.domain.model.MediaKey
import com.movies.domain.model.MediaSummary

// Ações do usuário possíveis na tela favorites.
internal sealed interface FavoritesUiAction {
    data class MediaClicked(val key: MediaKey) : FavoritesUiAction
    data class RemoveClicked(val media: MediaSummary) : FavoritesUiAction
    data class GenreToggled(val genre: String) : FavoritesUiAction
    data object Retry : FavoritesUiAction
}
