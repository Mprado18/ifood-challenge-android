package com.movies.impl_internal.support

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.movies.common.DispatcherProvider
import com.movies.database.CatalogDatabase
import com.movies.database.TransactionRunner
import com.movies.impl_internal.datasource.local.CatalogLocalDataSourceImpl
import com.movies.impl_internal.datasource.local.DetailLocalDataSourceImpl
import com.movies.impl_internal.datasource.local.FavoriteLocalDataSourceImpl
import com.movies.impl_internal.datasource.local.GenreLocalDataSourceImpl
import com.movies.impl_internal.datasource.remote.CatalogRemoteDataSourceImpl
import com.movies.impl_internal.datasource.remote.DetailRemoteDataSourceImpl
import com.movies.impl_internal.datasource.remote.GenreRemoteDataSourceImpl
import com.movies.impl_internal.repository.CatalogRepositoryImpl
import com.movies.impl_internal.repository.DetailRepositoryImpl
import com.movies.impl_internal.repository.FavoriteRepositoryImpl
import com.movies.impl_internal.repository.GenreRepositoryImpl
import com.movies.testing.ConnectivityObserverStub
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.junit.rules.ExternalResource

class CatalogGraphRule(
    private val tmdb: TmdbServerRule,
) : ExternalResource() {
    val connectivity = ConnectivityObserverStub()

    internal lateinit var database: CatalogDatabase
        private set
    internal lateinit var catalog: CatalogRepositoryImpl
        private set
    internal lateinit var genres: GenreRepositoryImpl
        private set
    internal lateinit var favorites: FavoriteRepositoryImpl
        private set
    internal lateinit var details: DetailRepositoryImpl
        private set

    override fun before() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, CatalogDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        val dispatchers = object : DispatcherProvider {
            override val main: CoroutineDispatcher = Dispatchers.Unconfined
            override val io: CoroutineDispatcher = Dispatchers.IO
            override val default: CoroutineDispatcher = Dispatchers.Default
        }
        val transactions = TransactionRunner(database)
        catalog = CatalogRepositoryImpl(
            CatalogRemoteDataSourceImpl(tmdb.api, tmdb.executor),
            CatalogLocalDataSourceImpl(database.listDao(), database.mediaDao(), transactions, dispatchers),
            connectivity,
        )
        genres = GenreRepositoryImpl(
            GenreRemoteDataSourceImpl(tmdb.api, tmdb.executor),
            GenreLocalDataSourceImpl(database.genreDao(), transactions, dispatchers),
        )
        favorites = FavoriteRepositoryImpl(
            FavoriteLocalDataSourceImpl(database.favoriteDao(), database.mediaDao(), transactions, dispatchers),
        )
        details = DetailRepositoryImpl(
            DetailRemoteDataSourceImpl(tmdb.api, tmdb.executor),
            DetailLocalDataSourceImpl(database.detailDao(), database.mediaDao(), transactions, dispatchers),
        )
    }

    override fun after() {
        database.close()
    }
}
