package com.movies.ifood_challenge

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.movies.common.AppNavigator
import com.movies.common.FeatureEntry
import com.movies.favorites.FavoritesRoute
import com.movies.home.HomeRoute
import com.movies.ifood_challenge.designsystem.R as DsR

private data class BottomBarDestination(
    val route: Any,
    @StringRes val label: Int,
    @DrawableRes val icon: Int,
    @DrawableRes val selectedIcon: Int,
)

private val bottomBarDestinations = listOf(
    BottomBarDestination(HomeRoute, R.string.nav_home, DsR.drawable.ic_home_outlined, DsR.drawable.ic_home),
    BottomBarDestination(FavoritesRoute, R.string.nav_favorites, DsR.drawable.ic_favorite_border, DsR.drawable.ic_favorite),
)

@Composable
fun MoviesRoot(featureEntries: Set<FeatureEntry>) {
    val navController = rememberNavController()
    val navigator = remember(navController) {
        object : AppNavigator {
            override fun navigate(route: Any) = navController.navigate(route)
            override fun back() {
                navController.popBackStack()
            }
        }
    }
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val showBottomBar = currentDestination == null ||
        bottomBarDestinations.any { currentDestination.hasRoute(it.route::class) }
    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                Surface(
                    shape = RoundedCornerShape(28.dp),
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                ) {
                    NavigationBar(containerColor = Color.Transparent, windowInsets = WindowInsets(0)) {
                        bottomBarDestinations.forEach { item ->
                            val selected = currentDestination?.hasRoute(item.route::class) ?: (item.route == HomeRoute)
                            NavigationBarItem(
                                selected = selected,
                                onClick = {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = { Icon(painterResource(if (selected) item.selectedIcon else item.icon), contentDescription = null) },
                                label = { Text(stringResource(item.label), style = MaterialTheme.typography.labelMedium) },
                            )
                        }
                    }
                }
            }
        },
    ) { _ ->
        NavHost(navController, startDestination = HomeRoute) {
            featureEntries.forEach { it.register(this, navigator) }
        }
    }
}
