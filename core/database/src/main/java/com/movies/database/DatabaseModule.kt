package com.movies.database

import android.content.Context
import androidx.room.Room
import com.project.movies.core.database.dao.DetailDao
import com.project.movies.core.database.dao.FavoriteDao
import com.project.movies.core.database.dao.GenreDao
import com.project.movies.core.database.dao.ListDao
import com.project.movies.core.database.dao.MediaDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val DATABASE_NAME = "catalog.db"

// Módulo Hilt que provê as dependências de database.
@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): CatalogDatabase =
        Room.databaseBuilder(context, CatalogDatabase::class.java, DATABASE_NAME).build()

    @Provides
    fun provideMediaDao(database: CatalogDatabase): MediaDao = database.mediaDao()

    @Provides
    fun provideListDao(database: CatalogDatabase): ListDao = database.listDao()

    @Provides
    fun provideGenreDao(database: CatalogDatabase): GenreDao = database.genreDao()

    @Provides
    fun provideFavoriteDao(database: CatalogDatabase): FavoriteDao = database.favoriteDao()

    @Provides
    fun provideDetailDao(database: CatalogDatabase): DetailDao = database.detailDao()
}
