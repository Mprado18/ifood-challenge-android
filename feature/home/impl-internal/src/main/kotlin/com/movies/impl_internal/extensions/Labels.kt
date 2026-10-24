package com.movies.impl_internal.extensions

import androidx.annotation.StringRes
import com.movies.domain.model.Category
import com.movies.ifood_challenge.feature.home.impl_internal.R

@StringRes
internal fun Category.labelRes(): Int = when (this) {
    Category.POPULAR_MOVIES -> R.string.category_popular_movies
    Category.NOW_PLAYING_MOVIES -> R.string.category_now_playing_movies
    Category.POPULAR_TV -> R.string.category_popular_tv
    Category.TOP_RATED_TV -> R.string.category_top_rated_tv
}
