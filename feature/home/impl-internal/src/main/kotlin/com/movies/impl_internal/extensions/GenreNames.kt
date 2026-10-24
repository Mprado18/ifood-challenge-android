package com.movies.impl_internal.extensions

import com.movies.domain.model.Genre

/** O gênero é escolhido pelo nome; filmes e séries têm ids diferentes para o mesmo nome. */
internal fun List<Genre>.matchingName(name: String?): List<Genre> =
    if (name == null) emptyList() else filter { it.name == name }
