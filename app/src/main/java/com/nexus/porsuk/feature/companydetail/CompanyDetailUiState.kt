package com.nexus.porsuk.feature.companydetail

import com.nexus.porsuk.data.local.entity.*
import com.nexus.porsuk.domain.model.MarketQuote

/**
 * Porsuk Company Detail Module — 7 Sekme Tanımları
 */
enum class CompanyDetailTab(val title: String) {
    OVERVIEW("Genel Bilgiler"),
    FINANCIALS("Finansallar"),
    DIVIDENDS("Temettü"),
    EARNINGS("Kazançlar"),
    NEWS("Haberler"),
    STATS("İstatistikler"),
    AI_ORAKUL("AI Orakul (Gelecek)")
}

/**
 * Porsuk Company Detail Module — UI Ekran Durumu (CompanyDetailUiState)
 */
data class CompanyDetailUiState(
    val symbol: String = "",
    val company: CompanyEntity? = null,
    val quote: MarketQuote? = null,
    val dividends: List<DividendEntity> = emptyList(),
    val earnings: List<EarningsEntity> = emptyList(),
    val news: List<NewsEntity> = emptyList(),
    val aiHistory: AIHistoryEntity? = null,
    val isFavorite: Boolean = false,
    val selectedTab: CompanyDetailTab = CompanyDetailTab.OVERVIEW,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
