package com.movies.impl_internal.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.movies.designsystem.components.badges.RatingBadge
import com.movies.designsystem.components.banners.OfflineBanner
import com.movies.designsystem.components.buttons.FavoriteButton
import com.movies.designsystem.components.image.PosterImage
import com.movies.designsystem.components.loading.skeleton.SkeletonCastRow
import com.movies.designsystem.components.loading.skeleton.SkeletonDetail
import com.movies.designsystem.components.text.MessageState
import com.movies.designsystem.theme.dimens
import com.movies.domain.model.CastMember
import com.movies.domain.model.MediaDetail
import com.movies.domain.model.MediaSummary
import com.movies.domain.model.MediaType
import com.movies.ifood_challenge.feature.detail.impl_internal.R
import com.movies.ifood_challenge.designsystem.R as DsR

private val BackdropHeight = 220.dp
private val CastItemSize = DpSize(96.dp, 150.dp)
private val CastPhotoSize = 80.dp

@Composable
internal fun DetailScreenRoute(onBack: () -> Unit, viewModel: DetailViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    LaunchedEffect(viewModel) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is DetailUiEffect.ShowMessage -> snackbar.showSnackbar(effect.message)
            }
        }
    }
    DetailScreen(state, snackbar, onBack, viewModel::onAction)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DetailScreen(
    state: DetailUiState,
    snackbar: SnackbarHostState,
    onBack: () -> Unit,
    onAction: (DetailUiAction) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.media?.title.orEmpty(), maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(painterResource(DsR.drawable.ic_chevron_left), contentDescription = stringResource(DsR.string.ds_back))
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (state.isOffline) OfflineBanner()
            val media = state.media
            when {
                state.isBlockingError -> MessageState(
                    title = stringResource(DsR.string.ds_error_title),
                    message = state.error.orEmpty(),
                    actionLabel = stringResource(DsR.string.ds_retry),
                    onAction = { onAction(DetailUiAction.Retry) },
                )

                media == null -> SkeletonDetail()
                else -> DetailContent(state, media, onAction)
            }
        }
    }
}

@Composable
private fun DetailContent(state: DetailUiState, media: MediaSummary, onAction: (DetailUiAction) -> Unit) {
    val dimens = MaterialTheme.dimens
    val detail = state.detail
    LazyColumn(
        contentPadding = PaddingValues(bottom = dimens.lg),
        verticalArrangement = Arrangement.spacedBy(dimens.lg),
    ) {
        item {
            PosterImage(
                url = media.backdropUrl ?: media.posterUrl,
                description = media.title,
                modifier = Modifier
                    .padding(horizontal = dimens.md)
                    .fillMaxWidth()
                    .height(BackdropHeight)
                    .clip(MaterialTheme.shapes.large),
            )
        }
        item {
            Column(Modifier.padding(horizontal = dimens.md), verticalArrangement = Arrangement.spacedBy(dimens.sm)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(dimens.xs)) {
                        Text(media.title, style = MaterialTheme.typography.headlineMedium)
                        detail?.tagline?.takeIf { it.isNotBlank() }?.let {
                            Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                        }
                    }
                    FavoriteButton(state.isFavorite, media.title, onClick = { onAction(DetailUiAction.FavoriteClicked) })
                }
                RatingBadge(media.voteAverage, voteCount = media.voteCount)
                Text(
                    metadata(media, detail),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        if (detail != null && detail.genres.isNotEmpty()) {
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = dimens.md),
                    horizontalArrangement = Arrangement.spacedBy(dimens.sm),
                ) {
                    items(detail.genres, key = { it.id }) {
                        AssistChip(
                            onClick = {},
                            label = { Text(it.name, style = MaterialTheme.typography.labelLarge) },
                            shape = CircleShape,
                            colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                            border = null,
                        )
                    }
                }
            }
        }
        item {
            Column(Modifier.padding(horizontal = dimens.md), verticalArrangement = Arrangement.spacedBy(dimens.sm)) {
                Text(stringResource(R.string.detail_synopsis), style = MaterialTheme.typography.titleLarge)
                Text(
                    media.overview.ifBlank { stringResource(R.string.detail_synopsis_missing) },
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
        item { CastSection(state, detail) }
    }
}

@Composable
private fun CastSection(state: DetailUiState, detail: MediaDetail?) {
    val dimens = MaterialTheme.dimens
    Column(verticalArrangement = Arrangement.spacedBy(dimens.md)) {
        Text(
            stringResource(R.string.detail_cast),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = dimens.md),
        )
        when {
            detail == null && state.isLoading -> SkeletonCastRow()
            detail == null -> Text(
                when {
                    state.isOffline -> stringResource(R.string.detail_cast_offline)
                    else -> state.error ?: stringResource(R.string.detail_cast_unavailable)
                },
                modifier = Modifier.padding(horizontal = dimens.md),
                style = MaterialTheme.typography.bodyMedium,
            )

            detail.cast.isEmpty() -> Text(
                stringResource(R.string.detail_cast_empty),
                modifier = Modifier.padding(horizontal = dimens.md),
                style = MaterialTheme.typography.bodyMedium,
            )

            else -> LazyRow(
                contentPadding = PaddingValues(horizontal = dimens.md),
                horizontalArrangement = Arrangement.spacedBy(dimens.md),
            ) {
                items(detail.cast, key = { it.id }) { CastItem(it) }
            }
        }
    }
}

@Composable
private fun CastItem(member: CastMember) {
    Column(Modifier.size(CastItemSize), horizontalAlignment = Alignment.CenterHorizontally) {
        PosterImage(
            member.profileUrl, member.name, Modifier
                .size(CastPhotoSize)
                .clip(CircleShape)
        )
        Text(member.name, style = MaterialTheme.typography.labelMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
        member.character?.takeIf { it.isNotBlank() }?.let {
            Text(it, style = MaterialTheme.typography.bodyMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun metadata(media: MediaSummary, detail: MediaDetail?): String = listOfNotNull(
    stringResource(if (media.key.type == MediaType.MOVIE) R.string.detail_type_movie else R.string.detail_type_tv),
    media.releaseDate?.take(4)?.takeIf { it.isNotBlank() },
    detail?.runtimeMinutes?.let { stringResource(R.string.detail_runtime, it / 60, it % 60) },
    detail?.seasons?.let { pluralStringResource(R.plurals.detail_seasons, it, it) },
    detail?.episodes?.let { stringResource(R.string.detail_episodes, it) },
    detail?.status?.takeIf { it.isNotBlank() },
).joinToString(" · ")
