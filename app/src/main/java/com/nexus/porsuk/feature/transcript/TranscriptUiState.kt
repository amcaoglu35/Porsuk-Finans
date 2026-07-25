package com.nexus.porsuk.feature.transcript

import com.nexus.porsuk.domain.model.*

/**
 * Transkript Platformu Sekmeleri
 */
enum class TranscriptTab(val title: String, val iconEmoji: String) {
    TRANSCRIPT_VIEWER("Transkript & Akış", "🎙️"),
    MANAGEMENT_ANALYSIS("Yönetici İfadeleri & Guidance", "👔"),
    QNA_INTELLIGENCE("Soru - Cevap Analizi", "💬"),
    AI_INTELLIGENCE("AI Özet & Sinyaller", "🤖"),
    SEARCH_ANALYTICS("Transkript İçi Arama & Trendler", "🔍");
}

/**
 * Transcript Platform Ekran Durumu (TranscriptUiState)
 */
data class TranscriptUiState(
    val activeTab: TranscriptTab = TranscriptTab.TRANSCRIPT_VIEWER,
    val selectedSymbol: String = "THYAO.IS",
    val selectedCallId: String = "call_q2_2026",
    val selectedCallType: EarningsCallType? = null,

    // Calls Archive
    val recentCalls: List<EarningsCallTranscript> = emptyList(),
    val currentCall: EarningsCallTranscript? = null,
    val utterances: List<TranscriptUtterance> = emptyList(),
    val speakers: List<SpeakerInfo> = emptyList(),

    // Management & Guidance
    val managementAnalysis: ManagementAnalysis? = null,

    // Q&A Intelligence
    val qnaExchanges: List<QnaExchange> = emptyList(),

    // AI & Visuals
    val aiSummary: TranscriptAiSummary? = null,
    val visuals: TranscriptVisuals? = null,
    val futureStubs: TranscriptFutureStubs = TranscriptFutureStubs(),

    // Search Engine State
    val searchQuery: String = "",
    val searchResults: List<TranscriptSearchResult> = emptyList(),

    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
