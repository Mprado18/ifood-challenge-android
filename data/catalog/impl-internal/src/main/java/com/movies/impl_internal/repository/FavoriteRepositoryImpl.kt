package com.movies.impl_internal.repository

import com.movies.impl_internal.datasource.local.FavoriteLocalDataSource
import com.movies.domain.model.MediaKey
import com.movies.domain.model.MediaSummary
import com.movies.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// Implementação do repositório de favorite, combinando fontes locais e remotas.
internal class FavoriteRepositoryImpl @Inject constructor(
    private val local: FavoriteLocalDataSource,
) : FavoriteRepository {

    override fun observeFavorites(): Flow<List<MediaSummary>> = local.observeAll()

    override fun observeFavoriteKeys(): Flow<Set<MediaKey>> = local.observeKeys()

    override suspend fun setFavorite(
        media: MediaSummary,
        isFavorite: Boolean,
    ): Result<Unit> = runCatchingSuspend {
        if (isFavorite) local.add(media) else local.remove(media.key)
    }
}
