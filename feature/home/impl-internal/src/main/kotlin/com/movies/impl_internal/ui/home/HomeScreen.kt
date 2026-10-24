package com.movies.impl_internal.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.movies.designsystem.components.banners.OfflineBanner
import com.movies.designsystem.components.cards.MediaCard
import com.movies.designsystem.components.carousel.CarouselSection
import com.movies.designsystem.components.chips.ChipRow
import com.movies.designsystem.components.loading.skeleton.SkeletonCarousel
import com.movies.designsystem.theme.dimens
import com.movies.domain.model.Category
import com.movies.domain.model.MediaKey
import com.movies.domain.model.MediaSummary
import com.movies.ifood_challenge.feature.home.impl_internal.R
import com.movies.impl_internal.extensions.labelRes
import com.movies.impl_internal.ui.utils.PagedMediaGrid
import com.movies.ifood_challenge.designsystem.R as DsR

@Composable
internal fun HomeScreenRoute(
    onOpenDetail: (MediaKey) -> Unit,
    onOpenCategory: (Category, String?) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val searchItems = viewModel.searchResults.collectAsLazyPagingItems()
    val snackbar = remember { SnackbarHostState() }
    val currentOnOpenDetail by rememberUpdatedState(onOpenDetail)
    val currentOnOpenCategory by rememberUpdatedState(onOpenCategory)
    LaunchedEffect(viewModel) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is HomeUiEffect.OpenDetail -> currentOnOpenDetail(effect.key)
                is HomeUiEffect.OpenCategory -> currentOnOpenCategory(effect.category, effect.genre)
                is HomeUiEffect.ShowMessage -> snackbar.showSnackbar(effect.message)
            }
        }
    }
    HomeScreen(state, searchItems, snackbar, viewModel::onAction)
}

@Composable
internal fun HomeScreen(
    state: HomeUiState,
    searchItems: LazyPagingItems<MediaSummary>,
    snackbar: SnackbarHostState,
    onAction: (HomeUiAction) -> Unit,
) {
    val dimens = MaterialTheme.dimens
    val searchDescription = stringResource(R.string.home_search_description)
    Scaffold(snackbarHost = { SnackbarHost(snackbar) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            Text(
                stringResource(R.string.home_title),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(horizontal = dimens.md, vertical = dimens.sm),
            )
            TextField(
                value = state.query,
                onValueChange = { onAction(HomeUiAction.QueryChanged(it)) },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium,
                placeholder = { Text(stringResource(R.string.home_search_hint), style = MaterialTheme.typography.bodyMedium) },
                shape = CircleShape,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                trailingIcon = {
                    if (state.query.isNotEmpty()) {
                        IconButton(onClick = { onAction(HomeUiAction.QueryChanged("")) }) {
                            Icon(
                                painter = painterResource(DsR.drawable.ic_close),
                                contentDescription = stringResource(R.string.home_search_clear),
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimens.md)
                    .heightIn(min = 48.dp)
                    .semantics { contentDescription = searchDescription },
            )
            if (state.isOffline) OfflineBanner(Modifier.padding(top = dimens.sm))
            if (state.genres.isNotEmpty()) {
                ChipRow(
                    options = state.genres,
                    selected = setOfNotNull(state.selectedGenre),
                    label = { it },
                    onToggle = { onAction(HomeUiAction.GenreToggled(it)) },
                    modifier = Modifier.padding(vertical = dimens.sm),
                )
            }
            if (state.isSearching) {
                PagedMediaGrid(
                    items = searchItems,
                    favorites = state.favorites,
                    onMediaClick = { onAction(HomeUiAction.MediaClicked(it)) },
                    onFavoriteClick = { onAction(HomeUiAction.FavoriteClicked(it)) },
                    modifier = Modifier.fillMaxSize(),
                    bottomPadding = dimens.bottomBarSpace,
                )
            } else {
                CategorySections(state, onAction)
            }
        }
    }
}

@Composable
private fun CategorySections(state: HomeUiState, onAction: (HomeUiAction) -> Unit) {
    val dimens = MaterialTheme.dimens
    LazyColumn(
        contentPadding = PaddingValues(bottom = dimens.lg + dimens.bottomBarSpace),
        verticalArrangement = Arrangement.spacedBy(dimens.md),
    ) {
        items(state.sections.filter { it.status != SectionStatus.Empty }, key = { it.category.name }) { section ->
            when (section.status) {
                SectionStatus.Loading -> SkeletonCarousel()
                SectionStatus.Content -> CarouselSection(
                    title = stringResource(section.category.labelRes()),
                    items = section.items,
                    key = { it.key.uniqueId },
                    onSeeAll = { onAction(HomeUiAction.SeeAllClicked(section.category)) },
                ) { media ->
                    MediaCard(
                        title = media.title,
                        imageUrl = media.posterUrl,
                        rating = media.voteAverage,
                        isFavorite = media.key in state.favorites,
                        onClick = { onAction(HomeUiAction.MediaClicked(media.key)) },
                        onFavoriteClick = { onAction(HomeUiAction.FavoriteClicked(media)) },
                    )
                }
                else -> SectionMessage(section, onRetry = { onAction(HomeUiAction.Retry) })
            }
        }
        item {
            Text(
                stringResource(R.string.home_tmdb_notice),
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(horizontal = dimens.md),
            )
        }
    }
}

@Composable
private fun SectionMessage(section: SectionState, onRetry: () -> Unit) {
    val dimens = MaterialTheme.dimens
    Column(Modifier.padding(horizontal = dimens.md)) {
        Text(stringResource(section.category.labelRes()), style = MaterialTheme.typography.titleLarge)
        Row(verticalAlignment = Alignment.CenterVertically) {
            val error = section.status as? SectionStatus.Error
            Text(
                error?.message.orEmpty(),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f),
            )
            if (error != null) TextButton(onClick = onRetry) { Text(stringResource(DsR.string.ds_retry)) }
        }
    }
}
