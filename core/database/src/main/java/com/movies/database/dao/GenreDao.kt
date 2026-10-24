package com.movies.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.movies.database.entity.GenreEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GenreDao {
    @Query("SELECT * FROM genre ORDER BY name")
    fun observeAll(): Flow<List<GenreEntity>>

    @Query("DELETE FROM genre WHERE mediaType = :mediaType")
    suspend fun clear(mediaType: String)

    @Upsert
    suspend fun upsertAll(items: List<GenreEntity>)
}
