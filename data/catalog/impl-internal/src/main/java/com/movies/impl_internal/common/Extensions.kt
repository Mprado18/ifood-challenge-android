package com.movies.impl_internal.common

import com.movies.domain.model.Category

// Converte a categoria no escopo usado para chaves remotas.
internal fun Category.toScope() = name

internal fun searchScope(query: String) = "search:${query.trim().lowercase()}"
