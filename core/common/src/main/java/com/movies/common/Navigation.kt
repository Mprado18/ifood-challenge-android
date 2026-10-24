package com.movies.common

import androidx.navigation.NavGraphBuilder

/** Navegação exposta às features sem acoplá-las ao NavController nem entre si. */
interface AppNavigator {
    fun navigate(route: Any)

    fun back()
}

/** Cada feature registra seus destinos; o app apenas agrega as entradas via Hilt multibinding. */
interface FeatureEntry {
    fun register(builder: NavGraphBuilder, navigator: AppNavigator)
}
