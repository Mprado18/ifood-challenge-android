package com.movies.impl_internal

import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.movies.designsystem.theme.MoviesTheme
import com.movies.impl_internal.ui.FavoritesScreen
import com.movies.impl_internal.ui.FavoritesUiAction
import com.movies.impl_internal.ui.FavoritesUiState
import com.movies.testing.mediaSummary
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

// Testes de UI (Compose) da tela favorites.
@RunWith(RobolectricTestRunner::class)
class FavoritesScreenTest {
    @get:Rule
    val rule = createComposeRule()
    private val actions = mutableListOf<FavoritesUiAction>()

    private fun show(state: FavoritesUiState) = rule.setContent {
        MoviesTheme { FavoritesScreen(state, SnackbarHostState()) { actions += it } }
    }

    @Test
    fun `when there are no favorites then guidance is shown`() {
        // Given
        val state = FavoritesUiState(isLoading = false)

        // When
        show(state)

        // Then
        rule.onNodeWithText("Nenhum favorito ainda").assertIsDisplayed()
    }

    @Test
    fun `when list is shown then click and remove are dispatched and offline banner appears`() {
        // Given
        val mediaStub = mediaSummary(1)
        show(FavoritesUiState(listOf(mediaStub), isLoading = false, isOffline = true))

        // When
        rule.onNodeWithText("Título 1").performClick()
        rule.onNodeWithContentDescription("Remover Título 1 dos favoritos").performClick()

        // Then
        rule.onNodeWithText("Você está offline. Exibindo conteúdo salvo.").assertIsDisplayed()
        assertEquals(listOf(FavoritesUiAction.MediaClicked(mediaStub.key), FavoritesUiAction.RemoveClicked(mediaStub)), actions)
    }

    @Test
    fun `when loading then empty message is not shown`() {
        // Given
        val state = FavoritesUiState()

        // When
        show(state)

        // Then
        rule.onNodeWithText("Favoritos").assertIsDisplayed()
    }
}
