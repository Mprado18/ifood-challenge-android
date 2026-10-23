package com.movies.domain.repository

import com.movies.domain.model.MediaKey
import com.movies.domain.model.MediaSummary
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun observeFavorites(): Flow<List<MediaSummary>>

    fun observeFavoriteKeys(): Flow<Set<MediaKey>>

    suspend fun setFavorite(
        media: MediaSummary,
        isFavorite: Boolean,
    ): Result<Unit>
}
