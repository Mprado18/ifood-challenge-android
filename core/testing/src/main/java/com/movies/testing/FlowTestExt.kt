package com.movies.testing

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher

// Coleta um Flow em background durante o teste e devolve os valores emitidos.
/** Coleta [flow] em background e devolve a lista que vai sendo preenchida (ex.: efeitos da ViewModel). */
@OptIn(ExperimentalCoroutinesApi::class)
fun <T> TestScope.collectInBackground(flow: Flow<T>): List<T> {
    val received = mutableListOf<T>()
    backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { flow.toList(received) }
    return received
}

/** Mantém [flow] assinado, necessário para `stateIn(WhileSubscribed)` produzir valores. */
@OptIn(ExperimentalCoroutinesApi::class)
fun TestScope.subscribeTo(flow: Flow<*>) {
    backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { flow.collect {} }
}
