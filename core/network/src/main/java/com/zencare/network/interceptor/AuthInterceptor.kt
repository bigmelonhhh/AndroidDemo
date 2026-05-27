package com.zencare.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val request = original.newBuilder()
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .apply {
                // Add auth token when available
                // val token = TokenManager.getToken()
                // if (token != null) header("Authorization", "Bearer $token")
            }
            .build()
        return chain.proceed(request)
    }
}
