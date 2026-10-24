package com.movies.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.movies.database.dao.DetailDao
import com.movies.database.dao.FavoriteDao
import com.movies.database.dao.GenreDao
import com.movies.database.dao.ListDao
import com.movies.database.dao.MediaDao
import com.movies.database.entity.DetailEntity
import com.movies.database.entity.FavoriteEntity
import com.movies.database.entity.GenreEntity
import com.movies.database.entity.ListItemEntity
import com.movies.database.entity.MediaEntity
import com.movies.database.entity.RemoteKeyEntity

@Database(
    entities = [
        MediaEntity::class,
        ListItemEntity::class,
        RemoteKeyEntity::class,
        GenreEntity::class,
        FavoriteEntity::class,
        DetailEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class CatalogDatabase : RoomDatabase() {
    abstract fun mediaDao(): MediaDao
    abstract fun listDao(): ListDao
    abstract fun genreDao(): GenreDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun detailDao(): DetailDao
}
