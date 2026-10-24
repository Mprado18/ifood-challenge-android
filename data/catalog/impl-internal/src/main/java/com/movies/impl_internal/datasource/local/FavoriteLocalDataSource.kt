package com.movies.impl_internal.datasource.local

import com.movies.domain.model.MediaKey
import com.movies.domain.model.MediaSummary
import kotlinx.coroutines.flow.Flow

internal interface FavoriteLocalDataSource {
    fun observeAll(): Flow<List<MediaSummary>>

    fun observeKeys(): Flow<Set<MediaKey>>

    suspend fun add(media: MediaSummary)

    suspend fun remove(key: MediaKey)
}
