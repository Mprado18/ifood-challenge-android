package com.movies.impl_internal.ui.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.movies.common.ConnectivityObserver
import com.movies.domain.error.toUserMessage
import com.movies.domain.extensions.toggle
import com.movies.domain.model.Category
import com.movies.domain.model.Genre
import com.movies.domain.model.MediaSummary
import com.movies.domain.repository.CatalogRepository
import com.movies.domain.repository.FavoriteRepository
import com.movies.domain.repository.GenreRepository
import com.movies.impl_internal.extensions.matchingName
import com.movies.impl_internal.ui.utils.RefreshStatus
import com.movies.impl_internal.ui.utils.buildSection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val QUERY_KEY = "query"
private const val GENRE_KEY = "genre"
internal const val SEARCH_DEBOUNCE_MS = 300L

// Combina busca, gênero selecionado e nomes de gêneros disponíveis.
private data class FilterState(val query: String, val selectedGenre: String?, val genreNames: List<String>)

// ViewModel da tela home: mantém o estado de UI e processa as ações do usuário.
@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
internal class HomeViewModel @Inject constructor(
    private val catalogRepository: CatalogRepository,
    private val genreRepository: GenreRepository,
    private val favoriteRepository: FavoriteRepository,
    connectivity: ConnectivityObserver,
    private val savedState: SavedStateHandle,
) : ViewModel() {
    private val query = savedState.getStateFlow(QUERY_KEY, "")
    private val selectedGenre = savedState.getStateFlow<String?>(GENRE_KEY, null)
    private val refreshStatuses = MutableStateFlow<Map<Category, RefreshStatus>>(Category.entries.associateWith { RefreshStatus.Loading })
    private val effectChannel = Channel<HomeUiEffect>(Channel.BUFFERED)

    val uiEffect: Flow<HomeUiEffect> = effectChannel.receiveAsFlow()

    private val allGenres: Flow<List<Genre>> = genreRepository.observeGenres()

    private val activeGenres: Flow<List<Genre>> =
        combine(allGenres, selectedGenre) { all, name -> all.matchingName(name) }

    private val sections: Flow<List<SectionState>> = activeGenres.flatMapLatest { genres ->
        combine(observePreviews(genres), refreshStatuses) { previews, statuses ->
            Category.entries.mapIndexed { index, category ->
                buildSection(category, previews[index], statuses.getValue(category))
            }
        }
    }

    private val filterState: Flow<FilterState> =
        combine(query, selectedGenre, allGenres) { text, selected, genres ->
            FilterState(text, selected, genres.map { it.name }.distinct().sorted())
        }

    val uiState: StateFlow<HomeUiState> = combine(
        filterState,
        sections,
        favoriteRepository.observeFavoriteKeys(),
        connectivity.observeOnline(),
    ) { filter, sectionList, favorites, online ->
        HomeUiState(filter.query, filter.genreNames, filter.selectedGenre, sectionList, favorites, isOffline = !online)
    }.catch { effectChannel.send(HomeUiEffect.ShowMessage(it.toUserMessage())) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    val searchResults: Flow<PagingData<MediaSummary>> = combine(
        query.debounce(SEARCH_DEBOUNCE_MS).map { it.trim() }.distinctUntilChanged(),
        activeGenres,
    ) { text, genres -> text to genres }
        .flatMapLatest { (text, genres) ->
            if (text.isBlank()) flowOf(PagingData.empty()) else catalogRepository.searchPagingFlow(text, genres)
        }
        .cachedIn(viewModelScope)

    init {
        refreshAllCategories()
    }

    fun onAction(action: HomeUiAction) {
        when (action) {
            is HomeUiAction.QueryChanged -> savedState[QUERY_KEY] = action.query
            is HomeUiAction.GenreToggled ->
                savedState[GENRE_KEY] = if (selectedGenre.value == action.genre) null else action.genre

            is HomeUiAction.FavoriteClicked -> toggleFavorite(action.media)
            is HomeUiAction.MediaClicked -> effectChannel.trySend(HomeUiEffect.OpenDetail(action.key))
            is HomeUiAction.SeeAllClicked ->
                effectChannel.trySend(HomeUiEffect.OpenCategory(action.category, selectedGenre.value))

            HomeUiAction.Retry -> refreshAllCategories()
        }
    }

    private fun observePreviews(genres: List<Genre>): Flow<List<List<MediaSummary>>> =
        combine(Category.entries.map { catalogRepository.observePreview(it, genres) }) { it.toList() }

    private fun refreshAllCategories() {
        refreshStatuses.update { Category.entries.associateWith { RefreshStatus.Loading } }
        viewModelScope.launch { genreRepository.refreshGenres() }
        Category.entries.forEach { category -> viewModelScope.launch { refreshCategory(category) } }
    }

    private suspend fun refreshCategory(category: Category) {
        val status = catalogRepository.refreshCategory(category)
            .fold({ RefreshStatus.Done }, { RefreshStatus.Failed(it.toUserMessage()) })
        refreshStatuses.update { it + (category to status) }
    }

    private fun toggleFavorite(media: MediaSummary) {
        viewModelScope.launch {
            favoriteRepository.toggle(media, uiState.value.favorites).onFailure {
                effectChannel.send(HomeUiEffect.ShowMessage(it.toUserMessage()))
            }
        }
    }
}
