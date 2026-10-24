package com.movies.impl_internal

import androidx.lifecycle.SavedStateHandle
import com.movies.domain.error.CatalogException
import com.movies.domain.model.MediaDetail
import com.movies.domain.model.MediaKey
import com.movies.domain.model.MediaSummary
import com.movies.domain.model.MediaType
import com.movies.domain.repository.DetailRepository
import com.movies.domain.repository.FavoriteRepository
import com.movies.impl_internal.ui.DetailUiAction
import com.movies.impl_internal.ui.DetailUiEffect
import com.movies.impl_internal.ui.DetailViewModel
import com.movies.testing.ConnectivityObserverStub
import com.movies.testing.MainDispatcherRule
import com.movies.testing.collectInBackground
import com.movies.testing.subscribeTo
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

// Testes unitários do DetailViewModel, cobrindo estados e eventos emitidos.
class DetailViewModelTest {
    @get:Rule
    val mainRule = MainDispatcherRule.unconfined()

    private val keyStub = MediaKey(MediaType.MOVIE, 11)
    private val summary = MutableStateFlow<MediaSummary?>(null)
    private val detail = MutableStateFlow<MediaDetail?>(null)
    private val favorites = MutableStateFlow(emptySet<MediaKey>())
    private val detailRepository = mockk<DetailRepository> {
        every { observeSummary(keyStub) } returns summary
        every { observeDetail(keyStub) } returns detail
        coEvery { refreshDetail(keyStub) } returns Result.success(Unit)
    }
    private val favoriteRepository = mockk<FavoriteRepository> {
        every { observeFavoriteKeys() } returns favorites
        coEvery { setFavorite(any(), any()) } returns Result.success(Unit)
    }
    private val connectivity = ConnectivityObserverStub()

    private fun viewModel() = DetailViewModel(
        detailRepository,
        favoriteRepository,
        connectivity,
        SavedStateHandle(mapOf("mediaType" to "movie", "id" to 11)),
    )

    private fun TestScope.observed(vm: DetailViewModel = viewModel()): DetailViewModel {
        subscribeTo(vm.uiState)
        return vm
    }

    @Test
    fun `when refresh is pending then loading is shown and detail appears afterwards`() = runTest {
        // Given
        val gate = CompletableDeferred<Result<Unit>>()
        coEvery { detailRepository.refreshDetail(keyStub) } coAnswers { gate.await() }
        val vm = observed()
        assertTrue(vm.uiState.value.isLoading)

        // When
        detail.value = movieDetailStub
        gate.complete(Result.success(Unit))

        // Then
        val state = vm.uiState.value
        assertFalse(state.isLoading)
        assertEquals(movieDetailStub, state.detail)
        assertEquals(movieStub, state.media)
        assertNull(state.error)
    }

    @Test
    fun `when detail does not exist yet then summary is shown`() = runTest {
        // Given
        summary.value = movieStub

        // When
        val vm = observed()

        // Then
        assertEquals(movieStub, vm.uiState.value.media)
        assertNull(vm.uiState.value.detail)
    }

    @Test
    fun `when load fails without data then error blocks and retry reloads`() = runTest {
        // Given
        coEvery { detailRepository.refreshDetail(keyStub) } returns Result.failure(CatalogException.Network(Exception()))
        val vm = observed()
        assertTrue(vm.uiState.value.isBlockingError)
        coEvery { detailRepository.refreshDetail(keyStub) } returns Result.success(Unit)
        summary.value = movieStub

        // When
        vm.onAction(DetailUiAction.Retry)

        // Then
        coVerify(exactly = 2) { detailRepository.refreshDetail(keyStub) }
        assertFalse(vm.uiState.value.isBlockingError)
    }

    @Test
    fun `when load fails with summary then content is kept and error is exposed`() = runTest {
        // Given
        summary.value = movieStub
        coEvery { detailRepository.refreshDetail(keyStub) } returns Result.failure(CatalogException.Server(500))

        // When
        val vm = observed()

        // Then
        assertFalse(vm.uiState.value.isBlockingError)
        assertNotNull(vm.uiState.value.error)
    }

    @Test
    fun `when favorite and connectivity change then state reflects them`() = runTest {
        // Given
        summary.value = movieStub
        val vm = observed()

        // When
        favorites.value = setOf(keyStub)
        connectivity.online.value = false

        // Then
        assertTrue(vm.uiState.value.isFavorite)
        assertTrue(vm.uiState.value.isOffline)
    }

    @Test
    fun `when favorite is clicked then media is favorited`() = runTest {
        // Given
        summary.value = movieStub
        val vm = observed()

        // When
        vm.onAction(DetailUiAction.FavoriteClicked)

        // Then
        coVerify { favoriteRepository.setFavorite(movieStub, true) }
    }

    @Test
    fun `when favorite fails then message effect is emitted`() = runTest {
        // Given
        summary.value = movieStub
        coEvery { favoriteRepository.setFavorite(any(), any()) } returns Result.failure(CatalogException.RateLimited())
        val vm = observed()
        val effects = collectInBackground(vm.uiEffect)

        // When
        vm.onAction(DetailUiAction.FavoriteClicked)

        // Then
        assertTrue(effects.single() is DetailUiEffect.ShowMessage)
    }

    @Test
    fun `when favorite is clicked without media then nothing happens`() = runTest {
        // Given
        val vm = observed()

        // When
        vm.onAction(DetailUiAction.FavoriteClicked)

        // Then
        coVerify(exactly = 0) { favoriteRepository.setFavorite(any(), any()) }
    }

    @Test
    fun `when stream fails then state is blocking error`() = runTest {
        // Given
        every { detailRepository.observeDetail(keyStub) } returns flow { throw CatalogException.Server(500) }

        // When
        val vm = observed()

        // Then
        assertTrue(vm.uiState.value.isBlockingError)
    }

    @Test
    fun `when route arguments are invalid then error is shown instead of crashing`() = runTest {
        // Given
        val invalidArgsStub = SavedStateHandle(mapOf("id" to 11))

        // When
        val vm = observed(DetailViewModel(detailRepository, favoriteRepository, connectivity, invalidArgsStub))
        vm.onAction(DetailUiAction.Retry)

        // Then
        assertTrue(vm.uiState.value.isBlockingError)
    }
}
