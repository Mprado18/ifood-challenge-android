package com.movies.public.error

sealed class CatalogException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    class Network(cause: Throwable) : CatalogException("Falha de rede", cause)

    class MissingToken : CatalogException("Token do TMDB não configurado")

    class Unauthorized(val code: Int) : CatalogException("Acesso negado pelo TMDB ($code)")

    class RateLimited : CatalogException("Limite de requisições do TMDB atingido")

    class Server(val code: Int) : CatalogException("Erro no servidor do TMDB ($code)")

    class Contract(cause: Throwable) : CatalogException("Resposta inesperada do TMDB", cause)
}
