package com.movies.impl_internal.common

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.movies.database.entity.MediaEntity
import com.movies.impl_internal.datasource.local.CatalogLocalDataSource
import kotlinx.coroutines.CancellationException

@OptIn(ExperimentalPagingApi::class)
internal class ListRemoteMediator(
    private val scope: String,
    private val local: CatalogLocalDataSource,
    private val fetchPage: suspend (page: Int) -> RemotePage,
) : RemoteMediator<Int, MediaEntity>() {

    override suspend fun initialize(): InitializeAction =
        if (local.isFresh(scope)) InitializeAction.SKIP_INITIAL_REFRESH else InitializeAction.LAUNCH_INITIAL_REFRESH

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MediaEntity>,
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> FIRST_PAGE
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> local.nextPage(scope) ?: return MediatorResult.Success(endOfPaginationReached = true)
        }
        return try {
            val remote = fetchPage(page)
            local.storePage(scope, remote, replace = loadType == LoadType.REFRESH)
            MediatorResult.Success(endOfPaginationReached = remote.isLast)
        } catch (e: CancellationException) {
            throw e
        } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
            MediatorResult.Error(e)
        }
    }
}
