package com.nexus.porsuk.feature.ailab

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Porsuk AI Lab Platform — ViewModel
 *
 * 6 AI çalışma alanını (Chat, Analysis, Research, Portfolio, Strategy, Market), sohbet geçmişini ve prompt kütüphanesini yönetir.
 */
@HiltViewModel
class AiLabViewModel @Inject constructor(
    private val aiWorkspaceRepository: AIWorkspaceRepository,
    private val chatRepository: ChatRepository,
    private val promptLibraryRepository: PromptLibraryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AiLabUiState())
    val uiState: StateFlow<AiLabUiState> = _uiState.asStateFlow()

    init {
        loadChatMessages()
        loadPromptTemplates()
        loadWorkspaceReport(_uiState.value.selectedWorkspace)
    }

    fun selectWorkspace(workspace: AiWorkspaceType) {
        _uiState.update { it.copy(selectedWorkspace = workspace, isLoading = true) }
        loadWorkspaceReport(workspace)
    }

    fun onInputTextChange(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun sendMessage() {
        val text = _uiState.value.inputText
        if (text.isBlank()) return

        _uiState.update { it.copy(inputText = "") }
        viewModelScope.launch {
            chatRepository.sendMessage(text).collect { newMsg ->
                loadChatMessages()
            }
        }
    }

    private fun loadChatMessages() {
        viewModelScope.launch {
            chatRepository.getChatMessages().collect { list ->
                _uiState.update { it.copy(chatMessages = list, isLoading = false) }
            }
        }
    }

    private fun loadPromptTemplates() {
        viewModelScope.launch {
            promptLibraryRepository.getPromptTemplates().collect { list ->
                _uiState.update { it.copy(promptTemplates = list) }
            }
        }
    }

    private fun loadWorkspaceReport(type: AiWorkspaceType) {
        viewModelScope.launch {
            aiWorkspaceRepository.getWorkspaceReport(type).collect { rep ->
                _uiState.update { it.copy(currentReport = rep, isLoading = false) }
            }
        }
    }
}
