package com.movies.database.entity

import androidx.room.Entity

@Entity(tableName = "genre", primaryKeys = ["mediaType", "id"])
data class GenreEntity(
    val mediaType: String,
    val id: Int,
    val name: String,
)
