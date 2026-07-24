package com.nexus.porsuk.feature.chart

import com.nexus.porsuk.domain.model.*

/**
 * Porsuk Professional Chart Center — UI Ekran Durumu (ChartUiState)
 */
data class ChartUiState(
    val symbol: String = "THYAO.IS",
    val selectedTimeFrame: ChartTimeFrame = ChartTimeFrame.DAILY,
    val selectedChartType: ChartType = ChartType.CANDLESTICK,
    val selectedTool: DrawingToolType = DrawingToolType.CROSSHAIR,
    val candles: List<CandleStickItem> = emptyList(),
    val drawings: List<ChartDrawingObject> = emptyList(),
    val portfolioMarkers: List<PortfolioTransactionMarker> = emptyList(),
    val showPortfolioOverlay: Boolean = true,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
