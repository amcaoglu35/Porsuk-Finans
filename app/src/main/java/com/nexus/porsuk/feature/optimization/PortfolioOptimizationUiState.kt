package com.nexus.porsuk.feature.optimization

import com.nexus.porsuk.domain.model.*

/**
 * Porsuk Portfolio Optimization & Asset Allocation Engine — UI Ekran Durumu (PortfolioOptimizationUiState)
 */
data class PortfolioOptimizationUiState(
    val selectedStrategy: OptimizationStrategyType = OptimizationStrategyType.MAXIMUM_SHARPE,
    val allocations: List<AssetAllocationItem> = emptyList(),
    val riskMetrics: PortfolioRiskMetrics = PortfolioRiskMetrics(),
    val frontierPoints: List<EfficientFrontierPoint> = emptyList(),
    val rebalanceSuggestions: List<RebalanceSuggestion> = emptyList(),
    val stressTestScenarios: List<StressTestScenario> = emptyList(),
    val selectedScenarioChangePct: Double? = null,
    val futureStubs: PortfolioOptimizationFutureStubs = PortfolioOptimizationFutureStubs(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
