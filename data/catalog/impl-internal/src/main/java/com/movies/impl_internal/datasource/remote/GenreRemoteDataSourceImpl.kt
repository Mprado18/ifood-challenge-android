package com.movies.impl_internal.datasource.remote

import com.movies.impl_internal.api.ApiExecutor
import com.movies.impl_internal.api.TmdbApiService
import com.movies.impl_internal.mapper.toGenre
import com.movies.domain.model.Genre
import com.movies.domain.model.MediaType
import javax.inject.Inject

// Implementação da fonte de dados remota (API TMDB) de genre.
internal class GenreRemoteDataSourceImpl @Inject constructor(
    private val api: TmdbApiService,
    private val executor: ApiExecutor,
) : GenreRemoteDataSource {

    override suspend fun fetchGenres(): List<Genre> {
        val movie = executor.execute { api.movieGenres() }.genres.map { it.toGenre(MediaType.MOVIE) }
        val tv = executor.execute { api.tvGenres() }.genres.map { it.toGenre(MediaType.TV) }
        return movie + tv
    }
}
