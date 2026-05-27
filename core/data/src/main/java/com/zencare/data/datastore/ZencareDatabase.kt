package com.zencare.data.datastore

import androidx.room.Database
import androidx.room.RoomDatabase
import com.zencare.data.datastore.dao.CartDao
import com.zencare.data.datastore.dao.ChatMessageDao
import com.zencare.data.datastore.dao.HealthRecordDao
import com.zencare.data.datastore.entity.CartItemEntity
import com.zencare.data.datastore.entity.ChatMessageEntity
import com.zencare.data.datastore.entity.HealthRecordEntity

@Database(
    entities = [
        ChatMessageEntity::class,
        HealthRecordEntity::class,
        CartItemEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class ZencareDatabase : RoomDatabase() {
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun healthRecordDao(): HealthRecordDao
    abstract fun cartDao(): CartDao
}
