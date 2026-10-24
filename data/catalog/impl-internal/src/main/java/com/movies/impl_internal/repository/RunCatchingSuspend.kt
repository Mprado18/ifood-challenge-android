package com.movies.impl_internal.repository

import kotlinx.coroutines.CancellationException

@Suppress("TooGenericExceptionCaught")
internal suspend inline fun <T> runCatchingSuspend(block: () -> T): Result<T> = try {
    Result.success(block())
} catch (e: CancellationException) {
    throw e
} catch (e: Exception) {
    Result.failure(e)
}
