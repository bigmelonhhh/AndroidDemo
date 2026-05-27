package com.zencare.shop.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.zencare.model.route.Route
import com.zencare.shop.ui.screen.CartScreen
import com.zencare.shop.ui.screen.ProductDetailScreen
import com.zencare.shop.ui.screen.ShopHomeScreen

fun NavGraphBuilder.shopNavGraph(navController: NavController) {
    composable<Route.ShopHome> {
        ShopHomeScreen(
            onProductClick = { productId ->
                navController.navigate(Route.ProductDetail(productId))
            },
            onCartClick = {
                navController.navigate(Route.Cart)
            }
        )
    }
    composable<Route.ProductDetail> { backStackEntry ->
        val route = backStackEntry.toRoute<Route.ProductDetail>()
        ProductDetailScreen(
            productId = route.productId,
            onBack = { navController.popBackStack() },
            onAddToCart = { productId ->
                navController.navigate(Route.Cart)
            }
        )
    }
    composable<Route.Cart> {
        CartScreen(
            onBack = { navController.popBackStack() },
            onCheckout = { total ->
                // Navigate to order confirmation
            }
        )
    }
    composable<Route.OrderConfirm> { backStackEntry ->
        val route = backStackEntry.toRoute<Route.OrderConfirm>()
        // TODO: OrderConfirmScreen in future iteration
        CartScreen(
            onBack = { navController.popBackStack() },
            onCheckout = { }
        )
    }
}
