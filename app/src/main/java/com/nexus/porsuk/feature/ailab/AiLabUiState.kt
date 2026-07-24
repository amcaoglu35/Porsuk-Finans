package com.nexus.porsuk.feature.ailab

import com.nexus.porsuk.domain.model.*

/**
 * Porsuk AI Lab Platform — UI Ekran Durumu (AiLabUiState)
 */
data class AiLabUiState(
    val selectedWorkspace: AiWorkspaceType = AiWorkspaceType.CHAT,
    val chatMessages: List<AiChatMessage> = emptyList(),
    val promptTemplates: List<AiPromptTemplate> = emptyList(),
    val currentReport: AiWorkspaceReport? = null,
    val naturalLanguageQuery: NaturalLanguageScanQuery = NaturalLanguageScanQuery(),
    val inputText: String = "",
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
