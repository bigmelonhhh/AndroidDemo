package com.zencare.data.datastore.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zencare.data.datastore.entity.HealthRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthRecordDao {

    @Query("SELECT * FROM health_records WHERE type = :type ORDER BY recordedAt DESC")
    fun getRecordsByType(type: String): Flow<List<HealthRecordEntity>>

    @Query("SELECT * FROM health_records ORDER BY recordedAt DESC")
    fun getAllRecords(): Flow<List<HealthRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecords(records: List<HealthRecordEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: HealthRecordEntity)

    @Query("DELETE FROM health_records WHERE id = :recordId")
    suspend fun deleteById(recordId: String)
}
