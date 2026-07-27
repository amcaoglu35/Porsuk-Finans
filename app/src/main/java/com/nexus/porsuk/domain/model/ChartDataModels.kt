package com.nexus.porsuk.domain.model

/**
 * Mum Verisi (CandleStickItem)
 */
data class CandleStickItem(
    val timestamp: Long,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Double
)

/**
 * Grafik Çizim Nesnesi
 */
data class ChartDrawingObject(
    val id: String = "draw_${System.currentTimeMillis()}",
    val type: DrawingToolType,
    val startTimestamp: Long,
    val startPrice: Double,
    val endTimestamp: Long? = null,
    val endPrice: Double? = null,
    val text: String? = null,
    val colorHex: Long = 0xFF00A388,
    // Add legacy support fields if needed, but let's try to unify
    val toolType: DrawingToolType = type,
    val textNote: String? = text
)

/**
 * Portföy İşlem İşaretçisi
 */
data class PortfolioTransactionMarker(
    val id: String,
    val symbol: String,
    val markerType: OverlayMarkerType,
    val price: Double,
    val timestamp: Long,
    val quantityText: String
)

/**
 * Grafik Ayarları
 */
data class ChartSettings(
    val chartType: ChartType = ChartType.CANDLESTICK,
    val timeFrame: ChartTimeFrame = ChartTimeFrame.DAILY,
    val showVolume: Boolean = true,
    val showGrid: Boolean = true,
    val activeIndicators: List<IndicatorConfig> = emptyList(),
    val drawings: List<ChartDrawingObject> = emptyList()
)

/**
 * İndikatör Yapılandırması
 */
data class IndicatorConfig(
    val type: IndicatorType,
    val params: Map<String, Any> = emptyMap(),
    val isVisible: Boolean = true
)

/**
 * AI Grafik Analiz Sonucu
 */
data class AiChartAnalysis(
    val trend: String,
    val pattern: String?,
    val supportLevels: List<Double>,
    val resistanceLevels: List<Double>,
    val riskScore: Int,
    val confidence: Int,
    val scenario: String,
    val signal: TechnicalSignalType
)
