package com.zencare.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class HealthRecord(
    val id: String,
    val type: HealthMetricType,
    val value: Double,
    val unit: String,
    val recordedAt: String,
    val note: String? = null
)

@Serializable
enum class HealthMetricType {
    BLOOD_PRESSURE_SYSTOLIC,
    BLOOD_PRESSURE_DIASTOLIC,
    BLOOD_SUGAR,
    WEIGHT,
    HEART_RATE,
    TEMPERATURE
}

@Serializable
data class HealthRecordRequest(
    val type: HealthMetricType,
    val value: Double,
    val unit: String,
    val recordedAt: String,
    val note: String? = null
)

@Serializable
data class HealthRecordListResponse(
    val records: List<HealthRecord>,
    val hasMore: Boolean
)

@Serializable
data class HealthStats(
    val type: HealthMetricType,
    val latestValue: Double,
    val unit: String,
    val trend: TrendDirection,
    val minValue: Double,
    val maxValue: Double,
    val avgValue: Double
)

@Serializable
enum class TrendDirection {
    UP, DOWN, STABLE
}
