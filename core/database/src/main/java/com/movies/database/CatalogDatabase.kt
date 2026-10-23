package com.movies.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.project.movies.core.database.dao.DetailDao
import com.project.movies.core.database.dao.FavoriteDao
import com.project.movies.core.database.dao.GenreDao
import com.project.movies.core.database.dao.ListDao
import com.project.movies.core.database.dao.MediaDao
import com.project.movies.core.database.entity.DetailEntity
import com.project.movies.core.database.entity.FavoriteEntity
import com.project.movies.core.database.entity.GenreEntity
import com.project.movies.core.database.entity.ListItemEntity
import com.project.movies.core.database.entity.MediaEntity
import com.project.movies.core.database.entity.RemoteKeyEntity

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
// Banco Room do catálogo: mídias, listas, favoritos, gêneros e detalhes.
abstract class CatalogDatabase : RoomDatabase() {
    abstract fun mediaDao(): MediaDao
    abstract fun listDao(): ListDao
    abstract fun genreDao(): GenreDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun detailDao(): DetailDao
}
