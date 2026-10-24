package com.movies.designsystem.components.loading.skeleton

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.movies.designsystem.components.loading.SHORT_LINE_FRACTION
import com.movies.designsystem.theme.dimens
import com.movies.ifood_challenge.designsystem.R

/** Esqueleto da tela de detalhe: backdrop, título, metadados, sinopse e elenco. */
@Composable
fun SkeletonDetail(modifier: Modifier = Modifier) {
    val loading = stringResource(R.string.ds_loading)
    val dimens = MaterialTheme.dimens
    Column(
        modifier.semantics { contentDescription = loading },
        verticalArrangement = Arrangement.spacedBy(dimens.md),
    ) {
        SkeletonBox(Modifier
            .fillMaxWidth()
            .height(dimens.skeletonBackdropHeight))
        Column(Modifier.padding(horizontal = dimens.md), verticalArrangement = Arrangement.spacedBy(dimens.sm)) {
            SkeletonBox(Modifier
                .width(dimens.skeletonTitleWidth)
                .height(dimens.skeletonTitleHeight))
            SkeletonBox(Modifier
                .fillMaxWidth(SHORT_LINE_FRACTION)
                .height(dimens.md))
            SkeletonBox(Modifier
                .fillMaxWidth()
                .height(dimens.skeletonParagraphHeight))
        }
        SkeletonCastRow()
    }
}
