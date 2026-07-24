package com.nexus.porsuk.feature.orakul

import com.nexus.porsuk.domain.model.*

/**
 * Porsuk Orakul Core Engine — UI Ekran Durumu (OrakulUiState)
 */
data class OrakulUiState(
    val symbol: String = "THYAO.IS",
    val report: OrakulAnalysisReport? = null,
    val financialData: FinancialAnalysisData? = null,
    val technicalData: TechnicalAnalysisData? = null,
    val riskData: RiskAnalysisData? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
