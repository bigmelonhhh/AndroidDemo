package com.zencare.model.route

import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {
    @Serializable
    data object Main : Route

    // Consultation
    @Serializable
    data object ConsultationHome : Route

    @Serializable
    data class ChatDetail(val sessionId: String) : Route

    // Health
    @Serializable
    data object HealthHome : Route

    @Serializable
    data class HealthRecordDetail(val recordId: String) : Route

    // Shop
    @Serializable
    data object ShopHome : Route

    @Serializable
    data class ProductDetail(val productId: String) : Route

    @Serializable
    data object Cart : Route

    @Serializable
    data class OrderConfirm(val productId: String) : Route
}
