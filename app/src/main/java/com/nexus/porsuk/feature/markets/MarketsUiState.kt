package com.nexus.porsuk.feature.markets

import com.nexus.porsuk.domain.model.AssetCategory
import com.nexus.porsuk.domain.model.MarketQuote

/**
 * Porsuk Markets Module — Piyasa Sıralama Seçenekleri
 */
enum class MarketSortOption(val displayName: String) {
    DEFAULT("Varsayılan Sıralama"),
    CHANGE_PERCENT_DESC("En Çok Yükselenler (% Değişim)"),
    CHANGE_PERCENT_ASC("En Çok Düşenler (% Değişim)"),
    PRICE_DESC("En Yüksek Fiyat"),
    PRICE_ASC("En Düşük Fiyat"),
    VOLUME_DESC("En Yüksek Hacim"),
    SYMBOL_ASC("Sembol (A-Z)");
}

/**
 * Porsuk Markets Module — 10 Piyasa Sekmesi
 */
enum class MarketTab(val title: String, val category: AssetCategory?) {
    FAVORITES("Favoriler", null),
    BIST("BIST", AssetCategory.BIST_STOCK),
    USA("ABD", AssetCategory.NASDAQ_STOCK),
    EUROPE("Avrupa", AssetCategory.EUROPE_STOCK),
    ETF("ETF", AssetCategory.ETF),
    TEFAS("TEFAS Fonları", AssetCategory.MUTUAL_FUND),
    INDICES("Endeksler", AssetCategory.INDEX),
    FOREX("Döviz", AssetCategory.CURRENCY),
    COMMODITY("Emtialar", AssetCategory.COMMODITY),
    CRYPTO("Kripto", AssetCategory.CRYPTO);
}

/**
 * Porsuk Markets Module — UI Ekran Durumu (MarketsUiState)
 */
data class MarketsUiState(
    val selectedTab: MarketTab = MarketTab.BIST,
    val searchQuery: String = "",
    val sortOption: MarketSortOption = MarketSortOption.DEFAULT,
    val isGridView: Boolean = false,
    val favoriteSymbols: Set<String> = emptySet(),
    val quotesList: List<MarketQuote> = emptyList(),
    val filteredQuotesList: List<MarketQuote> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
