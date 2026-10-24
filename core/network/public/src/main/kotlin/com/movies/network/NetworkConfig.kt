package com.movies.network

/** Implementado pelo app, que conhece o token e o ambiente da API. */
interface NetworkConfig {
    val baseUrl: String
    val bearerToken: String
}
