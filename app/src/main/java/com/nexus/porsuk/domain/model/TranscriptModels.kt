package com.nexus.porsuk.domain.model

/**
 * Earnings Call Toplantı Türleri
 */
enum class EarningsCallType(val code: String, val title: String, val iconEmoji: String) {
    QUARTERLY("Q_CALL", "Çeyreklik Bilanço Toplantısı (Quarterly)", "📊"),
    ANNUAL("A_CALL", "Yıllık Bilanço Toplantısı (Annual)", "📅"),
    INVESTOR_DAY("INV_DAY", "Yatırımcı Günü (Investor Day)", "🏛️"),
    CAPITAL_MARKETS_DAY("CMD", "Sermaye Piyasaları Günü", "📈"),
    SPECIAL_CONFERENCE("CONF", "Özel Konferans / Duyuru", "📢");
}

/**
 * Konuşmacı Unvan / Rolü
 */
enum class SpeakerRole(val displayName: String, val isManagement: Boolean) {
    CEO("Chief Executive Officer (CEO)", true),
    CFO("Chief Financial Officer (CFO)", true),
    EXECUTIVE("Üst Düzey Yönetici / COO / VP", true),
    ANALYST("Finansal Analist / Yatırım Bankası", false),
    MODERATOR("Toplantı Moderatörü / IR", false);
}

/**
 * Soru-Cevap Kategorisi
 */
enum class QnaCategory(val title: String, val iconEmoji: String) {
    REVENUE_GROWTH("Gelir & Büyüme", "🚀"),
    MARGINS_PROFITABILITY("Marjlar & Kârlılık", "💰"),
    CAPEX_INVESTMENT("Yatırımlar & CapEx", "🏗️"),
    MACRO_INFLATION("Makroekonomi & Enflasyon", "🌍"),
    AI_TECHNOLOGY("Yapay Zeka & Teknoloji", "🤖"),
    COMPETITION_MARKET("Rekabet & Pazar Payı", "⚔️");
}

/**
 * Konuşmacı Profil Bilgisi
 */
data class SpeakerInfo(
    val speakerId: String,
    val name: String,
    val title: String,
    val organization: String,
    val role: SpeakerRole
)

/**
 * Transkript Konuşma Paragrafı (Utterance)
 */
data class TranscriptUtterance(
    val utteranceId: String,
    val speaker: SpeakerInfo,
    val text: String,
    val timestampMs: Long,
    val timeLabel: String, // e.g. "12:45"
    val sentimentScore: Double, // -1.0 (Bearish) to +1.0 (Bullish)
    val topicTag: String,
    val isGuidanceStatement: Boolean = false,
    val isRiskStatement: Boolean = false,
    val isBookmarked: Boolean = false
)

/**
 * Earnings Call Transkripti Modeli
 */
data class EarningsCallTranscript(
    val callId: String,
    val symbol: String,
    val companyName: String,
    val period: String, // e.g. "2026-Q2"
    val dateLabel: String,
    val callType: EarningsCallType = EarningsCallType.QUARTERLY,
    val durationMinutes: Int = 45,
    val audioUrl: String? = null,
    val utterances: List<TranscriptUtterance> = emptyList()
)

/**
 * Soru-Cevap Eşleşmesi (Q&A Exchange)
 */
data class QnaExchange(
    val exchangeId: String,
    val analystName: String,
    val analystFirm: String,
    val questionUtterance: TranscriptUtterance,
    val managementAnswers: List<TranscriptUtterance>,
    val category: QnaCategory
)

/**
 * Yönetici Açıklamaları & Gelecek Öngörüleri (Management Analysis)
 */
data class ManagementAnalysis(
    val symbol: String,
    val ceoOpeningStatementSummary: String,
    val cfoFinancialReviewSummary: String,
    val guidanceStatements: List<String>,
    val forwardLookingStatements: List<String>,
    val riskCommentaryList: List<String>
)

/**
 * AI Akıllı Transkript Özeti & Metrikleri
 */
data class TranscriptAiSummary(
    val callId: String,
    val executiveSummary: String,
    val bullishSignals: List<String>,
    val bearishSignals: List<String>,
    val overallSentiment: String, // "Strongly Bullish", "Neutral", "Cautious"
    val managementConfidenceScore: Double, // 0 to 100
    val guidanceComparisonText: String,
    val qOqComparisonText: String
)

/**
 * Transkript Arama Eşleşmesi
 */
data class TranscriptSearchResult(
    val callId: String,
    val symbol: String,
    val period: String,
    val matchedUtterance: TranscriptUtterance,
    val highlightedSnippet: String
)

/**
 * Transkript Görselleştirme Verileri
 */
data class TranscriptVisuals(
    val speakerTalkTimeDistribution: Map<String, Double>, // Speaker Name -> Talk Time %
    val quarterlySentimentTimeline: List<TimestampValuePair>,
    val topicHeatmapGrid: Map<String, Int> // Topic -> Mention Count
)

/**
 * Geleceğe Hazır Transkript Stubs
 */
data class TranscriptFutureStubs(
    val isVoiceToTextActive: Boolean = false,
    val isLiveCallActive: Boolean = false,
    val isRealtimeAiSummaryActive: Boolean = false,
    val isVoiceEmotionDetectionActive: Boolean = false,
    val isLlmQnaAssistantActive: Boolean = true
)
