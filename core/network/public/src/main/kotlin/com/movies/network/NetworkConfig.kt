package com.movies.network

// Configuração de rede (URL base, token e flags) fornecida pelo app.

/** Implementado pelo app, que conhece o token e o ambiente da API. */
interface NetworkConfig {
    val baseUrl: String
    val bearerToken: String
}
