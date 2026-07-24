package com.nexus.porsuk.domain.repository

import com.nexus.porsuk.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * 1. AI Çalışma Alanı Deposu Sözleşmesi (AIWorkspaceRepository)
 */
interface AIWorkspaceRepository {
    fun getWorkspaceReport(type: AiWorkspaceType): Flow<AiWorkspaceReport>
}

/**
 * 2. AI Sohbet Akış Deposu Sözleşmesi (ChatRepository)
 */
interface ChatRepository {
    fun getChatMessages(): Flow<List<AiChatMessage>>
    suspend fun sendMessage(userText: String): Flow<AiChatMessage>
}

/**
 * 3. Otomatik AI Analiz Sorgu Deposu Sözleşmesi (AiAnalysisQueryRepository)
 */
interface AiAnalysisQueryRepository {
    fun parseNaturalLanguageQuery(queryText: String): Flow<NaturalLanguageScanQuery>
}

/**
 * 4. Prompt Kütüphanesi Deposu Sözleşmesi (PromptLibraryRepository)
 */
interface PromptLibraryRepository {
    fun getPromptTemplates(): Flow<List<AiPromptTemplate>>
}

/**
 * 5. Saklanan İçerik Deposu Sözleşmesi (WorkspaceRepository)
 */
interface WorkspaceRepository {
    fun getSavedReports(): Flow<List<AiWorkspaceReport>>
}
