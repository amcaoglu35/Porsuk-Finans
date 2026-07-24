package com.nexus.porsuk.domain.model

/**
 * Fiyat Mumu Verisi (CandleStickItem)
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
 * Grafik Üzeri Çizim Nesnesi (ChartDrawingObject)
 */
data class ChartDrawingObject(
    val id: String,
    val toolType: DrawingToolType,
    val startTimestamp: Long,
    val startPrice: Double,
    val endTimestamp: Long? = null,
    val endPrice: Double? = null,
    val textNote: String? = null,
    val colorHex: Long = 0xFF00B0FF
)

/**
 * Portföy İşlem Türü (Transaction Type for Chart Overlay)
 */
enum class OverlayMarkerType(val displayName: String, val colorHex: Long) {
    BUY("Alış", 0xFF00C853),
    SELL("Satış", 0xFFD50000),
    DIVIDEND("Temettü", 0xFFFFD600);
}

/**
 * Portföy Alış/Satış/Temettü Grafik İşaretçisi (PortfolioTransactionMarker)
 */
data class PortfolioTransactionMarker(
    val markerId: String,
    val symbol: String,
    val markerType: OverlayMarkerType,
    val price: Double,
    val timestamp: Long,
    val quantityText: String
)

/**
 * Geleceğe Hazır AI Chart Analysis Stub Modeli
 */
data class AiChartPatternStub(
    val symbol: String,
    val detectedPattern: String = "Fincan Kulp Formasyonu (Cup & Handle)",
    val targetPrice: Double = 310.0,
    val confidencePct: Double = 91.5
)
