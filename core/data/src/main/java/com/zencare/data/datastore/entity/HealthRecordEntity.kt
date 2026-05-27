package com.zencare.data.datastore.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "health_records")
data class HealthRecordEntity(
    @PrimaryKey
    val id: String,
    val type: String,
    val value: Double,
    val unit: String,
    val recordedAt: String,
    val note: String? = null,
    val syncStatus: String = "synced"
)
