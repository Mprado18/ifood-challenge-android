package com.movies.impl_internal.datasource.remote

import com.movies.impl_internal.api.ApiExecutor
import com.movies.impl_internal.api.TmdbApiService
import com.movies.impl_internal.mapper.toDetail
import com.movies.domain.model.MediaDetail
import com.movies.domain.model.MediaKey
import com.movies.domain.model.MediaType
import javax.inject.Inject

internal class DetailRemoteDataSourceImpl @Inject constructor(
    private val api: TmdbApiService,
    private val executor: ApiExecutor,
) : DetailRemoteDataSource {

    override suspend fun fetchDetail(key: MediaKey): MediaDetail = when (key.type) {
        MediaType.MOVIE -> executor.execute { api.movieDetail(key.id) }.toDetail()
        MediaType.TV -> executor.execute { api.tvDetail(key.id) }.toDetail()
    }
}
