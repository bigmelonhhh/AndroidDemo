package com.zencare.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val category: ProductCategory,
    val stock: Int,
    val prescription: Boolean = false
)

@Serializable
enum class ProductCategory {
    MEDICINE, HEALTH_PRODUCT, MEDICAL_DEVICE, OTHER
}

@Serializable
data class ProductListResponse(
    val products: List<Product>,
    val hasMore: Boolean,
    val totalCount: Int
)

@Serializable
data class CartItem(
    val productId: String,
    val productName: String,
    val price: Double,
    val quantity: Int,
    val imageUrl: String
)

@Serializable
data class Order(
    val id: String,
    val items: List<CartItem>,
    val totalAmount: Double,
    val status: OrderStatus,
    val createdAt: String
)

@Serializable
enum class OrderStatus {
    PENDING, CONFIRMED, SHIPPING, DELIVERED, CANCELLED
}

@Serializable
data class CreateOrderRequest(
    val items: List<OrderItemRequest>
)

@Serializable
data class OrderItemRequest(
    val productId: String,
    val quantity: Int
)
