package com.nexus.porsuk.domain.model

/**
 * 6 AI Çalışma Alanı (AI Workspace Types)
 */
enum class AiWorkspaceType(val displayName: String, val iconEmoji: String) {
    CHAT("AI Finansal Sohbet (Chat)", "💬"),
    ANALYSIS("Otomatik Şirket Analizi", "📊"),
    RESEARCH("Makro Araştırma & Haber", "🔎"),
    PORTFOLIO("Portföy AI Doktoru", "🩺"),
    STRATEGY("Strateji & Backtest AI", "📈"),
    MARKET("Piyasa Özeti (Market AI)", "🌐");
}

/**
 * AI Sohbet Mesaj Modeli (AiChatMessage)
 */
data class AiChatMessage(
    val messageId: String = "msg_${System.currentTimeMillis()}",
    val senderName: String,
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * AI Çalışma Alanı Rapor Modeli (AiWorkspaceReport)
 */
data class AiWorkspaceReport(
    val reportId: String = "rep_${System.currentTimeMillis()}",
    val title: String,
    val workspaceType: AiWorkspaceType,
    val summaryMarkdown: String,
    val generatedAt: Long = System.currentTimeMillis()
)

/**
 * Doğal Dil Tarama Sorgu Modeli (NaturalLanguageScanQuery)
 */
data class NaturalLanguageScanQuery(
    val userQueryText: String = "Bana düşük riskli yüksek temettü hisseleri göster.",
    val parsedCriteriaText: String = "Orakul AI Filtresi: Dividend Yield > %5.0, Risk Level = LOW",
    val matchedCount: Int = 8
)
