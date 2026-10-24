package com.movies.impl_internal.datasource.local

import com.movies.domain.model.MediaDetail
import com.movies.domain.model.MediaKey
import com.movies.domain.model.MediaSummary
import kotlinx.coroutines.flow.Flow

// Contrato da fonte de dados local de detail.
internal interface DetailLocalDataSource {
    fun observeDetail(key: MediaKey): Flow<MediaDetail?>

    fun observeSummary(key: MediaKey): Flow<MediaSummary?>

    suspend fun save(detail: MediaDetail)
}
