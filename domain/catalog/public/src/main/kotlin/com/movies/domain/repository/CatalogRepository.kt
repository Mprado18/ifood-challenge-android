package com.movies.domain.repository

import androidx.paging.PagingData
import com.movies.domain.model.Category
import com.movies.domain.model.Genre
import com.movies.domain.model.MediaSummary
import kotlinx.coroutines.flow.Flow

interface CatalogRepository {
    /**
     * Primeiros itens salvos da categoria, usados nos carrosséis.
     * [genres] traz o mesmo gênero para cada tipo de mídia; lista vazia não filtra.
     */
    fun observePreview(
        category: Category,
        genres: List<Genre> = emptyList(),
    ): Flow<List<MediaSummary>>

    fun categoryPagingFlow(
        category: Category,
        genres: List<Genre> = emptyList(),
    ): Flow<PagingData<MediaSummary>>

    /** Online usa a busca remota; offline pesquisa apenas os títulos já salvos. */
    fun searchPagingFlow(
        query: String,
        genres: List<Genre> = emptyList(),
    ): Flow<PagingData<MediaSummary>>

    suspend fun refreshCategory(category: Category): Result<Unit>
}
