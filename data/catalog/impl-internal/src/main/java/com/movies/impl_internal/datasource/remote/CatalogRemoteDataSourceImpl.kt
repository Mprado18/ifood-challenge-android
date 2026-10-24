package com.movies.impl_internal.datasource.remote

import com.movies.impl_internal.api.ApiExecutor
import com.movies.impl_internal.api.TmdbApiService
import com.movies.impl_internal.mapper.toMoviePage
import com.movies.impl_internal.mapper.toSearchPage
import com.movies.impl_internal.mapper.toTvPage
import com.movies.impl_internal.common.RemotePage
import com.movies.domain.model.Category
import javax.inject.Inject

internal class CatalogRemoteDataSourceImpl @Inject constructor(
    private val api: TmdbApiService,
    private val executor: ApiExecutor,
) : CatalogRemoteDataSource {

    override suspend fun fetchCategoryPage(
        category: Category,
        page: Int,
    ): RemotePage = when (category) {
        Category.POPULAR_MOVIES -> executor.execute { api.popularMovies(page) }.toMoviePage()
        Category.NOW_PLAYING_MOVIES -> executor.execute { api.nowPlayingMovies(page) }.toMoviePage()
        Category.POPULAR_TV -> executor.execute { api.popularTv(page) }.toTvPage()
        Category.TOP_RATED_TV -> executor.execute { api.topRatedTv(page) }.toTvPage()
    }

    override suspend fun searchPage(
        query: String,
        page: Int,
    ): RemotePage = executor.execute { api.searchMulti(query, page) }.toSearchPage()
}
