package com.nexus.porsuk.feature.quant

import com.nexus.porsuk.domain.model.*

/**
 * Porsuk Quant Research Studio — UI Ekran Durumu (QuantResearchUiState)
 */
data class QuantResearchUiState(
    val activeWorkspace: ResearchWorkspace = ResearchWorkspace(),
    val factorMetrics: List<FactorMetric> = emptyList(),
    val selectedFactorCategory: FactorCategory? = null,
    val statisticalResult: StatisticalAnalysisResult = StatisticalAnalysisResult(),
    val portfolioResearch: PortfolioResearchMetrics = PortfolioResearchMetrics(),
    val datasets: List<DatasetItem> = emptyList(),
    val savedWorkspaces: List<ResearchWorkspace> = emptyList(),
    val futureStubs: QuantFutureStubs = QuantFutureStubs(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
