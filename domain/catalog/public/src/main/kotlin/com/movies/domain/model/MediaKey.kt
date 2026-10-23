package com.movies.domain.model

/** Identidade de uma mídia; filme e série com o mesmo id continuam distintos. */
data class MediaKey(
    val type: MediaType,
    val id: Int,
) {
    /** Chave estável para listas, já que o id sozinho pode repetir entre filme e série. */
    val uniqueId: String get() = "${type.apiValue}:$id"
}
