package com.movies.domain.model

enum class MediaType(val apiValue: String) {
    MOVIE("movie"),
    TV("tv"),
    ;

    companion object {
        fun fromApiValue(value: String): MediaType? = entries.firstOrNull { it.apiValue == value }
    }
}
