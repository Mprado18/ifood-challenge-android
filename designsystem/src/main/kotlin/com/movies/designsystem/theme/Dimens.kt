package com.movies.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class Dimens(
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 16.dp,
    val lg: Dp = 24.dp,
    val xl: Dp = 32.dp,
    val cardWidth: Dp = 140.dp,
    val cardHeight: Dp = 210.dp,
    val bottomBarSpace: Dp = 96.dp,
    val minTouchTarget: Dp = 48.dp,
    val skeletonTitleWidth: Dp = 160.dp,
    val skeletonTitleHeight: Dp = 24.dp,
    val skeletonBackdropHeight: Dp = 220.dp,
    val skeletonParagraphHeight: Dp = 96.dp,
    val skeletonAvatarSize: Dp = 80.dp,
)

val LocalDimens = staticCompositionLocalOf { Dimens() }

val MaterialTheme.dimens: Dimens
    @Composable get() = LocalDimens.current
