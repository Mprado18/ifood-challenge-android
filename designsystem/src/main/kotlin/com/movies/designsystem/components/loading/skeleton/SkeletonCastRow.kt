package com.movies.designsystem.components.loading.skeleton

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.movies.designsystem.components.loading.SKELETON_CARD_COUNT
import com.movies.designsystem.theme.dimens

@Composable
fun SkeletonCastRow(modifier: Modifier = Modifier) {
    val dimens = MaterialTheme.dimens
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = dimens.md),
        horizontalArrangement = Arrangement.spacedBy(dimens.md),
        userScrollEnabled = false,
    ) {
        items(List(SKELETON_CARD_COUNT) { it }) {
            SkeletonBox(Modifier
                .size(dimens.skeletonAvatarSize)
                .clip(CircleShape))
        }
    }
}
