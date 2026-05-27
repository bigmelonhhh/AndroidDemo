package com.zencare.data.di

import android.content.Context
import androidx.room.Room
import com.zencare.data.datastore.ZencareDatabase
import com.zencare.data.datastore.dao.CartDao
import com.zencare.data.datastore.dao.ChatMessageDao
import com.zencare.data.datastore.dao.HealthRecordDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ZencareDatabase =
        Room.databaseBuilder(
            context,
            ZencareDatabase::class.java,
            "zencare.db"
        ).fallbackToDestructiveMigration().build()

    @Provides
    fun provideChatMessageDao(db: ZencareDatabase): ChatMessageDao = db.chatMessageDao()

    @Provides
    fun provideHealthRecordDao(db: ZencareDatabase): HealthRecordDao = db.healthRecordDao()

    @Provides
    fun provideCartDao(db: ZencareDatabase): CartDao = db.cartDao()
}
