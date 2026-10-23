package com.movies.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.project.movies.core.database.entity.DetailEntity
import kotlinx.coroutines.flow.Flow

// DAO Room com as consultas de detail.
@Dao
interface DetailDao {
    @Query("SELECT * FROM detail WHERE mediaKey = :key")
    fun observe(key: String): Flow<DetailEntity?>

    @Upsert
    suspend fun upsert(item: DetailEntity)
}
