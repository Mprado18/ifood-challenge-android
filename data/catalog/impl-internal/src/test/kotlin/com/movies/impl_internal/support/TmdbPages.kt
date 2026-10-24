package com.movies.impl_internal.support

// Gera o JSON de uma página de filmes para o servidor falso.
/** Página de filmes populares no formato JSON do TMDB. */
fun moviePageJson(page: Int, totalPages: Int, ids: List<Int>, genreId: Int = 28) =
    """{"page":$page,"total_pages":$totalPages,"results":[${
        ids.joinToString(",") {
            """{"id":$it,"title":"Filme $it","overview":"o","genre_ids":[$genreId],"vote_average":7.5,"vote_count":$it}"""
        }
    }]}"""

fun tvPageJson(ids: List<Int>) =
    """{"page":1,"total_pages":1,"results":[${
        ids.joinToString(",") {
            """{"id":$it,"name":"Série $it","overview":"o","genre_ids":[18],"vote_average":8.0,"vote_count":$it}"""
        }
    }]}"""
