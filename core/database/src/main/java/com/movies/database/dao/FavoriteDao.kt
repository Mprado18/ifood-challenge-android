package com.movies.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.movies.database.entity.FavoriteEntity
import com.movies.database.entity.MediaEntity
import kotlinx.coroutines.flow.Flow

// DAO Room com as consultas de favorite.
@Dao
interface FavoriteDao {
    @Query(
        """
        SELECT m.* FROM media m JOIN favorite f ON m.`key` = f.mediaKey
        ORDER BY f.addedAt DESC
        """,
    )
    fun observeMedia(): Flow<List<MediaEntity>>

    @Query("SELECT mediaKey FROM favorite")
    fun observeKeys(): Flow<List<String>>

    @Upsert
    suspend fun upsert(item: FavoriteEntity)

    @Query("DELETE FROM favorite WHERE mediaKey = :key")
    suspend fun delete(key: String)
}
