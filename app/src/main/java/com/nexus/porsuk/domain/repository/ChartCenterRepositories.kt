package com.nexus.porsuk.domain.repository

import com.nexus.porsuk.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * 1. Grafik Deposu Sözleşmesi (ChartRepository)
 */
interface ChartRepository {
    fun getCandles(symbol: String, timeFrame: ChartTimeFrame, chartType: ChartType): Flow<List<CandleStickItem>>
    fun getPortfolioOverlayMarkers(symbol: String): Flow<List<PortfolioTransactionMarker>>
}

/**
 * 2. Çizim Nesneleri Deposu Sözleşmesi (DrawingRepository)
 */
interface DrawingRepository {
    fun getSavedDrawings(symbol: String): Flow<List<ChartDrawingObject>>
    suspend fun saveDrawing(drawing: ChartDrawingObject)
    suspend fun deleteDrawing(drawingId: String)
}

/**
 * 3. Grafik İndikatörleri Deposu Sözleşmesi (ChartIndicatorRepository)
 */
interface ChartIndicatorRepository {
    fun getActiveOverlays(symbol: String): Flow<List<String>>
    fun getActiveSubIndicators(symbol: String): Flow<List<String>>
}
