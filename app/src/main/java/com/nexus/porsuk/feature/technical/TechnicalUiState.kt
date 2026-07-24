package com.nexus.porsuk.feature.technical

import com.nexus.porsuk.domain.model.*

/**
 * Porsuk Technical Engine — UI Ekran Durumu (TechnicalUiState)
 */
data class TechnicalUiState(
    val symbol: String = "THYAO.IS",
    val selectedTimeFrame: TimeFrame = TimeFrame.DAILY,
    val selectedCategory: IndicatorCategory = IndicatorCategory.TREND,
    val report: TechnicalAnalysisReport? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
