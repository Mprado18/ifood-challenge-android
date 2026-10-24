package com.movies.impl_internal.ui

import com.movies.domain.model.MediaKey

// Efeitos de única vez (navegação, mensagens) da tela favorites.
internal sealed interface FavoritesUiEffect {
    data class OpenDetail(val key: MediaKey) : FavoritesUiEffect
    data class ShowMessage(val message: String) : FavoritesUiEffect
}
