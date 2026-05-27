package com.zencare.network.api

import com.zencare.model.dto.ChatHistoryResponse
import com.zencare.model.dto.ChatSessionListResponse
import com.zencare.model.dto.SendMessageRequest
import com.zencare.model.dto.SendMessageResponse
import com.zencare.network.ApiResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ConsultationApi {

    @POST("api/v1/chat/sessions")
    suspend fun createSession(): ApiResponse<String>

    @POST("api/v1/chat/send")
    suspend fun sendMessage(@Body request: SendMessageRequest): ApiResponse<SendMessageResponse>

    @GET("api/v1/chat/sessions")
    suspend fun getSessions(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): ApiResponse<ChatSessionListResponse>

    @GET("api/v1/chat/history/{sessionId}")
    suspend fun getChatHistory(
        @Path("sessionId") sessionId: String,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 50
    ): ApiResponse<ChatHistoryResponse>

    @DELETE("api/v1/chat/sessions/{sessionId}")
    suspend fun deleteSession(@Path("sessionId") sessionId: String): ApiResponse<Unit>
}
