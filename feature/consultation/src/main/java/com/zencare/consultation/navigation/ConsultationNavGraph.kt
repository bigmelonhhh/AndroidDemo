package com.zencare.consultation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.zencare.consultation.ui.screen.ChatDetailScreen
import com.zencare.consultation.ui.screen.ConsultationHomeScreen
import com.zencare.model.route.Route

fun NavGraphBuilder.consultationNavGraph(navController: NavController) {
    composable<Route.ConsultationHome> {
        ConsultationHomeScreen(
            onSessionClick = { sessionId ->
                navController.navigate(Route.ChatDetail(sessionId))
            }
        )
    }
    composable<Route.ChatDetail> { backStackEntry ->
        val route = backStackEntry.toRoute<Route.ChatDetail>()
        ChatDetailScreen(
            sessionId = route.sessionId,
            onBack = { navController.popBackStack() }
        )
    }
}
