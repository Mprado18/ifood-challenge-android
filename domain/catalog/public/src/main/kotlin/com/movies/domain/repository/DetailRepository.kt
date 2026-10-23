package com.movies.domain.repository

import com.movies.domain.model.MediaDetail
import com.movies.domain.model.MediaKey
import com.movies.domain.model.MediaSummary
import kotlinx.coroutines.flow.Flow

interface DetailRepository {
    /** Emite null enquanto o detalhe completo nunca foi baixado; [observeSummary] serve de fallback. */
    fun observeDetail(key: MediaKey): Flow<MediaDetail?>

    fun observeSummary(key: MediaKey): Flow<MediaSummary?>

    suspend fun refreshDetail(key: MediaKey): Result<Unit>
}
