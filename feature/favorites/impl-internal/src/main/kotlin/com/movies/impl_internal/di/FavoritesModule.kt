package com.movies.impl_internal.di

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.movies.common.AppNavigator
import com.movies.common.FeatureEntry
import com.movies.detail.DetailRoute
import com.movies.favorites.FavoritesRoute
import com.movies.impl_internal.ui.FavoritesScreenRoute
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Inject

// Registra o destino de favoritos no NavGraph.
internal class FavoritesFeatureEntry @Inject constructor() : FeatureEntry {
    override fun register(builder: NavGraphBuilder, navigator: AppNavigator) {
        builder.composable<FavoritesRoute> {
            FavoritesScreenRoute(onOpenDetail = { navigator.navigate(DetailRoute(it.type.apiValue, it.id)) })
        }
    }
}

// Módulo Hilt que provê as dependências de favorites.
@Module
@InstallIn(SingletonComponent::class)
internal abstract class FavoritesModule {
    @Binds
    @IntoSet
    abstract fun bindFeatureEntry(entry: FavoritesFeatureEntry): FeatureEntry
}
