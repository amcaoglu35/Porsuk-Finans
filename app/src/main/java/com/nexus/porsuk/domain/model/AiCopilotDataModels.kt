package com.nexus.porsuk.domain.model

/**
 * LLM Sağlayıcı Türü (LlmProviderType)
 */
enum class LlmProviderType(val displayName: String, val iconEmoji: String) {
    OPENAI("OpenAI GPT-4o / Orakul", "🤖"),
    CLAUDE("Anthropic Claude 3.5 Sonnet", "🧠"),
    GEMINI("Google Gemini 1.5 Pro", "✨"),
    AZURE_OPENAI("Azure OpenAI Enterprise", "☁️"),
    OLLAMA("Ollama Local LLM", "🦙"),
    LOCAL_MODEL("On-Device Mobile Model", "📱");
}

/**
 * Mesaj Gönderen Türü (MessageSender)
 */
enum class MessageSender {
    USER,
    ASSISTANT,
    SYSTEM;
}

/**
 * Çıktı Biçimi (OutputFormat)
 */
enum class OutputFormat {
    MARKDOWN,
    RICH_TEXT,
    TABLE,
    BULLET_SUMMARY;
}

/**
 * Copilot İstem Kısayolu ve Niyeti (CopilotIntent)
 */
enum class CopilotIntent(val title: String, val promptTemplate: String, val iconEmoji: String) {
    PORTFOLIO_REVIEW("Portföy İncelemesi", "Portföyümün risk dağılımını, sektör yoğunlaşmasını ve performansını kurumsal düzeyde analiz et.", "📊"),
    COMPANY_ANALYSIS("Şirket SWOT Analizi", "THYAO şirketinin finansal oranlarını, büyüme potansiyelini ve SWOT analizini özetle.", "🏢"),
    VALUATION_EXPLANATION("Değerleme Analizi", "Fiyat/Kazanç (F/K) ve İndirgenmiş Nakit Akımları (DCF) değerleme yöntemini açıklayarak BIST 100 ile karşılaştır.", "💎"),
    DAILY_BRIEF("Borsa Günlük Özet", "Bugünkü BIST ve küresel piyasaların özetini, kritik haberleri ve makro ekonomik takvim gelişmelerini aktar.", "📰"),
    DIVIDEND_ADVICE("Temettü Stratejisi", "Yüksek temettü verimi ve temettü büyüme stratejisi arasındaki farkı ve portföye katkısını analiz et.", "💰");
}

/**
 * Sohbet Mesajı (ChatMessage)
 */
data class ChatMessage(
    val messageId: String = "msg_${System.currentTimeMillis()}",
    val sender: MessageSender = MessageSender.ASSISTANT,
    val content: String,
    val outputFormat: OutputFormat = OutputFormat.MARKDOWN,
    val providerUsed: LlmProviderType = LlmProviderType.OPENAI,
    val timestamp: Long = System.currentTimeMillis(),
    val isStreaming: Boolean = false
)

/**
 * Sohbet Oturumu (ConversationThread)
 */
data class ConversationThread(
    val threadId: String = "thread_${System.currentTimeMillis()}",
    val title: String = "Portföy Risk & Büyüme Sohbeti",
    val lastMessageSnippet: String = "Portföyün %35 Havacılık yoğunlaşması içeriyor...",
    val messageCount: Int = 8,
    val lastUpdatedTimestamp: Long = System.currentTimeMillis()
)

/**
 * Kullanıcı Finansal AI Hafızası (UserAiMemory)
 */
data class UserAiMemory(
    val investmentGoal: String = "BIST 100 Uzun Vadeli Büyüme & Temettü",
    val riskTolerance: String = "Orta - Yüksek Risk (Balanced Growth)",
    val favoriteSymbols: List<String> = listOf("THYAO.IS", "GARAN.IS", "AAPL", "BTC-USD"),
    val favoriteSectors: List<String> = listOf("Havacılık", "Bankacılık", "Teknoloji"),
    val totalConsultedQueriesCount: Long = 142L,
    val lastMemoryUpdateTimestamp: Long = System.currentTimeMillis()
)

/**
 * İstem Şablonu (SystemPromptTemplate)
 */
data class SystemPromptTemplate(
    val promptId: String,
    val name: String,
    val templateText: String,
    val category: String = "FINANCIAL_ANALYSIS",
    val version: String = "v2.1"
)

/**
 * Geleceğe Hazır AI Copilot & Agent Stub Modeli (AiFutureStubs)
 */
data class AiFutureStubs(
    val isVoiceAssistantReady: Boolean = true,
    val isCameraDocumentAnalysisReady: Boolean = true,
    val isMultiAgentCollaborationActive: Boolean = true,
    val isAutonomousResearchAgentEnabled: Boolean = false,
    val isMcpToolCallingSupported: Boolean = true,
    val isOnDeviceAiActive: Boolean = false
)
