package com.zencare.network.api

import com.zencare.model.dto.CreateOrderRequest
import com.zencare.model.dto.Order
import com.zencare.model.dto.Product
import com.zencare.model.dto.ProductCategory
import com.zencare.model.dto.ProductListResponse
import com.zencare.network.ApiResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ShopApi {

    @GET("api/v1/shop/products")
    suspend fun getProducts(
        @Query("category") category: ProductCategory? = null,
        @Query("keyword") keyword: String? = null,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): ApiResponse<ProductListResponse>

    @GET("api/v1/shop/products/{productId}")
    suspend fun getProductDetail(@Path("productId") productId: String): ApiResponse<Product>

    @POST("api/v1/shop/cart/{productId}")
    suspend fun addToCart(
        @Path("productId") productId: String,
        @Query("quantity") quantity: Int = 1
    ): ApiResponse<Unit>

    @GET("api/v1/shop/cart")
    suspend fun getCart(): ApiResponse<List<Product>>

    @DELETE("api/v1/shop/cart/{productId}")
    suspend fun removeFromCart(@Path("productId") productId: String): ApiResponse<Unit>

    @POST("api/v1/shop/orders")
    suspend fun createOrder(@Body request: CreateOrderRequest): ApiResponse<Order>

    @GET("api/v1/shop/orders")
    suspend fun getOrders(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): ApiResponse<List<Order>>
}
