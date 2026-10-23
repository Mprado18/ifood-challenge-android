package com.movies.public.repository

import com.movies.public.model.Genre
import kotlinx.coroutines.flow.Flow

interface GenreRepository {
    fun observeGenres(): Flow<List<Genre>>

    suspend fun refreshGenres(): Result<Unit>
}
