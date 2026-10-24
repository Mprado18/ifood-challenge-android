package com.movies.impl_internal.datasource.remote

import com.movies.domain.model.Genre

// Contrato da fonte de dados remota de genre.
internal interface GenreRemoteDataSource {
    suspend fun fetchGenres(): List<Genre>
}
