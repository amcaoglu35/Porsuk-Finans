package com.nexus.porsuk.domain.repository

import com.nexus.porsuk.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * 1. AI Copilot Yöneticisi Deposu Sözleşmesi (AiCopilotRepository)
 */
interface AiCopilotRepository {
    fun streamCopilotResponse(prompt: String, provider: LlmProviderType): Flow<ChatMessage>
    suspend fun getActiveProvider(): LlmProviderType
    suspend fun setActiveProvider(provider: LlmProviderType)
}

/**
 * 2. Sohbet Oturumları Deposu Sözleşmesi (ConversationRepository)
 */
interface ConversationRepository {
    fun getConversationThreads(): Flow<List<ConversationThread>>
    fun getThreadMessages(threadId: String): Flow<List<ChatMessage>>
    suspend fun createNewThread(title: String): ConversationThread
    suspend fun saveMessage(threadId: String, message: ChatMessage)
}

/**
 * 3. Kullanıcı Finansal Hafıza Deposu Sözleşmesi (AiCopilotMemoryRepository)
 */
interface AiCopilotMemoryRepository {
    fun getUserAiMemory(): Flow<UserAiMemory>
    suspend fun updateInvestmentGoal(goal: String)
    suspend fun addFavoriteSymbol(symbol: String)
}

/**
 * 4. İstem ve Şablon Yöneticisi Deposu Sözleşmesi (AiCopilotPromptRepository)
 */
interface AiCopilotPromptRepository {
    fun getSystemPromptTemplates(): Flow<List<SystemPromptTemplate>>
    suspend fun getFormattedPrompt(intent: CopilotIntent, contextData: String): String
}

/**
 * 5. Otomatik İş Akışları Deposu Sözleşmesi (AiCopilotWorkflowRepository)
 */
interface AiCopilotWorkflowRepository {
    suspend fun generateDailyBriefReport(): ChatMessage
    suspend fun generatePortfolioHealthCheck(): ChatMessage
}
