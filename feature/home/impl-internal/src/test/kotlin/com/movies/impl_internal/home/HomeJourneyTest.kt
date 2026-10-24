package com.movies.impl_internal.home

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.movies.designsystem.theme.MoviesTheme
import com.movies.domain.model.Category
import com.movies.domain.model.Genre
import com.movies.domain.model.MediaKey
import com.movies.domain.model.MediaSummary
import com.movies.domain.model.MediaType
import com.movies.domain.repository.CatalogRepository
import com.movies.domain.repository.FavoriteRepository
import com.movies.domain.repository.GenreRepository
import com.movies.impl_internal.media
import com.movies.impl_internal.ui.home.HomeScreen
import com.movies.impl_internal.ui.home.HomeViewModel
import com.movies.testing.ConnectivityObserverStub
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Jornada integrada: ViewModel real + tela, com repositório em memória (cache offline). */
private class InMemoryRepositoryStub(private val catalog: List<MediaSummary>) :
    CatalogRepository,
    GenreRepository,
    FavoriteRepository {
    val favorites = MutableStateFlow(emptySet<MediaKey>())
    private val idleStub = LoadStates(LoadState.NotLoading(true), LoadState.NotLoading(true), LoadState.NotLoading(true))

    override fun observePreview(category: Category, genres: List<Genre>): Flow<List<MediaSummary>> =
        flowOf(catalog.filter { it.key.type == category.mediaType })

    override fun categoryPagingFlow(category: Category, genres: List<Genre>) = flowOf(PagingData.empty<MediaSummary>())

    override fun searchPagingFlow(query: String, genres: List<Genre>) = flowOf(
        PagingData.from(catalog.filter { it.title.contains(query, ignoreCase = true) }, sourceLoadStates = idleStub),
    )

    override suspend fun refreshCategory(category: Category) = Result.failure<Unit>(Exception("offline"))
    override fun observeGenres() = flowOf(listOf(Genre(28, "Ação", MediaType.MOVIE)))
    override suspend fun refreshGenres() = Result.failure<Unit>(Exception("offline"))
    override fun observeFavorites() = favorites.map { keys -> catalog.filter { it.key in keys } }
    override fun observeFavoriteKeys(): Flow<Set<MediaKey>> = favorites
    override suspend fun setFavorite(media: MediaSummary, isFavorite: Boolean): Result<Unit> {
        favorites.value = if (isFavorite) favorites.value + media.key else favorites.value - media.key
        return Result.success(Unit)
    }
}

@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w400dp-h1000dp")
class HomeJourneyTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun `when offline then cached content is shown and favorite and search work`() {
        // Given
        val repository = InMemoryRepositoryStub(listOf(media(1), media(2, title = "Matrix")))
        val offline = ConnectivityObserverStub(initiallyOnline = false)
        val viewModel = HomeViewModel(repository, repository, repository, offline, SavedStateHandle())
        rule.setContent {
            MoviesTheme {
                val state by viewModel.uiState.collectAsStateWithLifecycle()
                HomeScreen(
                    state = state,
                    searchItems = viewModel.searchResults.collectAsLazyPagingItems(),
                    snackbar = SnackbarHostState(),
                    onAction = viewModel::onAction,
                )
            }
        }

        // Then: conteúdo em cache e aviso offline
        rule.onNodeWithText("Você está offline. Exibindo conteúdo salvo.").assertIsDisplayed()
        rule.onAllNodesWithText("Filme 1").onFirst().assertIsDisplayed()

        // When: favorita um título
        rule.onAllNodesWithContentDescription("Favoritar Filme 1").onFirst().performClick()
        rule.waitUntil { rule.onAllNodesWithText("Filme 1").fetchSemanticsNodes().isNotEmpty() && repository.favorites.value.isNotEmpty() }

        // Then: o botão passa a indicar favorito
        rule.onAllNodesWithContentDescription("Remover Filme 1 dos favoritos").onFirst().assertIsDisplayed()

        // When: busca por outro título
        rule.onNodeWithContentDescription("Campo de busca").performTextInput("matr")

        // Then: só o resultado da busca aparece
        rule.waitUntil(5_000) { rule.onAllNodesWithText("Filme 1").fetchSemanticsNodes().isEmpty() }
        rule.waitUntil(5_000) { rule.onAllNodesWithText("Matrix").fetchSemanticsNodes().isNotEmpty() }
        rule.onAllNodesWithText("Matrix").onFirst().assertExists()
    }
}
