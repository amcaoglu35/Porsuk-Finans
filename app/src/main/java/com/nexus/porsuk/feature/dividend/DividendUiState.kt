package com.nexus.porsuk.feature.dividend

import com.nexus.porsuk.domain.model.*

/**
 * Porsuk Dividend Intelligence Center — UI Ekran Durumu (DividendUiState)
 */
data class DividendUiState(
    val projection: IncomeProjection = IncomeProjection(),
    val stocks: List<DividendStockItem> = emptyList(),
    val upcomingDividends: List<DividendStockItem> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
