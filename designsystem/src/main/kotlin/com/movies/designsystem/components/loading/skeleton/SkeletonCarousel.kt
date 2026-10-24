package com.movies.designsystem.components.loading.skeleton

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.movies.designsystem.theme.dimens
import com.movies.ifood_challenge.designsystem.R

@Composable
fun SkeletonCarousel(modifier: Modifier = Modifier) {
    val loading = stringResource(R.string.ds_loading)
    val dimens = MaterialTheme.dimens
    Column(
        modifier.semantics { contentDescription = loading },
        verticalArrangement = Arrangement.spacedBy(dimens.sm),
    ) {
        SkeletonBox(
            Modifier
                .padding(horizontal = dimens.md)
                .width(dimens.skeletonTitleWidth)
                .height(dimens.skeletonTitleHeight),
        )
        SkeletonCardRow()
    }
}
