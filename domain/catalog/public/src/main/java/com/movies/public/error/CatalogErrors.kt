package com.movies.public.error

/** Mensagem em pt-BR adequada para exibição ao usuário. */
fun Throwable.toUserMessage(): String = when (this) {
    is CatalogException.Network -> "Sem conexão. Mostrando o conteúdo salvo."
    is CatalogException.MissingToken -> "Configure o token do TMDB para carregar conteúdo."
    is CatalogException.Unauthorized -> "Token do TMDB inválido ou sem permissão."
    is CatalogException.RateLimited -> "Muitas requisições. Tente novamente em instantes."
    is CatalogException.Server -> "O TMDB está indisponível no momento."
    is CatalogException.Contract -> "Resposta inesperada do TMDB."
    else -> "Algo deu errado. Tente novamente."
}
