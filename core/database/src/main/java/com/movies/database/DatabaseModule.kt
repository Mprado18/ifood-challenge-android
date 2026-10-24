package com.movies.database

import android.content.Context
import androidx.room.Room
import com.movies.database.dao.DetailDao
import com.movies.database.dao.FavoriteDao
import com.movies.database.dao.GenreDao
import com.movies.database.dao.ListDao
import com.movies.database.dao.MediaDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val DATABASE_NAME = "catalog.db"

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
