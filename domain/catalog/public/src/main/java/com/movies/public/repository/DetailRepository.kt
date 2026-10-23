package com.movies.public.repository

import com.movies.public.model.MediaDetail
import com.movies.public.model.MediaKey
import com.movies.public.model.MediaSummary
import kotlinx.coroutines.flow.Flow

interface DetailRepository {
    /** Emite null enquanto o detalhe completo nunca foi baixado; [observeSummary] serve de fallback. */
    fun observeDetail(key: MediaKey): Flow<MediaDetail?>

    fun observeSummary(key: MediaKey): Flow<MediaSummary?>

    suspend fun refreshDetail(key: MediaKey): Result<Unit>
}
