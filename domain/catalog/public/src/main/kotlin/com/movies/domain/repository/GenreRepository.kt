package com.movies.domain.repository

import com.movies.domain.model.Genre
import kotlinx.coroutines.flow.Flow

interface GenreRepository {
    fun observeGenres(): Flow<List<Genre>>

    suspend fun refreshGenres(): Result<Unit>
}
