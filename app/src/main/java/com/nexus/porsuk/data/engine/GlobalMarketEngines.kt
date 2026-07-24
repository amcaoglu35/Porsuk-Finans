package com.nexus.porsuk.data.engine

import com.nexus.porsuk.domain.model.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 1. Küresel Sıralama Motoru (GlobalRankingEngine)
 */
@Singleton
class GlobalRankingEngine @Inject constructor() {

    fun getTopGainers(tickers: List<MarketTickerItem>): List<MarketTickerItem> {
        return tickers.sortedByDescending { it.dailyChangePct }
    }

    fun getTopLosers(tickers: List<MarketTickerItem>): List<MarketTickerItem> {
        return tickers.sortedBy { it.dailyChangePct }
    }
}

/**
 * 2. Dünya Isı Haritası & Sektör Motoru (WorldHeatMapEngine)
 */
@Singleton
class WorldHeatMapEngine @Inject constructor() {

    fun getSectorPerformances(): List<SectorPerformanceItem> {
        return listOf(
            SectorPerformanceItem(SectorType.TECHNOLOGY, 3.45, "NVDA", "$18.4T"),
            SectorPerformanceItem(SectorType.TRANSPORT, 2.80, "THYAO.IS", "$2.8T"),
            SectorPerformanceItem(SectorType.BANKING, 1.95, "AKBNK.IS", "$8.2T"),
            SectorPerformanceItem(SectorType.ENERGY, -0.45, "TUPRS.IS", "$4.1T")
        )
    }

    fun getWorldHeatMap(): WorldHeatMapData {
        return WorldHeatMapData(
            countryPerformances = mapOf("Türkiye" to 2.45, "ABD" to 1.15, "Almanya" to 0.85, "Japonya" to -0.40),
            capitalFlowStatusText = "Gelişmekte Olan Piyasalara ve ABD Teknoloji Hisselerine Güçlü Sermaye Girişi"
        )
    }
}
