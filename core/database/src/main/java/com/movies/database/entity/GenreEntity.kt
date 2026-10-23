package com.movies.database.entity

import androidx.room.Entity

// Entidade Room que representa genre.
@Entity(tableName = "genre", primaryKeys = ["mediaType", "id"])
data class GenreEntity(
    val mediaType: String,
    val id: Int,
    val name: String,
)
