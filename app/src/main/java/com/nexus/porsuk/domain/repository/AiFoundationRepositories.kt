package com.nexus.porsuk.domain.repository

import com.nexus.porsuk.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * 1. AI Temel Depo Sözleşmesi (AIRepository)
 */
interface AIRepository {
    fun generateCompletion(prompt: String, symbol: String? = null): Flow<String>
}

/**
 * 2. Prompt Şablonları Deposu Sözleşmesi (PromptRepository)
 */
interface PromptRepository {
    fun getPromptTemplates(): Flow<List<AiPromptTemplate>>
}

/**
 * 3. AI Bağlam Deposu Sözleşmesi (ContextRepository)
 */
interface ContextRepository {
    fun getCurrentContext(symbol: String? = null): Flow<AiContextFrame>
}

/**
 * 4. AI Bellek Deposu Sözleşmesi (MemoryRepository)
 */
interface MemoryRepository {
    fun getSessionMemory(sessionId: String): Flow<AiMemorySession>
}

/**
 * 5. AI Sağlayıcı & Maliyet Deposu Sözleşmesi (ProviderRepository)
 */
interface ProviderRepository {
    fun getActiveProvider(): Flow<AiProviderType>
    fun getCostSummary(): Flow<AiCostSummary>
}
