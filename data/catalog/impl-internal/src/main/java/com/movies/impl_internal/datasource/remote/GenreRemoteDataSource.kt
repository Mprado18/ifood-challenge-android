package com.movies.impl_internal.datasource.remote

import com.movies.domain.model.Genre

internal interface GenreRemoteDataSource {
    suspend fun fetchGenres(): List<Genre>
}
