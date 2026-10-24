package com.movies.impl_internal.di

import com.movies.impl_internal.api.TmdbApiService
import com.movies.impl_internal.datasource.local.CatalogLocalDataSource
import com.movies.impl_internal.datasource.local.CatalogLocalDataSourceImpl
import com.movies.impl_internal.datasource.local.DetailLocalDataSource
import com.movies.impl_internal.datasource.local.DetailLocalDataSourceImpl
import com.movies.impl_internal.datasource.local.FavoriteLocalDataSource
import com.movies.impl_internal.datasource.local.FavoriteLocalDataSourceImpl
import com.movies.impl_internal.datasource.local.GenreLocalDataSource
import com.movies.impl_internal.datasource.local.GenreLocalDataSourceImpl
import com.movies.impl_internal.datasource.remote.CatalogRemoteDataSource
import com.movies.impl_internal.datasource.remote.CatalogRemoteDataSourceImpl
import com.movies.impl_internal.datasource.remote.DetailRemoteDataSource
import com.movies.impl_internal.datasource.remote.DetailRemoteDataSourceImpl
import com.movies.impl_internal.datasource.remote.GenreRemoteDataSource
import com.movies.impl_internal.datasource.remote.GenreRemoteDataSourceImpl
import com.movies.impl_internal.repository.CatalogRepositoryImpl
import com.movies.impl_internal.repository.DetailRepositoryImpl
import com.movies.impl_internal.repository.FavoriteRepositoryImpl
import com.movies.impl_internal.repository.GenreRepositoryImpl
import com.movies.domain.repository.CatalogRepository
import com.movies.domain.repository.DetailRepository
import com.movies.domain.repository.FavoriteRepository
import com.movies.domain.repository.GenreRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

// Módulo Hilt que provê as dependências de catalog data.
@Module
@InstallIn(SingletonComponent::class)
internal abstract class CatalogDataModule {

    @Binds
    abstract fun catalogRemoteDataSource(impl: CatalogRemoteDataSourceImpl): CatalogRemoteDataSource

    @Binds
    abstract fun genreRemoteDataSource(impl: GenreRemoteDataSourceImpl): GenreRemoteDataSource

    @Binds
    abstract fun detailRemoteDataSource(impl: DetailRemoteDataSourceImpl): DetailRemoteDataSource

    @Binds
    abstract fun catalogLocalDataSource(impl: CatalogLocalDataSourceImpl): CatalogLocalDataSource

    @Binds
    abstract fun genreLocalDataSource(impl: GenreLocalDataSourceImpl): GenreLocalDataSource

    @Binds
    abstract fun favoriteLocalDataSource(impl: FavoriteLocalDataSourceImpl): FavoriteLocalDataSource

    @Binds
    abstract fun detailLocalDataSource(impl: DetailLocalDataSourceImpl): DetailLocalDataSource

    @Binds
    @Singleton
    abstract fun catalogRepository(impl: CatalogRepositoryImpl): CatalogRepository

    @Binds
    @Singleton
    abstract fun genreRepository(impl: GenreRepositoryImpl): GenreRepository

    @Binds
    @Singleton
    abstract fun favoriteRepository(impl: FavoriteRepositoryImpl): FavoriteRepository

    @Binds
    @Singleton
    abstract fun detailRepository(impl: DetailRepositoryImpl): DetailRepository

    companion object {
        @Provides
        @Singleton
        fun tmdbApiService(retrofit: Retrofit): TmdbApiService = retrofit.create(TmdbApiService::class.java)
    }
}
