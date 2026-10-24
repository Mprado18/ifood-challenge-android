package com.movies.impl_internal.home

import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.movies.designsystem.theme.MoviesTheme
import com.movies.domain.model.Category
import com.movies.domain.model.MediaSummary
import com.movies.impl_internal.media
import com.movies.impl_internal.ui.home.HomeScreen
import com.movies.impl_internal.ui.home.HomeUiAction
import com.movies.impl_internal.ui.home.HomeUiState
import com.movies.impl_internal.ui.home.SectionState
import com.movies.impl_internal.ui.home.SectionStatus
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class HomeScreenTest {
    @get:Rule
    val rule = createComposeRule()
    private val idleStub = LoadStates(
        LoadState.NotLoading(true),
        LoadState.NotLoading(true),
        LoadState.NotLoading(true),
    )
    private val actions = mutableListOf<HomeUiAction>()

    private fun show(state: HomeUiState, search: List<MediaSummary> = emptyList()) {
        rule.setContent {
            MoviesTheme {
                HomeScreen(
                    state = state,
                    searchItems = flowOf(PagingData.from(search, sourceLoadStates = idleStub)).collectAsLazyPagingItems(),
                    snackbar = SnackbarHostState(),
                    onAction = { actions += it },
                )
            }
        }
    }

    private fun contentState(vararg extra: Pair<Category, SectionStatus>) = HomeUiState(
        genres = listOf("Ação"),
        sections = Category.entries.map { category ->
            val status = extra.toMap()[category] ?: SectionStatus.Content
            SectionState(category, if (status == SectionStatus.Content) listOf(media(category.ordinal + 1)) else emptyList(), status)
        },
    )

    @Test
    fun `when home has content then search genres and first carousel are shown`() {
        // Given
        val state = contentState()

        // When
        show(state)

        // Then
        rule.onNodeWithText("Buscar filmes e séries").assertIsDisplayed()
        rule.onNodeWithText("Ação").assertIsDisplayed()
        rule.onNodeWithText("Filmes populares").assertIsDisplayed()
        rule.onNodeWithText("Filme 1").assertIsDisplayed()
    }

    @Test
    fun `when user clicks on home items then matching actions are dispatched`() {
        // Given
        show(contentState())

        // When
        rule.onNodeWithText("Ação").performClick()
        rule.onNodeWithContentDescription("Favoritar Filme 1").performClick()
        rule.onNodeWithText("Filme 1").performClick()
        rule.onNodeWithText("Ver todos").performClick()

        // Then
        assertEquals(HomeUiAction.GenreToggled("Ação"), actions[0])
        assertEquals(HomeUiAction.FavoriteClicked(media(1)), actions[1])
        assertTrue(actions[2] is HomeUiAction.MediaClicked)
        assertEquals(HomeUiAction.SeeAllClicked(Category.POPULAR_MOVIES), actions[3])
    }

    @Test
    fun `when user types in search then query changed is dispatched`() {
        // Given
        show(contentState())

        // When
        rule.onNodeWithContentDescription("Campo de busca").performTextInput("matrix")

        // Then
        assertEquals(HomeUiAction.QueryChanged("matrix"), actions.single())
    }

    @Test
    fun `when home is offline then banner is shown`() {
        // Given
        val state = contentState().copy(isOffline = true)

        // When
        show(state)

        // Then
        rule.onNodeWithText("Você está offline. Exibindo conteúdo salvo.").assertIsDisplayed()
    }

    @Test
    fun `when section fails then retry is offered and dispatched`() {
        // Given
        show(contentState(Category.POPULAR_MOVIES to SectionStatus.Error("Falhou")))

        // When
        rule.onNodeWithText("Tentar novamente").performClick()

        // Then
        rule.onNodeWithText("Falhou").assertIsDisplayed()
        assertEquals(HomeUiAction.Retry, actions.single())
    }

    @Test
    fun `when searching with results then results grid is shown`() {
        // Given
        val state = contentState().copy(query = "matrix")

        // When
        show(state, search = listOf(media(77, title = "Matrix")))

        // Then
        rule.onNodeWithText("Matrix", substring = false).assertIsDisplayed()
    }

    @Test
    fun `when searching without results then empty message is shown`() {
        // Given
        val state = contentState().copy(query = "zzz")

        // When
        show(state)
        rule.waitUntil { rule.onAllNodesWithText("Nenhum título encontrado.").fetchSemanticsNodes().isNotEmpty() }

        // Then
        rule.onNodeWithText("Nenhum título encontrado.").assertIsDisplayed()
    }
}
