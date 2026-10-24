package com.movies.impl_internal.datasource.local

import com.movies.common.DispatcherProvider
import com.movies.database.TransactionRunner
import com.movies.database.dao.GenreDao
import com.movies.impl_internal.mapper.toEntity
import com.movies.impl_internal.mapper.toGenre
import com.movies.domain.model.Genre
import com.movies.domain.model.MediaType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class GenreLocalDataSourceImpl @Inject constructor(
    private val genreDao: GenreDao,
    private val transactions: TransactionRunner,
    private val dispatchers: DispatcherProvider,
) : GenreLocalDataSource {

    override fun observeAll(): Flow<List<Genre>> =
        genreDao.observeAll().map { list -> list.map { it.toGenre() } }

    override suspend fun replaceAll(genres: List<Genre>) = withContext(dispatchers.io) {
        transactions.run {
            MediaType.entries.forEach { genreDao.clear(it.apiValue) }
            genreDao.upsertAll(genres.map { it.toEntity() })
        }
    }
}
