package com.movies.designsystem.components.image

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.SubcomposeAsyncImage
import com.movies.designsystem.components.loading.skeleton.SkeletonBox

@Composable
fun PosterImage(url: String?, description: String?, modifier: Modifier = Modifier) {
    if (url == null) {
        PosterFallback(modifier)
        return
    }
    SubcomposeAsyncImage(
        model = url,
        contentDescription = description,
        contentScale = ContentScale.Crop,
        modifier = modifier,
        loading = { SkeletonBox(Modifier.fillMaxSize()) },
        error = { PosterFallback(Modifier.fillMaxSize()) },
    )
}

@Composable
private fun PosterFallback(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        Text("🎬", style = MaterialTheme.typography.headlineMedium)
    }
}
