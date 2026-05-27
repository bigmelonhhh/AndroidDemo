package com.zencare.app

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.zencare.consultation.navigation.consultationNavGraph
import com.zencare.health.navigation.healthNavGraph
import com.zencare.model.route.Route
import com.zencare.shop.navigation.shopNavGraph
import com.zencare.ui.theme.ZencareTheme

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: Route
)

private val bottomNavItems = listOf(
    BottomNavItem("问诊", Icons.AutoMirrored.Outlined.Chat, Route.ConsultationHome),
    BottomNavItem("健康", Icons.Outlined.LocalHospital, Route.HealthHome),
    BottomNavItem("商城", Icons.Outlined.ShoppingCart, Route.ShopHome)
)

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = bottomNavItems.any { item ->
        currentDestination?.hasRoute(item.route::class) == true
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentDestination?.hasRoute(item.route::class) == true,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Route.ConsultationHome,
            modifier = Modifier.padding(innerPadding)
        ) {
            consultationNavGraph(navController)
            healthNavGraph(navController)
            shopNavGraph(navController)
        }
    }
}
