package com.nexus.porsuk.feature.ma

import com.nexus.porsuk.domain.model.*

/**
 * Corporate Events Platform Sekmeleri
 */
enum class CorporateEventTab(val title: String, val iconEmoji: String) {
    MA_DEALS("Birleşme & Satın Alım (M&A)", "🤝"),
    EVENTS_TIMELINE("Olaylar Zaman Çizelgesi", "📅"),
    DEAL_ANALYTICS("İşlem Analitiği & Çarpanlar", "📊"),
    IMPACT_SYNERGIES("Etki & Sinerji Analizi", "📈"),
    AI_INTELLIGENCE("AI M&A Zekası", "🤖");
}

/**
 * Corporate Events UI State Modeli (CorporateEventUiState)
 */
data class CorporateEventUiState(
    val activeTab: CorporateEventTab = CorporateEventTab.MA_DEALS,
    val selectedSymbol: String = "THYAO.IS",
    val selectedEventTypeFilter: CorporateEventType? = null,
    val selectedDealId: String = "deal_a1",

    // Deals & Events
    val corporateEvents: List<CorporateEvent> = emptyList(),
    val mergers: List<DealAnalyticsItem> = emptyList(),
    val acquisitions: List<DealAnalyticsItem> = emptyList(),
    val activeDeal: DealAnalyticsItem? = null,

    // Impact & Visuals
    val impactAnalysis: DealImpactAnalysis? = null,
    val aiIntelligence: DealAiIntelligence? = null,
    val visuals: DealVisuals? = null,
    val futureStubs: CorporateEventFutureStubs = CorporateEventFutureStubs(),

    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
