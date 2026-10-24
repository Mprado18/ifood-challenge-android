package com.movies.impl_internal.common

import com.movies.domain.model.Genre
import com.movies.domain.model.MediaType

private const val NO_GENRE_ID = -1

// Argumentos normalizados do filtro de gênero para consulta no DAO.
internal class GenreFilterArgs(
    val filtered: Int,
    val movie: Int,
    val tv: Int,
)

internal fun List<Genre>.toFilterArgs() = GenreFilterArgs(
    filtered = if (isEmpty()) 0 else 1,
    movie = firstOrNull { it.mediaType == MediaType.MOVIE }?.id ?: NO_GENRE_ID,
    tv = firstOrNull { it.mediaType == MediaType.TV }?.id ?: NO_GENRE_ID,
)
