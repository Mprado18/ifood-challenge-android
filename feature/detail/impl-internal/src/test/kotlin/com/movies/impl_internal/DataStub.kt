package com.movies.impl_internal

import com.movies.testing.mediaDetail
import com.movies.testing.mediaSummary

internal val movieStub = mediaSummary(
    id = 11,
    title = "Star Wars",
    overview = "Uma princesa é capturada.",
    posterUrl = "p",
    backdropUrl = "b",
    voteAverage = 8.2,
    voteCount = 18528,
    releaseDate = "1977-05-25",
    genreIds = listOf(28),
)

internal val movieDetailStub = mediaDetail(movieStub)
