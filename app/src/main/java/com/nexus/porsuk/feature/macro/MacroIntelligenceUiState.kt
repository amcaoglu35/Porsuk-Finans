package com.nexus.porsuk.feature.macro

import com.nexus.porsuk.domain.model.*

/**
 * Porsuk Macro Intelligence Platform — UI Ekran Durumu (MacroIntelligenceUiState)
 */
data class MacroIntelligenceUiState(
    val activeTab: MacroDashboardTab = MacroDashboardTab.GLOBAL_HEATMAP,
    val selectedProvider: MacroProviderType = MacroProviderType.TCMB_TURKEY,
    val indicators: List<EconomicIndicator> = emptyList(),
    val centralBankPolicies: List<CentralBankPolicy> = emptyList(),
    val bondYields: List<BondYieldItem> = emptyList(),
    val fxCrosses: Map<String, Double> = emptyMap(),
    val commodities: List<CommodityItem> = emptyList(),
    val aiOutlook: MacroAiOutlook = MacroAiOutlook(),
    val futureStubs: MacroFutureStubs = MacroFutureStubs(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
