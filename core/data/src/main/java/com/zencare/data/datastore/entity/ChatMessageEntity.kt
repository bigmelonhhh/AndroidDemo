package com.zencare.data.datastore.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey
    val id: String,
    val sessionId: String,
    val role: String,       // USER / ASSISTANT / SYSTEM
    val type: String,       // TEXT / IMAGE / VOICE
    val content: String,
    val durationSeconds: Int? = null,
    val imageWidth: Int? = null,
    val imageHeight: Int? = null,
    val createdAt: String,
    val syncStatus: String = "synced"  // synced / pending / failed
)
