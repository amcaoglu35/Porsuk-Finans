package com.nexus.porsuk.data.repository

import com.nexus.porsuk.data.engine.ChartRendererEngine
import com.nexus.porsuk.data.engine.PortfolioOverlayEngine
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChartRepositoryImpl @Inject constructor(
    private val rendererEngine: ChartRendererEngine,
    private val overlayEngine: PortfolioOverlayEngine
) : ChartRepository {

    override fun getCandles(symbol: String, timeFrame: ChartTimeFrame, chartType: ChartType): Flow<List<CandleStickItem>> = flow {
        emit(rendererEngine.renderCandles(symbol, timeFrame, chartType))
    }

    override fun getPortfolioOverlayMarkers(symbol: String): Flow<List<PortfolioTransactionMarker>> = flow {
        emit(overlayEngine.getPortfolioMarkersForSymbol(symbol))
    }
}

@Singleton
class DrawingRepositoryImpl @Inject constructor() : DrawingRepository {

    private val drawingsMap = MutableStateFlow<List<ChartDrawingObject>>(emptyList())

    override fun getSavedDrawings(symbol: String): Flow<List<ChartDrawingObject>> = drawingsMap

    override suspend fun saveDrawing(drawing: ChartDrawingObject) {
        val current = drawingsMap.value.toMutableList()
        current.add(drawing)
        drawingsMap.value = current
    }

    override suspend fun deleteDrawing(drawingId: String) {
        val current = drawingsMap.value.toMutableList()
        current.removeAll { it.id == drawingId }
        drawingsMap.value = current
    }
}

@Singleton
class ChartIndicatorRepositoryImpl @Inject constructor() : ChartIndicatorRepository {

    override fun getActiveOverlays(symbol: String): Flow<List<String>> = flow {
        emit(listOf("EMA 20", "EMA 50", "Bollinger Bands"))
    }

    override fun getActiveSubIndicators(symbol: String): Flow<List<String>> = flow {
        emit(listOf("RSI (14)", "MACD (12, 26, 9)"))
    }
}
