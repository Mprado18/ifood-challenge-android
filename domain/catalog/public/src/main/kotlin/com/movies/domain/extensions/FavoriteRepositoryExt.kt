package com.movies.domain.extensions

import com.movies.domain.model.MediaKey
import com.movies.domain.model.MediaSummary
import com.movies.domain.repository.FavoriteRepository

/** Inverte o estado de favorito de [media] com base nas chaves favoritas já conhecidas pela tela. */
suspend fun FavoriteRepository.toggle(
    media: MediaSummary,
    favorites: Set<MediaKey>,
): Result<Unit> = setFavorite(media, isFavorite = media.key !in favorites)
