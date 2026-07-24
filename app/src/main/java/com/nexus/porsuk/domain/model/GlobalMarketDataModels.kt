package com.nexus.porsuk.domain.model

/**
 * Piyasa Ticker Varlık Modeli (MarketTickerItem)
 */
data class MarketTickerItem(
    val symbol: String,
    val name: String,
    val region: MarketRegion,
    val exchangeName: String,
    val lastPrice: Double,
    val dailyChangePct: Double,
    val volumeText: String,
    val openPrice: Double,
    val closePrice: Double,
    val dayHigh: Double,
    val dayLow: Double,
    val status: MarketState
)

/**
 * Borsa Durum Bilgisi (ExchangeStatusInfo)
 */
data class ExchangeStatusInfo(
    val exchangeName: String,
    val countryCode: String,
    val status: MarketState,
    val localTimeText: String,
    val openCloseHoursText: String
)

/**
 * Sektör Performans Modeli (SectorPerformanceItem)
 */
data class SectorPerformanceItem(
    val sector: SectorType,
    val dailyChangePct: Double,
    val topPerformingSymbol: String,
    val totalMarketCapUsdText: String
)

/**
 * Dünya Isı Haritası ve Sermaye Akış Modeli (WorldHeatMapData)
 */
data class WorldHeatMapData(
    val countryPerformances: Map<String, Double> = mapOf("Türkiye" to 2.45, "ABD" to 1.15, "Almanya" to 0.85, "Japonya" to -0.40),
    val capitalFlowStatusText: String = "ABD Teknoloji Piyasalarına Güçlü Sermaye Girişi"
)

/**
 * Geleceğe Hazır AI Macro & Global Analysis Stub Modeli
 */
data class AiGlobalAnalysisStub(
    val region: MarketRegion,
    val macroSentimentText: String = "Orakul AI: Küresel faiz indirimi beklentileri ile Asya ve Gelişmekte Olan Piyasalar pozitif eğilimdedir.",
    val capitalFlowScore: Int = 88
)
