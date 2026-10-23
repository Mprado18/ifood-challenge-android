package com.movies.common

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

// Contrato para observar o estado de conectividade do dispositivo.
interface ConnectivityObserver {
    /** Emite true quando há rede com acesso validado. */
    fun observeOnline(): Flow<Boolean>
}

// Observa a rede via ConnectivityManager e emite online/offline em Flow.
class AndroidConnectivityObserver(context: Context) : ConnectivityObserver {
    private val manager = context.getSystemService(ConnectivityManager::class.java)

    override fun observeOnline(): Flow<Boolean> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(isValidated())
            }

            override fun onLost(network: Network) {
                trySend(isValidated())
            }

            override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
                trySend(capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED))
            }
        }
        trySend(isValidated())
        manager.registerNetworkCallback(
            NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build(),
            callback,
        )
        awaitClose { manager.unregisterNetworkCallback(callback) }
    }.distinctUntilChanged()

    private fun isValidated(): Boolean =
        manager.getNetworkCapabilities(manager.activeNetwork)
            ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) == true
}
