package com.movies.impl_internal.ui

import com.movies.domain.model.MediaSummary
import com.movies.domain.model.MediaType

// Estado de UI da tela favorites.
internal data class FavoritesUiState(
    val items: List<MediaSummary> = emptyList(),
    val genres: List<String> = emptyList(),
    val selectedGenre: String? = null,
    val isLoading: Boolean = true,
    val isOffline: Boolean = false,
    val error: String? = null,
)

internal val FavoritesUiState.movies: List<MediaSummary> get() = items.filter { it.key.type == MediaType.MOVIE }

internal val FavoritesUiState.series: List<MediaSummary> get() = items.filter { it.key.type == MediaType.TV }
