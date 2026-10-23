package com.movies.public.repository

import com.movies.public.model.MediaKey
import com.movies.public.model.MediaSummary
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun observeFavorites(): Flow<List<MediaSummary>>

    fun observeFavoriteKeys(): Flow<Set<MediaKey>>

    suspend fun setFavorite(
        media: MediaSummary,
        isFavorite: Boolean,
    ): Result<Unit>
}
