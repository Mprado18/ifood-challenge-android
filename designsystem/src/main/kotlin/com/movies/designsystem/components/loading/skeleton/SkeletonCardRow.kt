package com.movies.designsystem.components.loading.skeleton

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.movies.designsystem.components.loading.SKELETON_CARD_COUNT
import com.movies.designsystem.theme.dimens

@Composable
fun SkeletonCardRow() {
    val dimens = MaterialTheme.dimens
    LazyRow(
        contentPadding = PaddingValues(horizontal = dimens.md),
        horizontalArrangement = Arrangement.spacedBy(dimens.sm),
        userScrollEnabled = false,
    ) {
        items(List(SKELETON_CARD_COUNT) { it }) { SkeletonCard() }
    }
}
