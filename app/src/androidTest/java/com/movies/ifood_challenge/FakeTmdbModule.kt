package com.movies.ifood_challenge

import com.movies.network.NetworkConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

/** Troca a configuração de produção por uma que aponta para o [TmdbServerStub]. */
@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [AppModule::class])
object FakeTmdbModule {
    @Provides
    @Singleton
    fun provideNetworkConfig(): NetworkConfig = object : NetworkConfig {
        override val baseUrl: String = TmdbServerStub.baseUrl
        override val bearerToken: String = "token-e2e"
    }
}
