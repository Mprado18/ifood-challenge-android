package com.movies.designsystem.components.loading.skeleton

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.movies.designsystem.components.loading.SKELETON_GRID_COUNT
import com.movies.designsystem.theme.dimens
import com.movies.ifood_challenge.designsystem.R

@Composable
fun SkeletonGrid(modifier: Modifier = Modifier) {
    val loading = stringResource(R.string.ds_loading)
    val dimens = MaterialTheme.dimens
    LazyVerticalGrid(
        columns = GridCells.Adaptive(dimens.cardWidth),
        modifier = modifier.semantics { contentDescription = loading },
        contentPadding = PaddingValues(dimens.md),
        horizontalArrangement = Arrangement.spacedBy(dimens.sm),
        verticalArrangement = Arrangement.spacedBy(dimens.sm),
        userScrollEnabled = false,
    ) {
        items(SKELETON_GRID_COUNT) { SkeletonCard() }
    }
}
