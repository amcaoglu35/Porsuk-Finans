package com.nexus.porsuk.feature.risk

import com.nexus.porsuk.domain.model.RiskIntelligenceReport

/**
 * Porsuk Risk Engine — UI Ekran Durumu (RiskUiState)
 */
data class RiskUiState(
    val symbol: String = "THYAO.IS",
    val report: RiskIntelligenceReport? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
