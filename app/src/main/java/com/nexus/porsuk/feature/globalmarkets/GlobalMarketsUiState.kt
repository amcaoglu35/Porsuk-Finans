package com.nexus.porsuk.feature.globalmarkets

import com.nexus.porsuk.domain.model.*

/**
 * Porsuk Global Markets Center — UI Ekran Durumu (GlobalMarketsUiState)
 */
data class GlobalMarketsUiState(
    val selectedRegion: MarketRegion = MarketRegion.TURKEY,
    val exchangeStatus: ExchangeStatusInfo? = null,
    val tickers: List<MarketTickerItem> = emptyList(),
    val sectors: List<SectorPerformanceItem> = emptyList(),
    val heatMapData: WorldHeatMapData? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
