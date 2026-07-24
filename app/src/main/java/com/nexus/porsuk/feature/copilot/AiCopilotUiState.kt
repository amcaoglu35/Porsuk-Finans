package com.nexus.porsuk.feature.copilot

import com.nexus.porsuk.domain.model.*

/**
 * Porsuk AI Copilot & Autonomous Investment Assistant — UI Ekran Durumu (AiCopilotUiState)
 */
data class AiCopilotUiState(
    val activeProvider: LlmProviderType = LlmProviderType.OPENAI,
    val activeThreadId: String = "thread_default",
    val threads: List<ConversationThread> = emptyList(),
    val messages: List<ChatMessage> = emptyList(),
    val userMemory: UserAiMemory = UserAiMemory(),
    val futureStubs: AiFutureStubs = AiFutureStubs(),
    val isStreamingResponse: Boolean = false,
    val currentPromptInput: String = "",
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
