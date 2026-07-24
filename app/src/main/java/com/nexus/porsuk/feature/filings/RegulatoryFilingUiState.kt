package com.nexus.porsuk.feature.filings

import com.nexus.porsuk.domain.model.*

/**
 * Porsuk Regulatory Filings & Disclosure Intelligence Platform — UI Ekran Durumu (RegulatoryFilingUiState)
 */
data class RegulatoryFilingUiState(
    val activeProvider: FilingProviderType = FilingProviderType.KAP_TURKEY,
    val selectedCategoryFilter: FilingCategory? = null,
    val searchQuery: String = "",
    val filings: List<RegulatoryFiling> = emptyList(),
    val companyTimeline: List<CompanyTimelineEvent> = emptyList(),
    val activeAiSummary: FilingAiSummary? = null,
    val selectedFilingForSummary: RegulatoryFiling? = null,
    val futureStubs: RegulatoryFutureStubs = RegulatoryFutureStubs(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
