package com.movies.impl_internal.datasource.local

import com.movies.common.DispatcherProvider
import com.movies.database.TransactionRunner
import com.movies.database.dao.FavoriteDao
import com.movies.database.dao.MediaDao
import com.movies.database.entity.FavoriteEntity
import com.movies.impl_internal.mapper.toEntity
import com.movies.impl_internal.mapper.toMediaKeyOrNull
import com.movies.impl_internal.mapper.toStoredKey
import com.movies.impl_internal.mapper.toSummary
import com.movies.domain.model.MediaKey
import com.movies.domain.model.MediaSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class FavoriteLocalDataSourceImpl @Inject constructor(
    private val favoriteDao: FavoriteDao,
    private val mediaDao: MediaDao,
    private val transactions: TransactionRunner,
    private val dispatchers: DispatcherProvider,
) : FavoriteLocalDataSource {

    override fun observeAll(): Flow<List<MediaSummary>> =
        favoriteDao.observeMedia().map { list -> list.map { it.toSummary() } }

    override fun observeKeys(): Flow<Set<MediaKey>> =
        favoriteDao.observeKeys().map { keys -> keys.mapNotNull { it.toMediaKeyOrNull() }.toSet() }

    override suspend fun add(media: MediaSummary) = withContext(dispatchers.io) {
        transactions.run {
            mediaDao.upsertAll(listOf(media.toEntity()))
            favoriteDao.upsert(FavoriteEntity(media.key.toStoredKey(), System.currentTimeMillis()))
        }
    }

    override suspend fun remove(key: MediaKey) = withContext(dispatchers.io) {
        favoriteDao.delete(key.toStoredKey())
    }
}
