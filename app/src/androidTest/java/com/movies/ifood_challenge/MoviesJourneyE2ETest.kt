package com.movies.ifood_challenge

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain

// Teste end-to-end da jornada principal do app, usando servidor falso.
/** E2E no emulador: app real (Hilt, Room, Retrofit) falando com o [TmdbServerStub]. */
@HiltAndroidTest
class MoviesJourneyE2ETest {
    private val hilt = HiltAndroidRule(this)
    private val compose = createAndroidComposeRule<MainActivity>()

    @get:Rule val rules: RuleChain = RuleChain.outerRule(hilt).around(compose)

    @Before
    fun setUp() {
        TmdbServerStub.failEverything = false
        hilt.inject()
    }

    private fun waitForText(text: String) = compose.waitUntil(TIMEOUT_MS) {
        compose.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
    }

    @Test
    fun when_app_opens_then_home_lists_catalog_from_api() {
        // When
        waitForText("Star Wars")

        // Then
        compose.onNodeWithText("Filmes populares").assertIsDisplayed()
        compose.onAllNodesWithText("Star Wars").onFirst().assertIsDisplayed()
    }

    @Test
    fun when_media_is_favorited_then_it_appears_in_favorites_and_opens_detail() {
        // Given
        waitForText("Star Wars")

        // When: favorita na Home e abre a aba Favoritos
        compose.onAllNodesWithContentDescription("Favoritar Star Wars").onFirst().performClick()
        compose.onNode(hasText("Favoritos") and hasClickAction()).performClick()

        // Then
        waitForText("Star Wars")
        compose.onAllNodesWithContentDescription("Remover Star Wars dos favoritos").onFirst().assertIsDisplayed()

        // When: abre o detalhe
        compose.onAllNodesWithText("Star Wars").onFirst().performClick()

        // Then
        waitForText("Mark Hamill")
        compose.onNodeWithText("Uma princesa é capturada.").assertIsDisplayed()
    }

    private companion object {
        const val TIMEOUT_MS = 10_000L
    }
}
