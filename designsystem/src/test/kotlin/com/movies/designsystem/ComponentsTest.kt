package com.movies.designsystem

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.movies.designsystem.components.cards.MediaCard
import com.movies.designsystem.components.text.MessageState
import com.movies.designsystem.theme.MoviesTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

// Testes unitários de components.
@RunWith(RobolectricTestRunner::class)
class ComponentsTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun `when favorite is clicked on media card then title is shown and callback is invoked`() {
        // Given
        var favorited = 0
        rule.setContent {
            MoviesTheme { MediaCard("Matrix", null, 8.1, false, {}, { favorited++ }) }
        }

        // When
        rule.onNodeWithContentDescription("Favoritar Matrix").performClick()

        // Then
        rule.onNodeWithText("Matrix").assertIsDisplayed()
        assertEquals(1, favorited)
    }

    @Test
    fun `when action is clicked on message state then callback is invoked`() {
        // Given
        var retried = false
        rule.setContent {
            MoviesTheme { MessageState("Erro", "Falhou", actionLabel = "Tentar novamente", onAction = { retried = true }) }
        }

        // When
        rule.onNodeWithText("Tentar novamente").performClick()

        // Then
        assertEquals(true, retried)
    }
}
