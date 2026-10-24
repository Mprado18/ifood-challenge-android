package com.movies.database.entity

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "list_item",
    primaryKeys = ["scope", "mediaKey"],
    indices = [Index("scope", "position")],
)
data class ListItemEntity(
    val scope: String,
    val mediaKey: String,
    val position: Int,
)
