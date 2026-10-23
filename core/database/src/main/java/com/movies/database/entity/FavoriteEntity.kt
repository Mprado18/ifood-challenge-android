package com.movies.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Entidade Room que representa favorite.
@Entity(tableName = "favorite")
data class FavoriteEntity(
    @PrimaryKey val mediaKey: String,
    val addedAt: Long,
)
