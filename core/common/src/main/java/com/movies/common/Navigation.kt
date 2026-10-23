package com.movies.common

import androidx.navigation.NavGraphBuilder

// Contrato de navegação que as features usam sem conhecer o NavController.
/** Navegação exposta às features sem acoplá-las ao NavController nem entre si. */
interface AppNavigator {
    fun navigate(route: Any)

    fun back()
}

// Contrato para cada feature registrar seu destino no NavGraph.
/** Cada feature registra seus destinos; o app apenas agrega as entradas via Hilt multibinding. */
interface FeatureEntry {
    fun register(builder: NavGraphBuilder, navigator: AppNavigator)
}
