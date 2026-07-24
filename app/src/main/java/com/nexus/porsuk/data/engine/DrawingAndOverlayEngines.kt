package com.nexus.porsuk.data.engine

import com.nexus.porsuk.domain.model.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Grafik Çizim Araçları Motoru (DrawingToolsEngine)
 */
@Singleton
class DrawingToolsEngine @Inject constructor() {

    fun createDrawing(
        toolType: DrawingToolType,
        startTimestamp: Long,
        startPrice: Double,
        endTimestamp: Long? = null,
        endPrice: Double? = null,
        note: String? = null
    ): ChartDrawingObject {
        return ChartDrawingObject(
            id = "draw_${System.currentTimeMillis()}",
            toolType = toolType,
            startTimestamp = startTimestamp,
            startPrice = startPrice,
            endTimestamp = endTimestamp,
            endPrice = endPrice,
            textNote = note
        )
    }
}

/**
 * Portföy Alış/Satış/Temettü Grafik İşaretçileri Motoru (PortfolioOverlayEngine)
 */
@Singleton
class PortfolioOverlayEngine @Inject constructor() {

    fun getPortfolioMarkersForSymbol(symbol: String): List<PortfolioTransactionMarker> {
        val now = System.currentTimeMillis()
        val dayMs = 86400000L

        return listOf(
            PortfolioTransactionMarker("m1", symbol, OverlayMarkerType.BUY, 270.0, now - (5 * dayMs), "100 Lot Alış"),
            PortfolioTransactionMarker("m2", symbol, OverlayMarkerType.DIVIDEND, 278.0, now - (3 * dayMs), "Temettü 4.20 TL"),
            PortfolioTransactionMarker("m3", symbol, OverlayMarkerType.SELL, 284.5, now - (1 * dayMs), "20 Lot Satış")
        )
    }
}
