package com.movies.impl_internal

import com.movies.domain.model.MediaType
import com.movies.testing.mediaSummary

internal fun media(id: Int, type: MediaType = MediaType.MOVIE, title: String = "Filme $id") =
    mediaSummary(id, type, title, voteAverage = 7.5, genreIds = listOf(28))
