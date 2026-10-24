package com.movies.impl_internal

import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.movies.designsystem.theme.MoviesTheme
import com.movies.impl_internal.ui.DetailScreen
import com.movies.impl_internal.ui.DetailUiAction
import com.movies.impl_internal.ui.DetailUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

// Testes de UI (Compose) da tela detail.
@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w400dp-h1400dp")
class DetailScreenTest {
    @get:Rule
    val rule = createComposeRule()
    private val actions = mutableListOf<DetailUiAction>()
    private var backs = 0

    private fun show(state: DetailUiState) = rule.setContent {
        MoviesTheme { DetailScreen(state, SnackbarHostState(), { backs++ }) { actions += it } }
    }

    @Test
    fun `when detail is complete then synopsis rating metadata and cast are shown`() {
        // Given
        val state = DetailUiState(media = movieStub, detail = movieDetailStub, isLoading = false)

        // When
        show(state)

        // Then
        rule.onNodeWithText("Uma princesa é capturada.").assertIsDisplayed()
        rule.onNodeWithContentDescription("Nota 8.2 (18528)").assertIsDisplayed()
        rule.onNodeWithText("Filme · 1977 · 2h 1min · Released").assertIsDisplayed()
        rule.onNodeWithText("Mark Hamill").assertIsDisplayed()
        rule.onNodeWithText("Ação").assertIsDisplayed()
    }

    @Test
    fun `when favorite and back are clicked then they are dispatched`() {
        // Given
        show(DetailUiState(media = movieStub, detail = movieDetailStub, isLoading = false))

        // When
        rule.onNodeWithContentDescription("Favoritar Star Wars").performClick()
        rule.onNodeWithContentDescription("Voltar").performClick()

        // Then
        assertEquals(listOf(DetailUiAction.FavoriteClicked), actions)
        assertEquals(1, backs)
    }

    @Test
    fun `when offline without detail then cast availability is explained`() {
        // Given
        val state = DetailUiState(media = movieStub, isLoading = false, isOffline = true)

        // When
        show(state)

        // Then
        rule.onNodeWithText("Elenco disponível após abrir este título com internet.").assertIsDisplayed()
        rule.onNodeWithText("Você está offline. Exibindo conteúdo salvo.").assertIsDisplayed()
    }

    @Test
    fun `when error is blocking then retry is offered`() {
        // Given
        show(DetailUiState(isLoading = false, error = "Sem conexão"))

        // When
        rule.onNodeWithText("Tentar novamente").performClick()

        // Then
        assertEquals(listOf(DetailUiAction.Retry), actions)
    }
}
