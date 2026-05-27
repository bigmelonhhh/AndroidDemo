package com.zencare.data.repository

import com.zencare.common.result.AppResult
import com.zencare.data.datastore.dao.HealthRecordDao
import com.zencare.data.datastore.entity.HealthRecordEntity
import com.zencare.model.dto.HealthMetricType
import com.zencare.model.dto.HealthRecord
import com.zencare.model.dto.HealthRecordListResponse
import com.zencare.model.dto.HealthRecordRequest
import com.zencare.model.dto.HealthStats
import com.zencare.network.api.HealthApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HealthRepository @Inject constructor(
    private val api: HealthApi,
    private val healthRecordDao: HealthRecordDao
) {
    suspend fun getRecords(
        type: HealthMetricType? = null,
        page: Int = 1
    ): AppResult<HealthRecordListResponse> {
        return runCatching {
            val response = api.getRecords(type, page = page)
            val data = response.data
            if (response.code == 0 && data != null) {
                val entities = data.records.map { it.toEntity() }
                healthRecordDao.insertRecords(entities)
                AppResult.Success(data)
            } else {
                AppResult.Error(response.code, response.message)
            }
        }.getOrElse { AppResult.Error(-1, it.message ?: "网络异常") }
    }

    suspend fun addRecord(request: HealthRecordRequest): AppResult<HealthRecord> {
        return runCatching {
            val response = api.addRecord(request)
            val data = response.data
            if (response.code == 0 && data != null) {
                healthRecordDao.insertRecord(data.toEntity())
                AppResult.Success(data)
            } else {
                AppResult.Error(response.code, response.message)
            }
        }.getOrElse { AppResult.Error(-1, it.message ?: "网络异常") }
    }

    suspend fun getStats(type: HealthMetricType): AppResult<HealthStats> {
        return runCatching {
            val response = api.getStats(type)
            val data = response.data
            if (response.code == 0 && data != null) {
                AppResult.Success(data)
            } else {
                AppResult.Error(response.code, response.message)
            }
        }.getOrElse { AppResult.Error(-1, it.message ?: "网络异常") }
    }

    suspend fun deleteRecord(recordId: String): AppResult<Unit> {
        return runCatching {
            val response = api.deleteRecord(recordId)
            if (response.code == 0) {
                healthRecordDao.deleteById(recordId)
                AppResult.Success(Unit)
            } else {
                AppResult.Error(response.code, response.message)
            }
        }.getOrElse { AppResult.Error(-1, it.message ?: "网络异常") }
    }

    fun getLocalRecords(type: HealthMetricType? = null): Flow<List<HealthRecord>> {
        val flow = if (type != null) {
            healthRecordDao.getRecordsByType(type.name)
        } else {
            healthRecordDao.getAllRecords()
        }
        return flow.map { entities -> entities.map { it.toDomain() } }
    }
}

private fun HealthRecord.toEntity() = HealthRecordEntity(
    id = id, type = type.name, value = value,
    unit = unit, recordedAt = recordedAt, note = note
)

private fun HealthRecordEntity.toDomain() = HealthRecord(
    id = id,
    type = HealthMetricType.valueOf(type),
    value = value,
    unit = unit,
    recordedAt = recordedAt,
    note = note
)
