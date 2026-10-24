package com.movies.impl_internal

import com.movies.domain.error.CatalogException
import com.movies.domain.model.MediaKey
import com.movies.domain.model.MediaSummary
import com.movies.domain.model.MediaType
import com.movies.domain.repository.FavoriteRepository
import com.movies.domain.repository.GenreRepository
import com.movies.impl_internal.ui.FavoritesUiAction
import com.movies.impl_internal.ui.FavoritesUiEffect
import com.movies.impl_internal.ui.FavoritesViewModel
import com.movies.testing.ConnectivityObserverStub
import com.movies.testing.MainDispatcherRule
import com.movies.testing.collectInBackground
import com.movies.testing.mediaSummary
import com.movies.testing.subscribeTo
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

// Testes unitários do FavoritesViewModel, cobrindo estados e eventos emitidos.
class FavoritesViewModelTest {
    @get:Rule
    val mainRule = MainDispatcherRule.unconfined()

    private val favorites = MutableStateFlow(emptyList<MediaSummary>())
    private val connectivity = ConnectivityObserverStub()
    private val genres = mockk<GenreRepository> {
        every { observeGenres() } returns MutableStateFlow(emptyList())
    }
    private val repository = mockk<FavoriteRepository> {
        every { observeFavorites() } returns favorites
        coEvery { setFavorite(any(), any()) } returns Result.success(Unit)
    }

    private fun TestScope.observedViewModel(): FavoritesViewModel =
        FavoritesViewModel(repository, genres, connectivity).also { subscribeTo(it.uiState) }

    @Test
    fun `when view model starts then it is loading and then lists stored favorites`() = runTest {
        // Given
        val viewModel = FavoritesViewModel(repository, genres, connectivity)
        assertTrue(viewModel.uiState.value.isLoading)

        // When
        subscribeTo(viewModel.uiState)
        favorites.value = listOf(mediaSummary(1), mediaSummary(2))

        // Then
        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(2, viewModel.uiState.value.items.size)
    }

    @Test
    fun `when connectivity is lost then state is offline`() = runTest {
        // Given
        val viewModel = observedViewModel()

        // When
        connectivity.online.value = false

        // Then
        assertTrue(viewModel.uiState.value.isOffline)
    }

    @Test
    fun `when remove is clicked then media is unfavorited`() = runTest {
        // Given
        val viewModel = observedViewModel()
        val effects = collectInBackground(viewModel.uiEffect)

        // When
        viewModel.onAction(FavoritesUiAction.RemoveClicked(mediaSummary(1)))

        // Then
        coVerify { repository.setFavorite(mediaSummary(1), false) }
        assertTrue(effects.isEmpty())
    }

    @Test
    fun `when remove fails then message effect is emitted`() = runTest {
        // Given
        coEvery { repository.setFavorite(any(), any()) } returns Result.failure(CatalogException.Server(500))
        val viewModel = observedViewModel()
        val effects = collectInBackground(viewModel.uiEffect)

        // When
        viewModel.onAction(FavoritesUiAction.RemoveClicked(mediaSummary(1)))

        // Then
        assertTrue(effects.single() is FavoritesUiEffect.ShowMessage)
    }

    @Test
    fun `when media is clicked then detail is opened`() = runTest {
        // Given
        val viewModel = observedViewModel()
        val effects = collectInBackground(viewModel.uiEffect)
        val keyStub = MediaKey(MediaType.TV, 4)

        // When
        viewModel.onAction(FavoritesUiAction.MediaClicked(keyStub))

        // Then
        assertEquals(listOf(FavoritesUiEffect.OpenDetail(keyStub)), effects)
    }

    @Test
    fun `when stream fails then error is shown and retry recovers`() = runTest {
        // Given
        var attempts = 0
        every { repository.observeFavorites() } answers {
            if (attempts++ == 0) flow { throw CatalogException.Server(500) } else favorites
        }
        val viewModel = observedViewModel()
        assertFalse(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.error != null)

        // When
        viewModel.onAction(FavoritesUiAction.Retry)

        // Then
        assertNull(viewModel.uiState.value.error)
    }
}
