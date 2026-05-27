package com.zencare.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class ChatMessage(
    val id: String,
    val sessionId: String,
    val role: MessageRole,
    val type: MessageType,
    val content: String,
    val metadata: MessageMetadata? = null,
    val createdAt: String
)

@Serializable
enum class MessageRole {
    USER, ASSISTANT, SYSTEM
}

@Serializable
enum class MessageType {
    TEXT, IMAGE, VOICE
}

@Serializable
data class MessageMetadata(
    val durationSeconds: Int? = null,
    val imageWidth: Int? = null,
    val imageHeight: Int? = null
)

@Serializable
data class SendMessageRequest(
    val sessionId: String,
    val type: MessageType,
    val content: String,
    val metadata: MessageMetadata? = null
)

@Serializable
data class SendMessageResponse(
    val messageId: String,
    val sessionId: String
)

@Serializable
data class ChatSession(
    val id: String,
    val title: String,
    val lastMessage: String? = null,
    val updatedAt: String
)

@Serializable
data class ChatSessionListResponse(
    val sessions: List<ChatSession>,
    val hasMore: Boolean
)

@Serializable
data class ChatHistoryResponse(
    val messages: List<ChatMessage>,
    val hasMore: Boolean
)
