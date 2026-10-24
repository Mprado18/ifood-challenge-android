package com.movies.home

import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute


/** Grade paginada de uma categoria; [category] é o nome do enum `Category`. */
@Serializable
data class CatalogRoute(val category: String, val genre: String = "")
