package com.movies.impl_internal.datasource.local

import androidx.paging.PagingSource
import com.movies.database.entity.MediaEntity
import com.movies.impl_internal.common.RemotePage
import com.movies.domain.model.Genre
import kotlinx.coroutines.flow.Flow

// Contrato da fonte de dados local de catalog.
internal interface CatalogLocalDataSource {
    fun observePreview(
        scope: String,
        genres: List<Genre>,
    ): Flow<List<MediaEntity>>

    fun pagingSource(
        scope: String,
        genres: List<Genre>,
    ): PagingSource<Int, MediaEntity>

    fun searchLocalPagingSource(
        query: String,
        genres: List<Genre>,
    ): PagingSource<Int, MediaEntity>

    suspend fun storePage(
        scope: String,
        page: RemotePage,
        replace: Boolean,
    )

    suspend fun nextPage(scope: String): Int?

    suspend fun isFresh(scope: String): Boolean
}
