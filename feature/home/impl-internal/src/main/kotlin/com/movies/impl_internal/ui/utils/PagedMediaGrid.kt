package com.movies.impl_internal.ui.utils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.movies.designsystem.components.cards.MediaCard
import com.movies.designsystem.components.loading.skeleton.SkeletonBox
import com.movies.designsystem.components.loading.skeleton.SkeletonGrid
import com.movies.designsystem.components.text.MessageState
import com.movies.designsystem.theme.dimens
import com.movies.domain.error.toUserMessage
import com.movies.domain.model.MediaKey
import com.movies.domain.model.MediaSummary
import com.movies.ifood_challenge.feature.home.impl_internal.R
import com.movies.ifood_challenge.designsystem.R as DsR

@Composable
internal fun PagedMediaGrid(
    items: LazyPagingItems<MediaSummary>,
    favorites: Set<MediaKey>,
    onMediaClick: (MediaKey) -> Unit,
    onFavoriteClick: (MediaSummary) -> Unit,
    modifier: Modifier = Modifier,
    bottomPadding: Dp = 0.dp,
) {
    val dimens = MaterialTheme.dimens
    when (val refresh = items.loadState.refresh) {
        is LoadState.Loading if items.itemCount == 0 -> SkeletonGrid(modifier)
        is LoadState.Error if items.itemCount == 0 -> MessageState(
            title = stringResource(DsR.string.ds_error_title),
            message = refresh.error.toUserMessage(),
            modifier = modifier,
            actionLabel = stringResource(DsR.string.ds_retry),
            onAction = items::retry,
        )

        is LoadState.NotLoading if items.itemCount == 0 -> MessageState(
            title = stringResource(R.string.home_grid_empty_title),
            message = stringResource(R.string.home_grid_empty_message),
            modifier = modifier,
        )

        else -> LazyVerticalGrid(
            columns = GridCells.Adaptive(dimens.cardWidth),
            modifier = modifier,
            contentPadding = PaddingValues(start = dimens.md, top = dimens.md, end = dimens.md, bottom = dimens.md + bottomPadding),
            horizontalArrangement = Arrangement.spacedBy(dimens.sm),
            verticalArrangement = Arrangement.spacedBy(dimens.sm),
        ) {
            items(count = items.itemCount, key = items.itemKey { it.key.uniqueId }) { index ->
                items[index]?.let { media ->
                    MediaCard(
                        title = media.title,
                        imageUrl = media.posterUrl,
                        rating = media.voteAverage,
                        isFavorite = media.key in favorites,
                        onClick = { onMediaClick(media.key) },
                        onFavoriteClick = { onFavoriteClick(media) },
                    )
                }
            }
            val append = items.loadState.append
            if (append is LoadState.Loading) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    SkeletonBox(
                        Modifier
                            .fillMaxWidth()
                            .height(dimens.skeletonTitleHeight)
                    )
                }
            } else if (append is LoadState.Error) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Button(onClick = items::retry) { Text(stringResource(DsR.string.ds_retry)) }
                }
            }
        }
    }
}
