package com.movies.impl_internal.datasource.remote

import com.movies.domain.model.MediaDetail
import com.movies.domain.model.MediaKey

internal interface DetailRemoteDataSource {
    suspend fun fetchDetail(key: MediaKey): MediaDetail
}
