package com.movies.detail

import kotlinx.serialization.Serializable

// Definição da rota de navegação de detail.
@Serializable
data class DetailRoute(val mediaType: String, val id: Int)
