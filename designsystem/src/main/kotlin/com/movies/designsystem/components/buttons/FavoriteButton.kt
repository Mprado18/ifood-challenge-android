package com.movies.designsystem.components.buttons

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import com.movies.designsystem.theme.dimens
import com.movies.ifood_challenge.designsystem.R

// Composable favorite button.
@Composable
fun FavoriteButton(
    isFavorite: Boolean,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val description = stringResource(if (isFavorite) R.string.ds_favorite_remove else R.string.ds_favorite_add, title)
    val state = stringResource(if (isFavorite) R.string.ds_favorited else R.string.ds_not_favorited)
    FilledTonalIconButton(
        onClick = onClick,
        colors = IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
            contentColor = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
        ),
        modifier = modifier
            .padding(MaterialTheme.dimens.xs)
            .size(36.dp)
            .semantics {
                contentDescription = description
                stateDescription = state
            },
    ) {
        Icon(
            painter = painterResource(if (isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
        )
    }
}
