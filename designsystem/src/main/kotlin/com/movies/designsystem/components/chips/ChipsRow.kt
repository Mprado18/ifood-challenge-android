package com.movies.designsystem.components.chips

import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.movies.designsystem.theme.dimens
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter

@Composable
fun <T> ChipRow(
    options: List<T>,
    selected: Set<T>,
    label: (T) -> String,
    onToggle: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dimens = MaterialTheme.dimens
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = dimens.md),
        horizontalArrangement = Arrangement.spacedBy(dimens.sm),
    ) {
        items(options) { option ->
            FilterChip(
                selected = option in selected,
                onClick = { onToggle(option) },
                label = { Text(label(option), style = MaterialTheme.typography.labelLarge) },
                shape = CircleShape,
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = option in selected,
                    borderColor = Color.Transparent,
                    selectedBorderColor = Color.Transparent,
                ),
                interactionSource = remember { PressOnlyInteractionSource() },
            )
        }
    }
}

// Ignora hover/foco de mouse ou teclado; só o toque (pressed) gera feedback visual.
// InteractionSource que reporta apenas o estado de pressionado.
private class PressOnlyInteractionSource(
    private val delegate: MutableInteractionSource = MutableInteractionSource(),
) : MutableInteractionSource {
    override val interactions: Flow<Interaction> =
        delegate.interactions.filter { it !is HoverInteraction && it !is FocusInteraction }

    override suspend fun emit(interaction: Interaction) = delegate.emit(interaction)

    override fun tryEmit(interaction: Interaction) = delegate.tryEmit(interaction)
}
