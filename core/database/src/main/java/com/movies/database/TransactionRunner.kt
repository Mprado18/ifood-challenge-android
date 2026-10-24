package com.movies.database

import androidx.room.withTransaction
import javax.inject.Inject

/** Permite que a camada de dados agrupe escritas numa transação sem conhecer o [CatalogDatabase]. */
class TransactionRunner @Inject constructor(
    private val database: CatalogDatabase,
) {
    suspend fun <T> run(block: suspend () -> T): T = database.withTransaction(block)
}
