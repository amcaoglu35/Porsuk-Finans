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
    val compareCandles: Map<String, List<CandleStickItem>> = emptyMap(),
    val indicators: Map<IndicatorType, List<Double>> = emptyMap(),
    val drawings: List<ChartDrawingObject> = emptyList(),
    val portfolioMarkers: List<PortfolioTransactionMarker> = emptyList(),
    val showPortfolioOverlay: Boolean = true,
    val isLoading: Boolean = true,
    val isAiLoading: Boolean = false,
    val aiAnalysis: AiChartAnalysis? = null,
    val errorMessage: String? = null,
    val settings: ChartSettings = ChartSettings()
)

data class ChartSettings(
    val chartType: ChartType = ChartType.CANDLESTICK,
    val showVolume: Boolean = true
)
