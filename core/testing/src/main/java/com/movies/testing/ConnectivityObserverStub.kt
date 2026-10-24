package com.movies.testing

import com.movies.common.ConnectivityObserver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/** Conectividade controlável pelo teste: mude [online] para simular perda e retorno de rede. */
class ConnectivityObserverStub(initiallyOnline: Boolean = true) : ConnectivityObserver {
    val online = MutableStateFlow(initiallyOnline)

    override fun observeOnline(): Flow<Boolean> = online
}
