package com.movies.impl_internal.ui

// Ações do usuário possíveis na tela detail.
internal sealed interface DetailUiAction {
    data object FavoriteClicked : DetailUiAction
    data object Retry : DetailUiAction
}
