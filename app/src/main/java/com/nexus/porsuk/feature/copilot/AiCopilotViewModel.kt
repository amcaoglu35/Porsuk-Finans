package com.nexus.porsuk.feature.copilot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Porsuk AI Copilot & Autonomous Investment Assistant — ViewModel
 *
 * Akışlı istem yanıtları, LLM sağlayıcı değişimi, hafıza takibi ve hızlı istem şablonlarını yönetir.
 */
@HiltViewModel
class AiCopilotViewModel @Inject constructor(
    private val aiRepository: AiCopilotRepository,
    private val conversationRepository: ConversationRepository,
    private val memoryRepository: AiCopilotMemoryRepository,
    private val promptRepository: AiCopilotPromptRepository,
    private val workflowRepository: AiCopilotWorkflowRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AiCopilotUiState())
    val uiState: StateFlow<AiCopilotUiState> = _uiState.asStateFlow()

    init {
        loadCopilotData()
    }

    fun selectProvider(provider: LlmProviderType) {
        viewModelScope.launch {
            aiRepository.setActiveProvider(provider)
            _uiState.update { it.copy(activeProvider = provider) }
        }
    }

    fun onPromptInputChange(input: String) {
        _uiState.update { it.copy(currentPromptInput = input) }
    }

    fun sendUserPrompt(promptText: String = uiState.value.currentPromptInput) {
        if (promptText.trim().isEmpty()) return

        val userMessage = ChatMessage(
            sender = MessageSender.USER,
            content = promptText
        )

        viewModelScope.launch {
            val currentThreadId = uiState.value.activeThreadId
            conversationRepository.saveMessage(currentThreadId, userMessage)

            _uiState.update { current ->
                current.copy(
                    messages = current.messages + userMessage,
                    currentPromptInput = "",
                    isStreamingResponse = true
                )
            }

            aiRepository.streamCopilotResponse(promptText, uiState.value.activeProvider).collect { chunkMessage ->
                _uiState.update { current ->
                    val otherMessages = current.messages.filter { it.messageId != chunkMessage.messageId }
                    current.copy(
                        messages = otherMessages + chunkMessage,
                        isStreamingResponse = chunkMessage.isStreaming
                    )
                }
                if (!chunkMessage.isStreaming) {
                    conversationRepository.saveMessage(currentThreadId, chunkMessage)
                }
            }
        }
    }

    fun executeIntentShortcut(intent: CopilotIntent) {
        viewModelScope.launch {
            val formatted = promptRepository.getFormattedPrompt(intent, "THYAO.IS %35, GARAN.IS %25")
            sendUserPrompt(formatted)
        }
    }

    fun triggerWorkflowBrief() {
        viewModelScope.launch {
            val brief = workflowRepository.generateDailyBriefReport()
            _uiState.update { current -> current.copy(messages = current.messages + brief) }
        }
    }

    private fun loadCopilotData() {
        viewModelScope.launch {
            launch {
                conversationRepository.getConversationThreads().collect { threads ->
                    _uiState.update { it.copy(threads = threads, isLoading = false) }
                }
            }

            launch {
                conversationRepository.getThreadMessages("thread_default").collect { msgs ->
                    _uiState.update { it.copy(messages = msgs) }
                }
            }

            launch {
                memoryRepository.getUserAiMemory().collect { mem ->
                    _uiState.update { it.copy(userMemory = mem) }
                }
            }
        }
    }
}
