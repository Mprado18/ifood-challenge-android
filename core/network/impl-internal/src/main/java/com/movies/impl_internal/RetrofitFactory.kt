package com.movies.impl_internal

import com.movies.network.NetworkConfig
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

// Fábrica de retrofit.
internal object RetrofitFactory {
    fun json(): Json =
        Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }

    fun okHttp(config: NetworkConfig): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor(AuthInterceptor(config))
            .build()

    fun retrofit(
        client: OkHttpClient,
        json: Json,
        config: NetworkConfig,
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(config.baseUrl)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
}

private const val CONNECT_TIMEOUT_SECONDS = 15L
private const val READ_TIMEOUT_SECONDS = 20L
