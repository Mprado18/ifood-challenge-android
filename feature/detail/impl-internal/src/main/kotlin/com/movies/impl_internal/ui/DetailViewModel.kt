package com.movies.impl_internal.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movies.common.ConnectivityObserver
import com.movies.domain.error.toUserMessage
import com.movies.domain.model.MediaKey
import com.movies.domain.model.MediaType
import com.movies.domain.repository.DetailRepository
import com.movies.domain.repository.FavoriteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val INVALID_ARGS_MESSAGE = "Conteúdo inválido ou indisponível."

// Estado interno do refresh (carregando/erro/concluído).
private data class RefreshStatus(val isLoading: Boolean = true, val error: String? = null)

// ViewModel da tela detail: mantém o estado de UI e processa as ações do usuário.
/** Argumentos vêm de `DetailRoute` via SavedStateHandle (nomes das propriedades da rota). */
@HiltViewModel
internal class DetailViewModel @Inject constructor(
    private val detailRepository: DetailRepository,
    private val favoriteRepository: FavoriteRepository,
    private val connectivity: ConnectivityObserver,
    savedState: SavedStateHandle,
) : ViewModel() {
    private val key: MediaKey? = parseKey(savedState)
    private val refreshStatus = MutableStateFlow(RefreshStatus())
    private val effectChannel = Channel<DetailUiEffect>(Channel.BUFFERED)

    val uiEffect: Flow<DetailUiEffect> = effectChannel.receiveAsFlow()

    val uiState: StateFlow<DetailUiState> = if (key == null) {
        MutableStateFlow(DetailUiState(isLoading = false, error = INVALID_ARGS_MESSAGE))
    } else {
        observeState(key).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DetailUiState())
    }

    init {
        loadDetail()
    }

    fun onAction(action: DetailUiAction) {
        when (action) {
            DetailUiAction.Retry -> loadDetail()
            DetailUiAction.FavoriteClicked -> toggleFavorite()
        }
    }

    private fun observeState(key: MediaKey): Flow<DetailUiState> = combine(
        detailRepository.observeSummary(key),
        detailRepository.observeDetail(key),
        favoriteRepository.observeFavoriteKeys(),
        connectivity.observeOnline(),
        refreshStatus,
    ) { summary, detail, favorites, online, status ->
        DetailUiState(
            media = detail?.summary ?: summary,
            detail = detail,
            isFavorite = key in favorites,
            isLoading = status.isLoading && detail == null,
            error = status.error,
            isOffline = !online,
        )
    }.catch { error ->
        emit(DetailUiState(isLoading = false, error = error.toUserMessage()))
    }

    private fun loadDetail() {
        val key = key ?: return
        refreshStatus.value = RefreshStatus()
        viewModelScope.launch {
            val result = detailRepository.refreshDetail(key)
            refreshStatus.update { RefreshStatus(isLoading = false, error = result.exceptionOrNull()?.toUserMessage()) }
        }
    }

    private fun toggleFavorite() {
        val current = uiState.value
        val media = current.media ?: return
        viewModelScope.launch {
            favoriteRepository.setFavorite(media, isFavorite = !current.isFavorite).onFailure {
                effectChannel.send(DetailUiEffect.ShowMessage(it.toUserMessage()))
            }
        }
    }

    private fun parseKey(savedState: SavedStateHandle): MediaKey? {
        val type = savedState.get<String>("mediaType")?.let(MediaType::fromApiValue)
        val id = savedState.get<Int>("id")
        return if (type != null && id != null) MediaKey(type, id) else null
    }
}
