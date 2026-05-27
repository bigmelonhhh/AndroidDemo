package com.zencare.data.repository

import com.zencare.common.result.AppResult
import com.zencare.data.datastore.dao.CartDao
import com.zencare.data.datastore.entity.CartItemEntity
import com.zencare.model.dto.CreateOrderRequest
import com.zencare.model.dto.Order
import com.zencare.model.dto.Product
import com.zencare.model.dto.ProductCategory
import com.zencare.model.dto.ProductListResponse
import com.zencare.network.api.ShopApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShopRepository @Inject constructor(
    private val api: ShopApi,
    private val cartDao: CartDao
) {
    suspend fun getProducts(
        category: ProductCategory? = null,
        keyword: String? = null,
        page: Int = 1
    ): AppResult<ProductListResponse> {
        return runCatching {
            val response = api.getProducts(category, keyword, page = page)
            val data = response.data
            if (response.code == 0 && data != null) {
                AppResult.Success(data)
            } else {
                AppResult.Error(response.code, response.message)
            }
        }.getOrElse { AppResult.Error(-1, it.message ?: "网络异常") }
    }

    suspend fun getProductDetail(productId: String): AppResult<Product> {
        return runCatching {
            val response = api.getProductDetail(productId)
            val data = response.data
            if (response.code == 0 && data != null) {
                AppResult.Success(data)
            } else {
                AppResult.Error(response.code, response.message)
            }
        }.getOrElse { AppResult.Error(-1, it.message ?: "网络异常") }
    }

    suspend fun getCart(): AppResult<List<Product>> {
        return runCatching {
            val response = api.getCart()
            val data = response.data
            if (response.code == 0 && data != null) {
                AppResult.Success(data)
            } else {
                AppResult.Error(response.code, response.message)
            }
        }.getOrElse { AppResult.Error(-1, it.message ?: "网络异常") }
    }

    suspend fun addToCart(
        productId: String,
        productName: String,
        price: Double,
        imageUrl: String,
        quantity: Int = 1
    ) {
        val existing = cartDao.getById(productId)
        if (existing != null) {
            cartDao.updateQuantity(productId, existing.quantity + quantity)
        } else {
            cartDao.insert(CartItemEntity(productId, productName, price, quantity, imageUrl))
        }
        api.addToCart(productId, quantity)
    }

    suspend fun removeFromCart(productId: String) {
        cartDao.deleteById(productId)
        api.removeFromCart(productId)
    }

    fun getCartFlow(): Flow<List<CartItemEntity>> = cartDao.getAll()

    suspend fun createOrder(request: CreateOrderRequest): AppResult<Order> {
        return runCatching {
            val response = api.createOrder(request)
            val data = response.data
            if (response.code == 0 && data != null) {
                cartDao.clearAll()
                AppResult.Success(data)
            } else {
                AppResult.Error(response.code, response.message)
            }
        }.getOrElse { AppResult.Error(-1, it.message ?: "网络异常") }
    }
}
