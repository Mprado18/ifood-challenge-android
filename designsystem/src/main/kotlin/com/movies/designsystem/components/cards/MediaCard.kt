package com.movies.designsystem.components.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.movies.designsystem.components.buttons.FavoriteButton
import com.movies.designsystem.components.image.PosterImage
import com.movies.designsystem.components.badges.RatingBadge
import com.movies.designsystem.theme.dimens

@Composable
fun MediaCard(
    title: String,
    imageUrl: String?,
    rating: Double,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dimens = MaterialTheme.dimens
    Card(onClick = onClick, modifier = modifier.width(dimens.cardWidth)) {
        Column {
            Box {
                PosterImage(
                    url = imageUrl,
                    description = title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dimens.cardHeight),
                )
                FavoriteButton(
                    isFavorite = isFavorite,
                    title = title,
                    onClick = onFavoriteClick,
                    modifier = Modifier.align(Alignment.TopEnd),
                )
            }
            Column(Modifier.padding(dimens.sm), verticalArrangement = Arrangement.spacedBy(dimens.xs)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                RatingBadge(rating)
            }
        }
    }
}
