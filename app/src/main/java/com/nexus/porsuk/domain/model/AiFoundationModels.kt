package com.nexus.porsuk.domain.model

/**
 * Dinamik Prompt Şablon Modeli (AiPromptTemplate)
 */
data class AiPromptTemplate(
    val templateId: String,
    val title: String,
    val rawPromptText: String,
    val category: String,
    val version: Int = 1
)

/**
 * Dinamik Bağlam Çerçevesi (AiContextFrame)
 */
data class AiContextFrame(
    val portfolioContextJson: String? = null,
    val watchlistContextJson: String? = null,
    val companyContextJson: String? = null,
    val marketContextJson: String? = null,
    val userPreferencesJson: String? = null
)

/**
 * AI Bellek Oturumu Modeli (AiMemorySession)
 */
data class AiMemorySession(
    val sessionId: String,
    val conversationSummary: String,
    val tokenCount: Int,
    val lastActiveTimestamp: Long = System.currentTimeMillis()
)

/**
 * AI Maliyet ve Kullanım İstatistiği (AiCostSummary)
 */
data class AiCostSummary(
    val totalPromptTokens: Long = 14500,
    val totalCompletionTokens: Long = 8200,
    val estimatedCostUsd: Double = 0.042,
    val activeProvider: AiProviderType = AiProviderType.GEMINI
)

/**
 * Tool Calling Araç Tanımı (AiToolDefinition)
 */
data class AiToolDefinition(
    val toolName: String,
    val description: String,
    val parameterJsonSchema: String
)

/**
 * Geleceğe Hazır RAG Vector Store Stub Modeli
 */
data class RagVectorStoreStub(
    val indexName: String = "porsuk_financial_knowledge_index",
    val totalChunkCount: Int = 1250,
    val embeddingModelName: String = "text-embedding-004",
    val retrievalAccuracyPct: Double = 96.4
)
