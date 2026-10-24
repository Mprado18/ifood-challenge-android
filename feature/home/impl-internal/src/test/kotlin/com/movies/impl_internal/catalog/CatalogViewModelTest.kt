package com.movies.impl_internal.catalog

import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingData
import com.movies.domain.error.CatalogException
import com.movies.domain.model.Category
import com.movies.domain.model.Genre
import com.movies.domain.model.MediaKey
import com.movies.domain.model.MediaType
import com.movies.domain.repository.CatalogRepository
import com.movies.domain.repository.FavoriteRepository
import com.movies.domain.repository.GenreRepository
import com.movies.impl_internal.media
import com.movies.impl_internal.ui.catalog.CatalogUiAction
import com.movies.impl_internal.ui.catalog.CatalogUiEffect
import com.movies.impl_internal.ui.catalog.CatalogViewModel
import com.movies.testing.ConnectivityObserverStub
import com.movies.testing.MainDispatcherRule
import com.movies.testing.collectInBackground
import com.movies.testing.subscribeTo
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CatalogViewModelTest {
    @get:Rule
    val mainRule = MainDispatcherRule.unconfined()

    private val favorites = MutableStateFlow(emptySet<MediaKey>())
    private val catalogRepository = mockk<CatalogRepository>()
    private val genreRepository = mockk<GenreRepository>()
    private val favoriteRepository = mockk<FavoriteRepository>()
    private val connectivity = ConnectivityObserverStub()
    private val actionGenreStub = Genre(28, "Ação", MediaType.MOVIE)

    @Before
    fun setUp() {
        every { genreRepository.observeGenres() } returns MutableStateFlow(listOf(actionGenreStub))
        every { favoriteRepository.observeFavoriteKeys() } returns favorites
        every { catalogRepository.categoryPagingFlow(any(), any()) } returns flowOf(PagingData.empty())
        coEvery { favoriteRepository.setFavorite(any(), any()) } returns Result.success(Unit)
    }

    private fun viewModel(genre: String? = null) = CatalogViewModel(
        catalogRepository,
        genreRepository,
        favoriteRepository,
        connectivity,
        SavedStateHandle(buildMap {
            put("category", Category.POPULAR_MOVIES.name)
            genre?.let { put("genre", it) }
        }),
    )

    private fun TestScope.observedViewModel(genre: String? = null): CatalogViewModel =
        viewModel(genre).also {
            subscribeTo(it.uiState)
            subscribeTo(it.items)
        }

    @Test
    fun `when no genre is given then category is paged unfiltered`() = runTest {
        // When
        val vm = observedViewModel()

        // Then
        verify { catalogRepository.categoryPagingFlow(Category.POPULAR_MOVIES, emptyList()) }
        assertEquals(Category.POPULAR_MOVIES, vm.uiState.value.category)
        assertNull(vm.uiState.value.genre)
    }

    @Test
    fun `when genre is given then category is filtered by equivalent genres`() = runTest {
        // When
        val vm = observedViewModel("Ação")

        // Then
        verify { catalogRepository.categoryPagingFlow(Category.POPULAR_MOVIES, listOf(actionGenreStub)) }
        assertEquals("Ação", vm.uiState.value.genre)
    }

    @Test
    fun `when favorites and connectivity change then state reflects them`() = runTest {
        // Given
        val vm = observedViewModel()
        val keyStub = MediaKey(MediaType.MOVIE, 1)

        // When
        favorites.value = setOf(keyStub)
        connectivity.online.value = false

        // Then
        assertEquals(setOf(keyStub), vm.uiState.value.favorites)
        assertTrue(vm.uiState.value.isOffline)
    }

    @Test
    fun `when favorite is clicked then it adds if not favorite and removes if favorite`() = runTest {
        // Given
        val vm = observedViewModel()
        val itemStub = media(1)

        // When
        vm.onAction(CatalogUiAction.FavoriteClicked(itemStub))
        favorites.value = setOf(itemStub.key)
        vm.onAction(CatalogUiAction.FavoriteClicked(itemStub))

        // Then
        coVerify { favoriteRepository.setFavorite(itemStub, true) }
        coVerify { favoriteRepository.setFavorite(itemStub, false) }
    }

    @Test
    fun `when favorite fails then message effect is emitted`() = runTest {
        // Given
        coEvery { favoriteRepository.setFavorite(any(), any()) } returns Result.failure(CatalogException.RateLimited())
        val vm = observedViewModel()
        val effects = collectInBackground(vm.uiEffect)

        // When
        vm.onAction(CatalogUiAction.FavoriteClicked(media(1)))

        // Then
        assertTrue(effects.single() is CatalogUiEffect.ShowMessage)
    }

    @Test
    fun `when media is clicked then open detail effect is emitted`() = runTest {
        // Given
        val vm = viewModel()
        val effects = collectInBackground(vm.uiEffect)
        val keyStub = MediaKey(MediaType.TV, 2)

        // When
        vm.onAction(CatalogUiAction.MediaClicked(keyStub))

        // Then
        assertEquals(listOf(CatalogUiEffect.OpenDetail(keyStub)), effects)
    }

    @Test
    fun `when favorites stream fails then message effect is emitted`() = runTest {
        // Given
        every { favoriteRepository.observeFavoriteKeys() } returns flow { throw CatalogException.Server(500) }
        val vm = viewModel()
        val effects = collectInBackground(vm.uiEffect)

        // When
        subscribeTo(vm.uiState)

        // Then
        assertTrue(effects.single() is CatalogUiEffect.ShowMessage)
    }

    @Test
    fun `when category argument is invalid then first category is used`() {
        // Given
        val invalidArgsStub = SavedStateHandle(mapOf("category" to "UNKNOWN"))

        // When
        val vm = CatalogViewModel(catalogRepository, genreRepository, favoriteRepository, connectivity, invalidArgsStub)

        // Then
        assertEquals(Category.entries.first(), vm.uiState.value.category)
    }
}
