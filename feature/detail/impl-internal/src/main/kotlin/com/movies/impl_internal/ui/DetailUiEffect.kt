package com.movies.impl_internal.ui

// Efeitos de única vez (navegação, mensagens) da tela detail.
internal sealed interface DetailUiEffect {
    data class ShowMessage(val message: String) : DetailUiEffect
}
