package com.movies.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_key")
data class RemoteKeyEntity(
    @PrimaryKey val scope: String,
    val nextPage: Int?,
    val updatedAt: Long,
)
