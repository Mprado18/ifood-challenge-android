package com.movies.impl_internal.datasource.local

import com.movies.common.DispatcherProvider
import com.movies.database.TransactionRunner
import com.movies.database.dao.DetailDao
import com.movies.database.dao.MediaDao
import com.movies.impl_internal.mapper.toDetail
import com.movies.impl_internal.mapper.toEntity
import com.movies.impl_internal.mapper.toStoredKey
import com.movies.impl_internal.mapper.toSummary
import com.movies.domain.model.MediaDetail
import com.movies.domain.model.MediaKey
import com.movies.domain.model.MediaSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

// Implementação da fonte de dados local (Room) de detail.
internal class DetailLocalDataSourceImpl @Inject constructor(
    private val detailDao: DetailDao,
    private val mediaDao: MediaDao,
    private val transactions: TransactionRunner,
    private val dispatchers: DispatcherProvider,
) : DetailLocalDataSource {

    override fun observeDetail(key: MediaKey): Flow<MediaDetail?> =
        combine(
            detailDao.observe(key.toStoredKey()),
            mediaDao.observe(key.toStoredKey()),
        ) { detail, media ->
            if (detail != null && media != null) detail.toDetail(media.toSummary()) else null
        }

    override fun observeSummary(key: MediaKey): Flow<MediaSummary?> =
        mediaDao.observe(key.toStoredKey()).map { it?.toSummary() }

    override suspend fun save(detail: MediaDetail) = withContext(dispatchers.io) {
        transactions.run {
            mediaDao.upsertAll(listOf(detail.summary.toEntity()))
            detailDao.upsert(detail.toEntity(System.currentTimeMillis()))
        }
    }
}
