package com.movies.impl_internal.datasource.local

import com.movies.domain.model.Genre
import kotlinx.coroutines.flow.Flow

// Contrato da fonte de dados local de genre.
internal interface GenreLocalDataSource {
    fun observeAll(): Flow<List<Genre>>

    suspend fun replaceAll(genres: List<Genre>)
}
