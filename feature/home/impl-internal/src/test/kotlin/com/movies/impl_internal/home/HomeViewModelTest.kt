package com.movies.impl_internal.home

import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingData
import com.movies.domain.error.CatalogException
import com.movies.domain.model.Category
import com.movies.domain.model.Genre
import com.movies.domain.model.MediaKey
import com.movies.domain.model.MediaSummary
import com.movies.domain.model.MediaType
import com.movies.domain.repository.CatalogRepository
import com.movies.domain.repository.FavoriteRepository
import com.movies.domain.repository.GenreRepository
import com.movies.impl_internal.media
import com.movies.impl_internal.ui.home.HomeUiAction
import com.movies.impl_internal.ui.home.HomeUiEffect
import com.movies.impl_internal.ui.home.HomeViewModel
import com.movies.impl_internal.ui.home.SEARCH_DEBOUNCE_MS
import com.movies.impl_internal.ui.home.SectionStatus
import com.movies.testing.ConnectivityObserverStub
import com.movies.testing.MainDispatcherRule
import com.movies.testing.collectInBackground
import com.movies.testing.subscribeTo
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class HomeViewModelTest {
    @get:Rule val mainRule = MainDispatcherRule.unconfined()

    private val actionGenreStub = Genre(28, "Ação", MediaType.MOVIE)
    private val actionTvGenreStub = Genre(10759, "Ação", MediaType.TV)
    private val dramaGenreStub = Genre(18, "Drama", MediaType.MOVIE)
    private val genres = MutableStateFlow(listOf(actionGenreStub, actionTvGenreStub, dramaGenreStub))
    private val favorites = MutableStateFlow(emptySet<MediaKey>())
    private val previews = Category.entries.associateWith { MutableStateFlow(emptyList<MediaSummary>()) }
    private val catalogRepository = mockk<CatalogRepository>()
    private val genreRepository = mockk<GenreRepository>()
    private val favoriteRepository = mockk<FavoriteRepository>()
    private val connectivity = ConnectivityObserverStub()

    @Before
    fun setUp() {
        every { genreRepository.observeGenres() } returns genres
        every { favoriteRepository.observeFavoriteKeys() } returns favorites
        every { catalogRepository.observePreview(any(), any()) } answers { previews.getValue(firstArg()) }
        every { catalogRepository.searchPagingFlow(any(), any()) } returns flowOf(PagingData.empty())
        coEvery { genreRepository.refreshGenres() } returns Result.success(Unit)
        coEvery { catalogRepository.refreshCategory(any()) } returns Result.success(Unit)
        coEvery { favoriteRepository.setFavorite(any(), any()) } returns Result.success(Unit)
    }

    private fun viewModel() = HomeViewModel(catalogRepository, genreRepository, favoriteRepository, connectivity, SavedStateHandle())

    private fun TestScope.observedViewModel(): HomeViewModel =
        viewModel().also { subscribeTo(it.uiState) }

    @Test
    fun `when view model starts then genres and all categories are refreshed`() = runTest {
        // When
        val vm = observedViewModel()

        // Then
        coVerify { genreRepository.refreshGenres() }
        Category.entries.forEach { coVerify { catalogRepository.refreshCategory(it) } }
        assertTrue(vm.uiState.value.sections.all { it.status == SectionStatus.Empty })
    }

    @Test
    fun `when refresh is pending then section is loading and content shows once cached`() = runTest {
        // Given
        val gate = CompletableDeferred<Result<Unit>>()
        coEvery { catalogRepository.refreshCategory(Category.POPULAR_MOVIES) } coAnswers { gate.await() }
        val vm = observedViewModel()
        assertEquals(SectionStatus.Loading, vm.uiState.value.sections.first().status)

        // When
        previews.getValue(Category.POPULAR_MOVIES).value = listOf(media(1))
        val statusWhileRefreshing = vm.uiState.value.sections.first().status
        gate.complete(Result.success(Unit))

        // Then
        assertEquals(SectionStatus.Content, statusWhileRefreshing)
        assertEquals(listOf(media(1)), vm.uiState.value.sections.first().items)
    }

    @Test
    fun `when refresh fails then error shows without cache and cached content wins`() = runTest {
        // Given
        coEvery { catalogRepository.refreshCategory(Category.POPULAR_TV) } returns Result.failure(CatalogException.Server(500))
        coEvery { catalogRepository.refreshCategory(Category.TOP_RATED_TV) } returns Result.failure(CatalogException.Network(Exception()))
        previews.getValue(Category.TOP_RATED_TV).value = listOf(media(9, MediaType.TV))

        // When
        val vm = observedViewModel()

        // Then
        val sections = vm.uiState.value.sections.associateBy { it.category }
        assertTrue(sections.getValue(Category.POPULAR_TV).status is SectionStatus.Error)
        assertEquals(SectionStatus.Content, sections.getValue(Category.TOP_RATED_TV).status)
    }

    @Test
    fun `when retry is clicked then genres and categories are reloaded`() = runTest {
        // Given
        val vm = observedViewModel()

        // When
        vm.onAction(HomeUiAction.Retry)

        // Then
        coVerify(exactly = 2) { catalogRepository.refreshCategory(Category.POPULAR_MOVIES) }
        coVerify(exactly = 2) { genreRepository.refreshGenres() }
    }

    @Test
    fun `when genre is toggled then equivalent genres are filtered and toggling again clears it`() = runTest {
        // Given
        val vm = observedViewModel()
        assertEquals(listOf("Ação", "Drama"), vm.uiState.value.genres)

        // When
        vm.onAction(HomeUiAction.GenreToggled("Ação"))

        // Then
        assertEquals("Ação", vm.uiState.value.selectedGenre)
        verify { catalogRepository.observePreview(Category.POPULAR_MOVIES, listOf(actionGenreStub, actionTvGenreStub)) }

        // When
        vm.onAction(HomeUiAction.GenreToggled("Ação"))

        // Then
        assertNull(vm.uiState.value.selectedGenre)
    }

    @Test
    fun `when query changes then state updates immediately and search runs after debounce`() = runTest {
        // Given
        val vm = observedViewModel()
        subscribeTo(vm.searchResults)

        // When
        vm.onAction(HomeUiAction.QueryChanged("  star "))

        // Then
        assertTrue(vm.uiState.value.isSearching)
        verify(exactly = 0) { catalogRepository.searchPagingFlow(any(), any()) }

        // When
        advanceTimeBy(SEARCH_DEBOUNCE_MS + 1)

        // Then
        verify(exactly = 1) { catalogRepository.searchPagingFlow("star", emptyList()) }
    }

    @Test
    fun `when query is blank then search is not triggered`() = runTest {
        // Given
        val vm = observedViewModel()
        subscribeTo(vm.searchResults)

        // When
        vm.onAction(HomeUiAction.QueryChanged("   "))
        advanceTimeBy(SEARCH_DEBOUNCE_MS + 1)

        // Then
        assertFalse(vm.uiState.value.isSearching)
        verify(exactly = 0) { catalogRepository.searchPagingFlow(any(), any()) }
    }

    @Test
    fun `when favorite is clicked then it toggles based on current state`() = runTest {
        // Given
        val vm = observedViewModel()
        val itemStub = media(1)

        // When
        vm.onAction(HomeUiAction.FavoriteClicked(itemStub))

        // Then
        coVerify { favoriteRepository.setFavorite(itemStub, true) }

        // When
        favorites.value = setOf(itemStub.key)
        vm.onAction(HomeUiAction.FavoriteClicked(itemStub))

        // Then
        coVerify { favoriteRepository.setFavorite(itemStub, false) }
        assertEquals(setOf(itemStub.key), vm.uiState.value.favorites)
    }

    @Test
    fun `when favorite fails then message effect is emitted`() = runTest {
        // Given
        coEvery { favoriteRepository.setFavorite(any(), any()) } returns Result.failure(CatalogException.Network(Exception()))
        val vm = observedViewModel()
        val effects = collectInBackground(vm.uiEffect)

        // When
        vm.onAction(HomeUiAction.FavoriteClicked(media(1)))

        // Then
        assertTrue(effects.single() is HomeUiEffect.ShowMessage)
    }

    @Test
    fun `when navigation actions are sent then matching effects are emitted`() = runTest {
        // Given
        val vm = observedViewModel()
        val effects = collectInBackground(vm.uiEffect)

        // When
        vm.onAction(HomeUiAction.MediaClicked(MediaKey(MediaType.TV, 3)))
        vm.onAction(HomeUiAction.GenreToggled("Drama"))
        vm.onAction(HomeUiAction.SeeAllClicked(Category.POPULAR_TV))

        // Then
        assertEquals(
            listOf(
                HomeUiEffect.OpenDetail(MediaKey(MediaType.TV, 3)),
                HomeUiEffect.OpenCategory(Category.POPULAR_TV, "Drama"),
            ),
            effects,
        )
    }

    @Test
    fun `when connectivity is lost then state is offline`() = runTest {
        // Given
        val vm = observedViewModel()

        // When
        connectivity.online.value = false

        // Then
        assertTrue(vm.uiState.value.isOffline)
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
        assertTrue(effects.single() is HomeUiEffect.ShowMessage)
    }
}
