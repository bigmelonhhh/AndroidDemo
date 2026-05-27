package com.zencare.consultation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zencare.common.result.AppResult
import com.zencare.data.repository.ConsultationRepository
import com.zencare.model.dto.ChatMessage
import com.zencare.model.dto.ChatSession
import com.zencare.model.dto.MessageRole
import com.zencare.model.dto.MessageType
import com.zencare.model.dto.SendMessageRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ConsultationHomeUiState(
    val sessions: List<ChatSession> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class ChatDetailUiState(
    val sessionId: String = "",
    val messages: List<ChatMessage> = emptyList(),
    val isSending: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ConsultationViewModel @Inject constructor(
    private val repository: ConsultationRepository
) : ViewModel() {

    private val _homeState = MutableStateFlow(ConsultationHomeUiState())
    val homeState: StateFlow<ConsultationHomeUiState> = _homeState.asStateFlow()

    private val _chatState = MutableStateFlow(ChatDetailUiState())
    val chatState: StateFlow<ChatDetailUiState> = _chatState.asStateFlow()

    fun loadSessions() {
        viewModelScope.launch {
            _homeState.update { it.copy(isLoading = true, error = null) }
            when (val result = repository.getSessions()) {
                is AppResult.Success -> _homeState.update {
                    it.copy(isLoading = false, sessions = result.data.sessions)
                }
                is AppResult.Error -> _homeState.update {
                    it.copy(isLoading = false, error = result.message)
                }
                else -> {}
            }
        }
    }

    fun loadChatHistory(sessionId: String) {
        _chatState.update { it.copy(sessionId = sessionId, isLoading = true, error = null) }
        viewModelScope.launch {
            when (val result = repository.getChatHistory(sessionId)) {
                is AppResult.Success -> _chatState.update {
                    it.copy(isLoading = false, messages = result.data.messages)
                }
                is AppResult.Error -> _chatState.update {
                    it.copy(isLoading = false, error = result.message)
                }
                else -> {}
            }
        }
    }

    fun sendTextMessage(text: String) {
        val sessionId = _chatState.value.sessionId
        if (sessionId.isEmpty() || text.isBlank()) return
        sendMessage(MessageType.TEXT, text)
    }

    fun sendImageMessage(imageUrl: String) {
        val sessionId = _chatState.value.sessionId
        if (sessionId.isEmpty() || imageUrl.isBlank()) return
        sendMessage(MessageType.IMAGE, imageUrl)
    }

    fun sendVoiceMessage(voiceUrl: String, durationSeconds: Int) {
        val sessionId = _chatState.value.sessionId
        if (sessionId.isEmpty() || voiceUrl.isBlank()) return
        sendMessage(
            MessageType.VOICE, voiceUrl,
            com.zencare.model.dto.MessageMetadata(durationSeconds = durationSeconds)
        )
    }

    private fun sendMessage(type: MessageType, content: String, metadata: com.zencare.model.dto.MessageMetadata? = null) {
        viewModelScope.launch {
            _chatState.update { it.copy(isSending = true) }

            val tempMessage = ChatMessage(
                id = "temp_${System.currentTimeMillis()}",
                sessionId = _chatState.value.sessionId,
                role = MessageRole.USER,
                type = type,
                content = content,
                metadata = metadata,
                createdAt = java.time.Instant.now().toString()
            )
            _chatState.update { it.copy(messages = it.messages + tempMessage) }

            val request = SendMessageRequest(
                sessionId = _chatState.value.sessionId,
                type = type,
                content = content,
                metadata = metadata
            )

            when (val result = repository.sendMessage(request)) {
                is AppResult.Success -> {
                    _chatState.update { it.copy(isSending = false) }
                    loadChatHistory(_chatState.value.sessionId)
                }
                is AppResult.Error -> {
                    _chatState.update { it.copy(isSending = false, error = result.message) }
                }
                else -> {}
            }
        }
    }

    fun retry() {
        _chatState.update { it.copy(error = null) }
        loadChatHistory(_chatState.value.sessionId)
    }
}
