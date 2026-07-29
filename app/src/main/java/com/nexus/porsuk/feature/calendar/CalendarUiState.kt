package com.nexus.porsuk.feature.calendar

import com.nexus.porsuk.domain.model.*

/**
 * Porsuk Economic Calendar Engine — UI Ekran Durumu (CalendarUiState)
 */
data class CalendarUiState(
    val viewMode: CalendarViewMode = CalendarViewMode.DAILY,
    val selectedCategory: CalendarEventCategory = CalendarEventCategory.ALL,
    val selectedCountry: String? = null,
    val selectedImpactLevel: CalendarImpactLevel? = null,
    val allEvents: List<EconomicEvent> = emptyList(),
    val filteredEvents: List<EconomicEvent> = emptyList(),
    val earningsEvents: List<EarningsEvent> = emptyList(),
    val dividendEvents: List<DividendEvent> = emptyList(),
    val ipoEvents: List<IpoIntelligence> = emptyList(),
    val dailyAiSummary: String? = null,
    val isLoading: Boolean = true,
    val isAiLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedTab: Int = 0,

    // Integrated from legacy ui.calendar
    val selectedDividendMarket: String = "Tümü",
    val selectedIpoStatus: String = "Tümü",
    val calcShares: String = "",
    val calcRate: String = "",
    val calcResult: Double? = null,
    val aiInsightText: String = "",
    val aiError: String? = null,
    val hasGeminiKey: Boolean = false,
    val activeIpoAlarms: Set<String> = emptySet()
)
