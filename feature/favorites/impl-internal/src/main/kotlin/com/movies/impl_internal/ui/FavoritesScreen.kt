package com.movies.impl_internal.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.movies.designsystem.components.banners.OfflineBanner
import com.movies.designsystem.components.cards.MediaCard
import com.movies.designsystem.components.chips.ChipRow
import com.movies.designsystem.components.loading.skeleton.SkeletonGrid
import com.movies.designsystem.components.text.MessageState
import com.movies.designsystem.theme.dimens
import com.movies.domain.model.MediaKey
import com.movies.domain.model.MediaSummary
import com.movies.ifood_challenge.feature.favorites.impl_internal.R
import com.movies.ifood_challenge.designsystem.R as DsR

// Composable favorites screen route.
@Composable
internal fun FavoritesScreenRoute(
    onOpenDetail: (MediaKey) -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val currentOnOpenDetail by rememberUpdatedState(onOpenDetail)
    LaunchedEffect(viewModel) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is FavoritesUiEffect.OpenDetail -> currentOnOpenDetail(effect.key)
                is FavoritesUiEffect.ShowMessage -> snackbar.showSnackbar(effect.message)
            }
        }
    }
    FavoritesScreen(state, snackbar, viewModel::onAction)
}

@Composable
internal fun FavoritesScreen(
    state: FavoritesUiState,
    snackbar: SnackbarHostState,
    onAction: (FavoritesUiAction) -> Unit,
) {
    val dimens = MaterialTheme.dimens
    Scaffold(snackbarHost = { SnackbarHost(snackbar) }) { padding ->
        Column(Modifier
            .fillMaxSize()
            .padding(padding)) {
            Text(
                stringResource(R.string.favorites_title),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(horizontal = dimens.md, vertical = dimens.sm),
            )
            if (state.isOffline) OfflineBanner()
            when {
                state.isLoading -> SkeletonGrid()
                state.error != null && state.items.isEmpty() -> MessageState(
                    title = stringResource(DsR.string.ds_error_title),
                    message = state.error,
                    actionLabel = stringResource(DsR.string.ds_retry),
                    onAction = { onAction(FavoritesUiAction.Retry) },
                )

                state.items.isEmpty() -> MessageState(
                    title = stringResource(R.string.favorites_empty_title),
                    message = stringResource(R.string.favorites_empty_message),
                )

                else -> FavoritesContent(state, onAction)
            }
        }
    }
}

@Composable
private fun FavoritesContent(state: FavoritesUiState, onAction: (FavoritesUiAction) -> Unit) {
    val dimens = MaterialTheme.dimens
    Column {
        if (state.genres.isNotEmpty()) {
            ChipRow(
                options = state.genres,
                selected = setOfNotNull(state.selectedGenre),
                label = { it },
                onToggle = { onAction(FavoritesUiAction.GenreToggled(it)) },
                modifier = Modifier.padding(vertical = dimens.sm),
            )
        }
        LazyVerticalGrid(
            columns = GridCells.Adaptive(dimens.cardWidth),
            contentPadding = PaddingValues(start = dimens.md, top = dimens.sm, end = dimens.md, bottom = dimens.md + dimens.bottomBarSpace),
            horizontalArrangement = Arrangement.spacedBy(dimens.sm),
            verticalArrangement = Arrangement.spacedBy(dimens.sm),
        ) {
            section(R.string.favorites_movies, state.movies, onAction)
            section(R.string.favorites_series, state.series, onAction)
        }
    }
}

private fun LazyGridScope.section(
    @StringRes title: Int,
    items: List<MediaSummary>,
    onAction: (FavoritesUiAction) -> Unit,
) {
    if (items.isEmpty()) return
    item(key = "header-$title", span = { GridItemSpan(maxLineSpan) }) {
        Text(
            stringResource(title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = MaterialTheme.dimens.md, bottom = MaterialTheme.dimens.xs),
        )
    }
    items(items, key = { it.key.uniqueId }) { media ->
        MediaCard(
            title = media.title,
            imageUrl = media.posterUrl,
            rating = media.voteAverage,
            isFavorite = true,
            onClick = { onAction(FavoritesUiAction.MediaClicked(media.key)) },
            onFavoriteClick = { onAction(FavoritesUiAction.RemoveClicked(media)) },
        )
    }
}
