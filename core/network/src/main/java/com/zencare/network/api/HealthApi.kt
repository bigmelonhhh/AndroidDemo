package com.zencare.network.api

import com.zencare.model.dto.HealthRecord
import com.zencare.model.dto.HealthRecordListResponse
import com.zencare.model.dto.HealthRecordRequest
import com.zencare.model.dto.HealthMetricType
import com.zencare.model.dto.HealthStats
import com.zencare.network.ApiResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface HealthApi {

    @GET("api/v1/health/records")
    suspend fun getRecords(
        @Query("type") type: HealthMetricType? = null,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 50
    ): ApiResponse<HealthRecordListResponse>

    @POST("api/v1/health/records")
    suspend fun addRecord(@Body request: HealthRecordRequest): ApiResponse<HealthRecord>

    @PUT("api/v1/health/records/{recordId}")
    suspend fun updateRecord(
        @Path("recordId") recordId: String,
        @Body request: HealthRecordRequest
    ): ApiResponse<HealthRecord>

    @DELETE("api/v1/health/records/{recordId}")
    suspend fun deleteRecord(@Path("recordId") recordId: String): ApiResponse<Unit>

    @GET("api/v1/health/stats/{type}")
    suspend fun getStats(@Path("type") type: HealthMetricType): ApiResponse<HealthStats>
}
