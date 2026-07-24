package com.nexus.porsuk.feature.backtest

import com.nexus.porsuk.domain.model.*

/**
 * Porsuk Backtesting Engine — UI Ekran Durumu (BacktestUiState)
 */
data class BacktestUiState(
    val config: BacktestConfig = BacktestConfig(),
    val report: BacktestReport? = null,
    val savedReports: List<BacktestReport> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
