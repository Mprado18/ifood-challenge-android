package com.movies.impl_internal.datasource.local

import androidx.paging.PagingSource
import com.movies.common.DispatcherProvider
import com.movies.database.TransactionRunner
import com.movies.database.dao.ListDao
import com.movies.database.dao.MediaDao
import com.movies.database.entity.ListItemEntity
import com.movies.database.entity.MediaEntity
import com.movies.database.entity.RemoteKeyEntity
import com.movies.impl_internal.common.toFilterArgs
import com.movies.impl_internal.mapper.toEntity
import com.movies.impl_internal.mapper.toStoredKey
import com.movies.impl_internal.common.RemotePage
import com.movies.domain.model.Genre
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val PREVIEW_SIZE = 20
// Posição = página * stride + índice na página, mantendo a ordem da API entre páginas.
private const val PAGE_ORDER_STRIDE = 1000
private const val CACHE_TTL_MS = 30 * 60 * 1000L

// Implementação da fonte de dados local (Room) de catalog.
internal class CatalogLocalDataSourceImpl @Inject constructor(
    private val listDao: ListDao,
    private val mediaDao: MediaDao,
    private val transactions: TransactionRunner,
    private val dispatchers: DispatcherProvider,
) : CatalogLocalDataSource {

    override fun observePreview(
        scope: String,
        genres: List<Genre>,
    ): Flow<List<MediaEntity>> {
        val args = genres.toFilterArgs()
        return listDao.preview(scope, args.filtered, args.movie, args.tv, PREVIEW_SIZE)
    }

    override fun pagingSource(
        scope: String,
        genres: List<Genre>,
    ): PagingSource<Int, MediaEntity> {
        val args = genres.toFilterArgs()
        return listDao.paging(scope, args.filtered, args.movie, args.tv)
    }

    override fun searchLocalPagingSource(
        query: String,
        genres: List<Genre>,
    ): PagingSource<Int, MediaEntity> {
        val args = genres.toFilterArgs()
        return mediaDao.searchLocal(query.escapeLike(), args.filtered, args.movie, args.tv)
    }

    override suspend fun storePage(
        scope: String,
        page: RemotePage,
        replace: Boolean,
    ) = withContext(dispatchers.io) {
        transactions.run {
            if (replace) listDao.clear(scope)
            mediaDao.upsertAll(page.items.map { it.toEntity() })
            listDao.upsertAll(
                page.items.mapIndexed { index, item ->
                    ListItemEntity(scope, item.key.toStoredKey(), page.page * PAGE_ORDER_STRIDE + index)
                },
            )
            val next = if (page.isLast) null else page.page + 1
            listDao.upsertRemoteKey(RemoteKeyEntity(scope, next, System.currentTimeMillis()))
        }
    }

    override suspend fun nextPage(scope: String): Int? = listDao.remoteKey(scope)?.nextPage

    override suspend fun isFresh(scope: String): Boolean {
        val key = listDao.remoteKey(scope) ?: return false
        return System.currentTimeMillis() - key.updatedAt < CACHE_TTL_MS
    }

    private fun String.escapeLike() = trim()
        .replace("\\", "\\\\")
        .replace("%", "\\%")
        .replace("_", "\\_")
}
