package com.movies.public.extensions

import com.movies.public.model.MediaKey
import com.movies.public.model.MediaSummary
import com.movies.public.repository.FavoriteRepository

/** Inverte o estado de favorito de [media] com base nas chaves favoritas já conhecidas pela tela. */
suspend fun FavoriteRepository.toggle(
    media: MediaSummary,
    favorites: Set<MediaKey>,
): Result<Unit> = setFavorite(media, isFavorite = media.key !in favorites)
