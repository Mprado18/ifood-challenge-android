package com.movies.impl_internal.api

import com.movies.common.DispatcherProvider
import com.movies.network.NetworkConfig
import com.movies.domain.error.CatalogException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

// Executa chamadas da API traduzindo falhas em CatalogException.
internal class ApiExecutor @Inject constructor(
    private val config: NetworkConfig,
    private val dispatchers: DispatcherProvider,
) {
    @Suppress("ThrowsCount")
    suspend fun <T> execute(block: suspend () -> T): T {
        if (config.bearerToken.isBlank()) throw CatalogException.MissingToken()
        return withContext(dispatchers.io) {
            try {
                block()
            } catch (e: CancellationException) {
                throw e
            } catch (e: HttpException) {
                throw when (val code = e.code()) {
                    401, 403 -> CatalogException.Unauthorized(code)
                    429 -> CatalogException.RateLimited()
                    in 500..599 -> CatalogException.Server(code)
                    else -> CatalogException.Contract(e)
                }
            } catch (e: IOException) {
                throw CatalogException.Network(e)
            } catch (e: SerializationException) {
                throw CatalogException.Contract(e)
            }
        }
    }
}
