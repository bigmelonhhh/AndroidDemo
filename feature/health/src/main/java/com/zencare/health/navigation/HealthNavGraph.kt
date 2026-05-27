package com.zencare.health.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.zencare.health.ui.screen.HealthHomeScreen
import com.zencare.health.ui.screen.HealthRecordInputScreen
import com.zencare.model.route.Route

fun NavGraphBuilder.healthNavGraph(navController: NavController) {
    composable<Route.HealthHome> {
        HealthHomeScreen(
            onAddRecord = { metricName, metricType, unit ->
                navController.navigate(Route.HealthRecordDetail("new"))
            }
        )
    }
    composable<Route.HealthRecordDetail> { backStackEntry ->
        val route = backStackEntry.toRoute<Route.HealthRecordDetail>()
        HealthRecordInputScreen(
            metricName = "健康数据",
            metricType = "GENERAL",
            unit = "",
            onBack = { navController.popBackStack() },
            onSave = { value, note ->
                navController.popBackStack()
            }
        )
    }
}
