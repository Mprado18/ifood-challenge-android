package com.movies.impl_internal

import com.movies.network.NetworkConfig
import okhttp3.Interceptor
import okhttp3.Response

// Interceptor OkHttp de auth.
internal class AuthInterceptor(
    private val config: NetworkConfig,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request =
            chain.request().newBuilder()
                .header("Authorization", "Bearer ${config.bearerToken}")
                .header("Accept", "application/json")
                .build()
        return chain.proceed(request)
    }
}
