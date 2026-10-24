package com.movies.impl_internal.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.movies.common.ConnectivityObserver
import com.movies.impl_internal.datasource.local.CatalogLocalDataSource
import com.movies.impl_internal.datasource.remote.CatalogRemoteDataSource
import com.movies.impl_internal.mapper.toSummary
import com.movies.impl_internal.common.FIRST_PAGE
import com.movies.impl_internal.common.ListRemoteMediator
import com.movies.impl_internal.common.searchScope
import com.movies.impl_internal.common.toScope
import com.movies.domain.model.Category
import com.movies.domain.model.Genre
import com.movies.domain.model.MediaSummary
import com.movies.domain.repository.CatalogRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val PAGE_SIZE = 20

// Implementação do repositório de catalog, combinando fontes locais e remotas.
@OptIn(ExperimentalPagingApi::class, ExperimentalCoroutinesApi::class)
internal class CatalogRepositoryImpl @Inject constructor(
    private val remote: CatalogRemoteDataSource,
    private val local: CatalogLocalDataSource,
    private val connectivity: ConnectivityObserver,
) : CatalogRepository {
    private val pagingConfig = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false)

    override fun observePreview(
        category: Category,
        genres: List<Genre>,
    ): Flow<List<MediaSummary>> =
        local.observePreview(category.toScope(), genres).map { list -> list.map { it.toSummary() } }

    override fun categoryPagingFlow(
        category: Category,
        genres: List<Genre>,
    ): Flow<PagingData<MediaSummary>> {
        val scope = category.toScope()
        return Pager(
            config = pagingConfig,
            remoteMediator = ListRemoteMediator(scope, local) { page -> remote.fetchCategoryPage(category, page) },
            pagingSourceFactory = { local.pagingSource(scope, genres) },
        ).flow.map { data -> data.map { it.toSummary() } }
    }

    override fun searchPagingFlow(
        query: String,
        genres: List<Genre>,
    ): Flow<PagingData<MediaSummary>> {
        val scope = searchScope(query)
        return connectivity.observeOnline().distinctUntilChanged().flatMapLatest { online ->
            if (online) {
                Pager(
                    config = pagingConfig,
                    remoteMediator = ListRemoteMediator(scope, local) { page -> remote.searchPage(query.trim(), page) },
                    pagingSourceFactory = { local.pagingSource(scope, genres) },
                ).flow
            } else {
                Pager(pagingConfig) { local.searchLocalPagingSource(query, genres) }.flow
            }
        }.map { data -> data.map { it.toSummary() } }
    }

    override suspend fun refreshCategory(category: Category): Result<Unit> = runCatchingSuspend {
        local.storePage(category.toScope(), remote.fetchCategoryPage(category, FIRST_PAGE), replace = true)
    }
}
