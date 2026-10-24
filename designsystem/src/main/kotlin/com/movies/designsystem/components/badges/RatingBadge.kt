package com.movies.designsystem.components.badges

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.movies.designsystem.theme.MoviesColors
import com.movies.designsystem.theme.dimens
import com.movies.ifood_challenge.designsystem.R

// Composable rating badge.
@Composable
fun RatingBadge(rating: Double, modifier: Modifier = Modifier, voteCount: Int? = null) {
    val text = ratingText(rating, voteCount)
    val description = stringResource(R.string.ds_rating_description, text)
    Row(
        modifier = modifier.semantics { contentDescription = description },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("★", color = MoviesColors.Rating, style = MaterialTheme.typography.labelMedium)
        Spacer(Modifier.width(MaterialTheme.dimens.xs))
        Text(text, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun ratingText(rating: Double, voteCount: Int?): String {
    if (rating <= 0) return stringResource(R.string.ds_no_rating)
    val formatted = "%.1f".format(rating)
    return if (voteCount != null) "$formatted ($voteCount)" else formatted
}
