package com.movies.public.model

enum class Category(val mediaType: MediaType) {
    POPULAR_MOVIES(MediaType.MOVIE),
    NOW_PLAYING_MOVIES(MediaType.MOVIE),
    POPULAR_TV(MediaType.TV),
    TOP_RATED_TV(MediaType.TV),
}
