package com.zencare.data.repository

import com.zencare.common.result.AppResult
import com.zencare.data.datastore.dao.ChatMessageDao
import com.zencare.data.datastore.entity.ChatMessageEntity
import com.zencare.model.dto.ChatHistoryResponse
import com.zencare.model.dto.ChatMessage
import com.zencare.model.dto.ChatSessionListResponse
import com.zencare.model.dto.MessageMetadata
import com.zencare.model.dto.SendMessageRequest
import com.zencare.model.dto.SendMessageResponse
import com.zencare.network.api.ConsultationApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConsultationRepository @Inject constructor(
    private val api: ConsultationApi,
    private val chatMessageDao: ChatMessageDao
) {
    suspend fun createSession(): AppResult<String> {
        return runCatching {
            val response = api.createSession()
            val data = response.data
            if (response.code == 0 && data != null) {
                AppResult.Success(data)
            } else {
                AppResult.Error(response.code, response.message)
            }
        }.getOrElse { AppResult.Error(-1, it.message ?: "网络异常") }
    }

    suspend fun sendMessage(request: SendMessageRequest): AppResult<SendMessageResponse> {
        // Save outgoing message to local DB immediately
        val entity = ChatMessageEntity(
            id = "local_${System.currentTimeMillis()}",
            sessionId = request.sessionId,
            role = "USER",
            type = request.type.name,
            content = request.content,
            durationSeconds = request.metadata?.durationSeconds,
            imageWidth = request.metadata?.imageWidth,
            imageHeight = request.metadata?.imageHeight,
            createdAt = java.time.Instant.now().toString(),
            syncStatus = "pending"
        )
        chatMessageDao.insertMessage(entity)

        return runCatching {
            val response = api.sendMessage(request)
            val data = response.data
            if (response.code == 0 && data != null) {
                chatMessageDao.updateSyncStatus(entity.id, "synced")
                AppResult.Success(data)
            } else {
                chatMessageDao.updateSyncStatus(entity.id, "failed")
                AppResult.Error(response.code, response.message)
            }
        }.getOrElse {
            AppResult.Error(-1, it.message ?: "网络异常")
        }
    }

    suspend fun getSessions(page: Int = 1): AppResult<ChatSessionListResponse> {
        return runCatching {
            val response = api.getSessions(page = page)
            val data = response.data
            if (response.code == 0 && data != null) {
                AppResult.Success(data)
            } else {
                AppResult.Error(response.code, response.message)
            }
        }.getOrElse { AppResult.Error(-1, it.message ?: "网络异常") }
    }

    suspend fun getChatHistory(sessionId: String, page: Int = 1): AppResult<ChatHistoryResponse> {
        return runCatching {
            val response = api.getChatHistory(sessionId, page = page)
            val data = response.data
            if (response.code == 0 && data != null) {
                // Cache messages to local DB
                val entities = data.messages.map { it.toEntity() }
                chatMessageDao.insertMessages(entities)
                AppResult.Success(data)
            } else {
                AppResult.Error(response.code, response.message)
            }
        }.getOrElse { AppResult.Error(-1, it.message ?: "网络异常") }
    }

    fun getLocalMessages(sessionId: String): Flow<List<ChatMessage>> =
        chatMessageDao.getMessagesBySession(sessionId).map { entities ->
            entities.map { it.toDomain() }
        }
}

private fun ChatMessage.toEntity() = ChatMessageEntity(
    id = id,
    sessionId = sessionId,
    role = role.name,
    type = type.name,
    content = content,
    durationSeconds = metadata?.durationSeconds,
    imageWidth = metadata?.imageWidth,
    imageHeight = metadata?.imageHeight,
    createdAt = createdAt
)

private fun ChatMessageEntity.toDomain() = ChatMessage(
    id = id,
    sessionId = sessionId,
    role = com.zencare.model.dto.MessageRole.valueOf(role),
    type = com.zencare.model.dto.MessageType.valueOf(type),
    content = content,
    metadata = if (durationSeconds != null || imageWidth != null) {
        MessageMetadata(durationSeconds, imageWidth, imageHeight)
    } else null,
    createdAt = createdAt
)
