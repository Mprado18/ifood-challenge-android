package com.movies.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.project.movies.core.database.entity.MediaEntity
import kotlinx.coroutines.flow.Flow

// DAO Room com as consultas de media.
@Dao
interface MediaDao {
    @Upsert
    suspend fun upsertAll(items: List<MediaEntity>)

    @Query("SELECT * FROM media WHERE `key` = :key")
    fun observe(key: String): Flow<MediaEntity?>

    @Query(
        """
        SELECT m.* FROM media m
        WHERE m.title LIKE '%' || :query || '%' ESCAPE '\' AND $GENRE_FILTER
        ORDER BY m.voteCount DESC
        """,
    )
    fun searchLocal(
        query: String,
        filtered: Int,
        movieGenre: Int,
        tvGenre: Int,
    ): PagingSource<Int, MediaEntity>
}
