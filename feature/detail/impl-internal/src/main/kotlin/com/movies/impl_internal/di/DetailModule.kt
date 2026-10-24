package com.movies.impl_internal.di

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.movies.common.AppNavigator
import com.movies.common.FeatureEntry
import com.movies.detail.DetailRoute
import com.movies.impl_internal.ui.DetailScreenRoute
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Inject

// Registra o destino de detalhe no NavGraph.
internal class DetailFeatureEntry @Inject constructor() : FeatureEntry {
    override fun register(builder: NavGraphBuilder, navigator: AppNavigator) {
        builder.composable<DetailRoute> { DetailScreenRoute(onBack = navigator::back) }
    }
}

// Módulo Hilt que provê as dependências de detail.
@Module
@InstallIn(SingletonComponent::class)
internal abstract class DetailModule {
    @Binds
    @IntoSet
    abstract fun bindFeatureEntry(entry: DetailFeatureEntry): FeatureEntry
}
