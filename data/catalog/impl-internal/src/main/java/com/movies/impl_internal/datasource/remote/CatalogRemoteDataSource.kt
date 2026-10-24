package com.movies.impl_internal.datasource.remote

import com.movies.impl_internal.common.RemotePage
import com.movies.domain.model.Category

// Contrato da fonte de dados remota de catalog.
internal interface CatalogRemoteDataSource {
    suspend fun fetchCategoryPage(
        category: Category,
        page: Int,
    ): RemotePage

    suspend fun searchPage(
        query: String,
        page: Int,
    ): RemotePage
}
