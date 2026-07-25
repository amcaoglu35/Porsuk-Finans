package com.nexus.porsuk.feature.institutional

import com.nexus.porsuk.domain.model.*

/**
 * Institutional Intelligence Ekran Sekmeleri
 */
enum class InstitutionalTab(val title: String, val iconEmoji: String) {
    INSTITUTIONAL_HOLDINGS("Kurumsal Fonlar (13F)", "🏛️"),
    INSIDER_TRADING("Insider Yönetici İşlemleri", "🕵️"),
    OWNERSHIP_STRUCTURE("Sahiplik & Konsolidasyon", "📊"),
    WHALE_TRACKER("Balina Takibi & Akıllı Para", "🐋"),
    AI_INTELLIGENCE("AI Akıllı Para Analizi", "🤖");
}

/**
 * UI State Modeli (InstitutionalUiState)
 */
data class InstitutionalUiState(
    val activeTab: InstitutionalTab = InstitutionalTab.INSTITUTIONAL_HOLDINGS,
    val selectedSymbol: String = "THYAO.IS",
    val selectedProvider: InstitutionalProviderType = InstitutionalProviderType.SEC_13F,

    // Institutional Holdings
    val topInvestors: List<InstitutionalInvestor> = emptyList(),
    val fundHoldings: List<InstitutionalHoldingItem> = emptyList(),
    val topBuyers: List<InstitutionalHoldingItem> = emptyList(),
    val topSellers: List<InstitutionalHoldingItem> = emptyList(),

    // Insider Trades
    val insiderTrades: List<InsiderTradeRecord> = emptyList(),
    val selectedRoleFilter: InsiderRoleType? = null,
    val netInsiderActivity: NetInsiderActivity? = null,

    // Ownership Structure
    val ownershipBreakdown: OwnershipBreakdown? = null,
    val ownershipHistory: List<OwnershipHistoryPoint> = emptyList(),

    // Whale Tracker & Smart Money
    val whaleAlerts: List<WhaleAlert> = emptyList(),
    val smartMoneyFlow: SmartMoneyFlowSummary? = null,

    // AI Intelligence
    val aiCommentary: SmartMoneyAiCommentary? = null,
    val futureStubs: InstitutionalFutureStubs = InstitutionalFutureStubs(),

    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
