package com.movies.ifood_challenge

import com.movies.network.NetworkConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** O token vem de `local.properties`/ambiente em tempo de build, nunca do código-fonte. */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideNetworkConfig(): NetworkConfig =
        object : NetworkConfig {
            override val baseUrl: String = "https://api.themoviedb.org/"
            override val bearerToken: String = BuildConfig.TMDB_TOKEN
        }
}
