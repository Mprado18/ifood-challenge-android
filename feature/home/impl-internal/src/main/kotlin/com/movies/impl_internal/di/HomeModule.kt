package com.movies.impl_internal.di

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.movies.common.AppNavigator
import com.movies.common.FeatureEntry
import com.movies.detail.DetailRoute
import com.movies.home.CatalogRoute
import com.movies.home.HomeRoute
import com.movies.impl_internal.ui.catalog.CatalogScreenRoute
import com.movies.impl_internal.ui.home.HomeScreenRoute
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Inject

internal class HomeFeatureEntry @Inject constructor() : FeatureEntry {
    override fun register(builder: NavGraphBuilder, navigator: AppNavigator) {
        builder.composable<HomeRoute> {
            HomeScreenRoute(
                onOpenDetail = { navigator.navigate(DetailRoute(it.type.apiValue, it.id)) },
                onOpenCategory = { category, genre -> navigator.navigate(CatalogRoute(category.name, genre.orEmpty())) },
            )
        }
        builder.composable<CatalogRoute> {
            CatalogScreenRoute(
                onBack = navigator::back,
                onOpenDetail = { navigator.navigate(DetailRoute(it.type.apiValue, it.id)) },
            )
        }
    }
}

@Module
@InstallIn(SingletonComponent::class)
internal abstract class HomeModule {
    @Binds
    @IntoSet
    abstract fun bindFeatureEntry(entry: HomeFeatureEntry): FeatureEntry
}
