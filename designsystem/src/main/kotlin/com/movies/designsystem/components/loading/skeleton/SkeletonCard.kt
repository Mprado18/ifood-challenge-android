package com.movies.designsystem.components.loading.skeleton

import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.movies.designsystem.theme.dimens

@Composable
fun SkeletonCard(modifier: Modifier = Modifier) {
    SkeletonBox(modifier.size(MaterialTheme.dimens.cardWidth, MaterialTheme.dimens.cardHeight))
}
