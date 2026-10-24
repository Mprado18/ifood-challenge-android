package com.movies.impl_internal.repository

import com.movies.impl_internal.datasource.local.GenreLocalDataSource
import com.movies.impl_internal.datasource.remote.GenreRemoteDataSource
import com.movies.domain.model.Genre
import com.movies.domain.repository.GenreRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// Implementação do repositório de genre, combinando fontes locais e remotas.
internal class GenreRepositoryImpl @Inject constructor(
    private val remote: GenreRemoteDataSource,
    private val local: GenreLocalDataSource,
) : GenreRepository {

    override fun observeGenres(): Flow<List<Genre>> = local.observeAll()

    override suspend fun refreshGenres(): Result<Unit> = runCatchingSuspend {
        local.replaceAll(remote.fetchGenres())
    }
}
