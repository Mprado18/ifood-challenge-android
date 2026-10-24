package com.movies.impl_internal.ui.home

import com.movies.domain.model.Category
import com.movies.domain.model.MediaKey
import com.movies.domain.model.MediaSummary

internal data class HomeUiState(
    val query: String = "",
    val genres: List<String> = emptyList(),
    val selectedGenre: String? = null,
    val sections: List<SectionState> = Category.entries.map { SectionState(it) },
    val favorites: Set<MediaKey> = emptySet(),
    val isOffline: Boolean = false,
) {
    val isSearching: Boolean get() = query.isNotBlank()
}

internal data class SectionState(
    val category: Category,
    val items: List<MediaSummary> = emptyList(),
    val status: SectionStatus = SectionStatus.Loading,
)

internal sealed interface SectionStatus {
    data object Loading : SectionStatus
    data object Content : SectionStatus
    data object Empty : SectionStatus
    data class Error(val message: String) : SectionStatus
}
