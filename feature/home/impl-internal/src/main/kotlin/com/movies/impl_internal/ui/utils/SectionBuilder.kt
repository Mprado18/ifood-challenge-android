package com.movies.impl_internal.ui.utils

import com.movies.domain.model.Category
import com.movies.domain.model.MediaSummary
import com.movies.impl_internal.ui.home.SectionState
import com.movies.impl_internal.ui.home.SectionStatus

internal sealed interface RefreshStatus {
    data object Loading : RefreshStatus
    data object Done : RefreshStatus
    data class Failed(val message: String) : RefreshStatus
}

/** Conteúdo local sempre vence; só sem itens o estado da atualização decide entre loading, erro e vazio. */
internal fun buildSection(category: Category, items: List<MediaSummary>, refresh: RefreshStatus): SectionState {
    val status = when {
        items.isNotEmpty() -> SectionStatus.Content
        refresh is RefreshStatus.Loading -> SectionStatus.Loading
        refresh is RefreshStatus.Failed -> SectionStatus.Error(refresh.message)
        else -> SectionStatus.Empty
    }
    return SectionState(category, items, status)
}
