package com.movies.impl_internal.ui.catalog

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.movies.common.ConnectivityObserver
import com.movies.domain.error.toUserMessage
import com.movies.domain.extensions.toggle
import com.movies.domain.model.Category
import com.movies.domain.model.MediaSummary
import com.movies.domain.repository.CatalogRepository
import com.movies.domain.repository.FavoriteRepository
import com.movies.domain.repository.GenreRepository
import com.movies.impl_internal.extensions.matchingName
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Argumentos vêm de `CatalogRoute` via SavedStateHandle (nomes das propriedades da rota). */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
internal class CatalogViewModel @Inject constructor(
    private val catalogRepository: CatalogRepository,
    private val genreRepository: GenreRepository,
    private val favoriteRepository: FavoriteRepository,
    connectivity: ConnectivityObserver,
    savedState: SavedStateHandle,
) : ViewModel() {
    private val category: Category =
        Category.entries.firstOrNull { it.name == savedState.get<String>("category") } ?: Category.entries.first()
    private val genreName: String? = savedState.get<String>("genre")?.takeIf { it.isNotBlank() }
    private val effectChannel = Channel<CatalogUiEffect>(Channel.BUFFERED)

    val uiEffect: Flow<CatalogUiEffect> = effectChannel.receiveAsFlow()

    val items: Flow<PagingData<MediaSummary>> = genreRepository.observeGenres()
        .map { all -> all.matchingName(genreName) }
        .flatMapLatest { catalogRepository.categoryPagingFlow(category, it) }
        .cachedIn(viewModelScope)

    val uiState: StateFlow<CatalogUiState> = combine(
        favoriteRepository.observeFavoriteKeys(),
        connectivity.observeOnline(),
    ) { favorites, online ->
        CatalogUiState(category, genreName, favorites, !online)
    }.catch { effectChannel.send(CatalogUiEffect.ShowMessage(it.toUserMessage())) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CatalogUiState(category, genreName))

    fun onAction(action: CatalogUiAction) {
        when (action) {
            is CatalogUiAction.MediaClicked -> effectChannel.trySend(CatalogUiEffect.OpenDetail(action.key))
            is CatalogUiAction.FavoriteClicked -> viewModelScope.launch {
                favoriteRepository.toggle(action.media, uiState.value.favorites).onFailure {
                    effectChannel.send(CatalogUiEffect.ShowMessage(it.toUserMessage()))
                }
            }
        }
    }
}
