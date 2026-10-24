package com.movies.impl_internal.datasource.remote

import com.movies.domain.model.MediaDetail
import com.movies.domain.model.MediaKey

// Contrato da fonte de dados remota de detail.
internal interface DetailRemoteDataSource {
    suspend fun fetchDetail(key: MediaKey): MediaDetail
}
