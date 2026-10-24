package com.movies.impl_internal.ui.catalog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.movies.designsystem.components.banners.OfflineBanner
import com.movies.domain.model.MediaKey
import com.movies.domain.model.MediaSummary
import com.movies.impl_internal.extensions.labelRes
import com.movies.impl_internal.ui.utils.PagedMediaGrid
import com.movies.ifood_challenge.designsystem.R as DsR

@Composable
internal fun CatalogScreenRoute(
    onBack: () -> Unit,
    onOpenDetail: (MediaKey) -> Unit,
    viewModel: CatalogViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val items = viewModel.items.collectAsLazyPagingItems()
    val snackbar = remember { SnackbarHostState() }
    val currentOnOpenDetail by rememberUpdatedState(onOpenDetail)
    LaunchedEffect(viewModel) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is CatalogUiEffect.OpenDetail -> currentOnOpenDetail(effect.key)
                is CatalogUiEffect.ShowMessage -> snackbar.showSnackbar(effect.message)
            }
        }
    }
    CatalogScreen(state, items, snackbar, onBack, viewModel::onAction)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CatalogScreen(
    state: CatalogUiState,
    items: LazyPagingItems<MediaSummary>,
    snackbar: SnackbarHostState,
    onBack: () -> Unit,
    onAction: (CatalogUiAction) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(listOfNotNull(stringResource(state.category.labelRes()), state.genre).joinToString(" · ")) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(painterResource(DsR.drawable.ic_chevron_left), contentDescription = stringResource(DsR.string.ds_back))
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            if (state.isOffline) OfflineBanner()
            PagedMediaGrid(
                items = items,
                favorites = state.favorites,
                onMediaClick = { onAction(CatalogUiAction.MediaClicked(it)) },
                onFavoriteClick = { onAction(CatalogUiAction.FavoriteClicked(it)) },
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
