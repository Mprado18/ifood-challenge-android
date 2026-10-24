package com.movies.impl_internal.common

import com.movies.domain.model.Category

internal fun Category.toScope() = name

internal fun searchScope(query: String) = "search:${query.trim().lowercase()}"
