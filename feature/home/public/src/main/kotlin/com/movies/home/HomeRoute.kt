package com.movies.home

import kotlinx.serialization.Serializable

// Definição da rota de navegação de home.
@Serializable
data object HomeRoute

// Definição da rota de navegação de catalog.
/** Grade paginada de uma categoria; [category] é o nome do enum `Category`. */
@Serializable
data class CatalogRoute(val category: String, val genre: String = "")
