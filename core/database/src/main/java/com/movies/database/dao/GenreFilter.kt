package com.movies.database.dao

internal const val GENRE_FILTER = """
    (:filtered = 0
     OR (m.type = 'movie' AND m.genreIds LIKE '%,' || :movieGenre || ',%')
     OR (m.type = 'tv' AND m.genreIds LIKE '%,' || :tvGenre || ',%'))
"""
