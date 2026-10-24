package com.movies.designsystem.components.carousel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.movies.designsystem.theme.dimens
import com.movies.ifood_challenge.designsystem.R

@Composable
fun <T> CarouselSection(
    title: String,
    items: List<T>,
    key: (T) -> Any,
    onSeeAll: (() -> Unit)?,
    modifier: Modifier = Modifier,
    itemContent: @Composable (T) -> Unit,
) {
    val dimens = MaterialTheme.dimens
    Column(modifier, verticalArrangement = Arrangement.spacedBy(dimens.sm)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimens.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            if (onSeeAll != null) {
                TextButton(onClick = onSeeAll) {
                    Text(stringResource(R.string.ds_see_all), style = MaterialTheme.typography.labelLarge)
                }
            }
        }
        LazyRow(
            contentPadding = PaddingValues(horizontal = dimens.md),
            horizontalArrangement = Arrangement.spacedBy(dimens.sm),
        ) {
            items(items, key = key) { itemContent(it) }
        }
    }
}
