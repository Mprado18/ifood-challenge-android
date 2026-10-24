package com.movies.impl_internal.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movies.common.ConnectivityObserver
import com.movies.domain.error.toUserMessage
import com.movies.domain.model.Genre
import com.movies.domain.model.MediaSummary
import com.movies.domain.repository.FavoriteRepository
import com.movies.domain.repository.GenreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// ViewModel da tela favorites: mantém o estado de UI e processa as ações do usuário.
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
internal class FavoritesViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
    private val genreRepository: GenreRepository,
    private val connectivity: ConnectivityObserver,
) : ViewModel() {
    private val effectChannel = Channel<FavoritesUiEffect>(Channel.BUFFERED)

    val uiEffect: Flow<FavoritesUiEffect> = effectChannel.receiveAsFlow()

    private val reloadCount = MutableStateFlow(0)
    private val selectedGenre = MutableStateFlow<String?>(null)

    val uiState: StateFlow<FavoritesUiState> = reloadCount
        .flatMapLatest { observeFavorites() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FavoritesUiState())

    fun onAction(action: FavoritesUiAction) {
        when (action) {
            is FavoritesUiAction.MediaClicked -> effectChannel.trySend(FavoritesUiEffect.OpenDetail(action.key))
            is FavoritesUiAction.GenreToggled ->
                selectedGenre.update { if (it == action.genre) null else action.genre }

            FavoritesUiAction.Retry -> reloadCount.update { it + 1 }
            is FavoritesUiAction.RemoveClicked -> viewModelScope.launch {
                favoriteRepository.setFavorite(action.media, false).onFailure {
                    effectChannel.send(FavoritesUiEffect.ShowMessage(it.toUserMessage()))
                }
            }
        }
    }

    private fun observeFavorites(): Flow<FavoritesUiState> = combine(
        favoriteRepository.observeFavorites(),
        genreRepository.observeGenres(),
        selectedGenre,
        connectivity.observeOnline(),
    ) { items, genres, selected, online ->
        val available = genres.filter { genre -> items.any { it.hasGenre(genre) } }.map { it.name }.distinct().sorted()
        val active = selected?.takeIf { it in available }
        val visible = if (active == null) items else items.filter { item -> genres.any { it.name == active && item.hasGenre(it) } }
        FavoritesUiState(visible, available, active, isLoading = false, isOffline = !online)
    }.catch { emit(FavoritesUiState(isLoading = false, error = it.toUserMessage())) }

    private fun MediaSummary.hasGenre(genre: Genre) = genre.mediaType == key.type && genre.id in genreIds
}
