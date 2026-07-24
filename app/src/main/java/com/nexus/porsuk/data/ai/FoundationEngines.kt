package com.nexus.porsuk.data.ai

import com.nexus.porsuk.domain.model.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 1. Prompt Motoru (PromptEngine)
 */
@Singleton
class PromptEngine @Inject constructor() {

    fun buildPrompt(template: AiPromptTemplate, variables: Map<String, String>): String {
        var result = template.rawPromptText
        variables.forEach { (key, value) ->
            result = result.replace("{$key}", value)
        }
        return result
    }

    fun getDefaultTemplates(): List<AiPromptTemplate> {
        return listOf(
            AiPromptTemplate("p1", "Şirket Temel Analiz İstemi", "Lütfen {symbol} şirketinin F/K, P/B ve Altman Z skorunu inceleyin.", "Şirket"),
            AiPromptTemplate("p2", "Portföy Sağlık İncelemesi", "Portföy risk dağılımını ve sektör yoğunlaşmasını değerlendirin.", "Portföy"),
            AiPromptTemplate("p3", "Doğal Dil Piyasa Taraması", "Bana {minYield}% üzeri temettü veren düşük riskli hisseleri listeleyin.", "Tarama")
        )
    }
}

/**
 * 2. Bağlam Motoru (ContextEngine)
 */
@Singleton
class ContextEngine @Inject constructor() {

    fun buildCurrentContext(symbol: String? = null): AiContextFrame {
        return AiContextFrame(
            portfolioContextJson = "{\"portfolioValueUsd\": 25000.0, \"riskLevel\": \"MODERATE\"}",
            watchlistContextJson = "[\"THYAO.IS\", \"AKBNK.IS\", \"NVDA\"]",
            companyContextJson = symbol?.let { "{\"symbol\": \"$it\", \"peRatio\": 4.85}" },
            marketContextJson = "{\"bist100ChangePct\": 1.85, \"sp500ChangePct\": 0.95}",
            userPreferencesJson = "{\"currency\": \"TRY\", \"riskTolerance\": \"GROWTH\"}"
        )
    }
}

/**
 * 3. Bellek Motoru (MemoryEngine)
 */
@Singleton
class MemoryEngine @Inject constructor() {

    private val sessionMemoryMap = mutableMapOf<String, AiMemorySession>()

    fun getOrCreateSession(sessionId: String): AiMemorySession {
        return sessionMemoryMap.getOrPut(sessionId) {
            AiMemorySession(sessionId = sessionId, conversationSummary = "Porsuk Orakul AI Oturumu Başlatıldı.", tokenCount = 150)
        }
    }
}

/**
 * 4. Tool Calling Araç Çağırma Motoru (ToolCallingEngine)
 */
@Singleton
class ToolCallingEngine @Inject constructor() {

    fun getRegisteredTools(): List<AiToolDefinition> {
        return listOf(
            AiToolDefinition("getCompanyFinancials", "Şirket bilanço ve gelir tablosunu getirir.", "{\"type\": \"object\", \"properties\": {\"symbol\": {\"type\": \"string\"}}}"),
            AiToolDefinition("calculatePortfolioHealth", "Portföy sağlık ve risk skorunu hesaplar.", "{\"type\": \"object\", \"properties\": {\"portfolioId\": {\"type\": \"string\"}}}"),
            AiToolDefinition("runSmartScan", "Belirtilen kriterlerle piyasa taraması çalıştırır.", "{\"type\": \"object\", \"properties\": {\"criteria\": {\"type\": \"string\"}}}")
        )
    }
}

/**
 * 5. AI Maliyet ve Token Motoru (AiCostManagerEngine)
 */
@Singleton
class AiCostManagerEngine @Inject constructor() {

    fun getCostSummary(): AiCostSummary {
        return AiCostSummary(
            totalPromptTokens = 24500,
            totalCompletionTokens = 12400,
            estimatedCostUsd = 0.068,
            activeProvider = AiProviderType.GEMINI
        )
    }
}
