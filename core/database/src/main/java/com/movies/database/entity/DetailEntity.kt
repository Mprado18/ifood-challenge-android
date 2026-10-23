package com.movies.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Entidade Room que representa detail.
@Entity(tableName = "detail")
data class DetailEntity(
    @PrimaryKey val mediaKey: String,
    val tagline: String?,
    val status: String?,
    val runtimeMinutes: Int?,
    val seasons: Int?,
    val episodes: Int?,
    val genresJson: String,
    val castJson: String,
    val fetchedAt: Long,
)
