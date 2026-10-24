package com.movies.impl_internal.repository

import com.movies.impl_internal.datasource.local.DetailLocalDataSource
import com.movies.impl_internal.datasource.remote.DetailRemoteDataSource
import com.movies.domain.model.MediaDetail
import com.movies.domain.model.MediaKey
import com.movies.domain.model.MediaSummary
import com.movies.domain.repository.DetailRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class DetailRepositoryImpl @Inject constructor(
    private val remote: DetailRemoteDataSource,
    private val local: DetailLocalDataSource,
) : DetailRepository {

    override fun observeDetail(key: MediaKey): Flow<MediaDetail?> = local.observeDetail(key)

    override fun observeSummary(key: MediaKey): Flow<MediaSummary?> = local.observeSummary(key)

    override suspend fun refreshDetail(key: MediaKey): Result<Unit> = runCatchingSuspend {
        local.save(remote.fetchDetail(key))
    }
}
