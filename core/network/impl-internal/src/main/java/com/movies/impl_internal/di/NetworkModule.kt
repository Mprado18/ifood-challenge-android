package com.movies.impl_internal.di

import com.movies.impl_internal.RetrofitFactory
import com.movies.public.NetworkConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

// Módulo Hilt que provê as dependências de network.
@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {
    @Provides
    @Singleton
    fun provideJson(): Json = RetrofitFactory.json()

    @Provides
    @Singleton
    fun provideOkHttpClient(config: NetworkConfig): OkHttpClient = RetrofitFactory.okHttp(config)

    @Provides
    @Singleton
    fun provideRetrofit(
        client: OkHttpClient,
        json: Json,
        config: NetworkConfig,
    ): Retrofit = RetrofitFactory.retrofit(client, json, config)
}
