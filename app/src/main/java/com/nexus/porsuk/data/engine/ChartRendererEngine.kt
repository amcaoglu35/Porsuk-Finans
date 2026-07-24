package com.nexus.porsuk.data.engine

import com.nexus.porsuk.domain.model.*
import javax.inject.Inject

/**
 * Porsuk Professional Chart Center — Soyut Grafik Çizici Arayüzü (ChartRendererEngine)
 *
 * Hiçbir grafik kütüphanesine sıkı sıkıya bağımlı değildir.
 * İleride TradingView Lightweight Charts, MPAndroidChart, SciChart veya özel Canvas Motoru
 * bu arayüz uygulanarak tek satır değişiklikle entegre edilebilir.
 */
interface ChartRendererEngine {
    fun renderCandles(symbol: String, timeFrame: ChartTimeFrame, chartType: ChartType): List<CandleStickItem>
    fun calculateOverlays(candles: List<CandleStickItem>, overlayName: String): List<Double>
}

/**
 * Jetpack Compose Canvas Varsayılan Grafik Çizicisi (DefaultComposeChartRenderer)
 */
class DefaultComposeChartRenderer @Inject constructor() : ChartRendererEngine {

    override fun renderCandles(symbol: String, timeFrame: ChartTimeFrame, chartType: ChartType): List<CandleStickItem> {
        val now = System.currentTimeMillis()
        val dayMs = 86400000L

        return listOf(
            CandleStickItem(now - (5 * dayMs), 270.0, 275.0, 268.0, 274.5, 1200000.0),
            CandleStickItem(now - (4 * dayMs), 274.5, 279.0, 272.0, 278.0, 1450000.0),
            CandleStickItem(now - (3 * dayMs), 278.0, 282.0, 276.5, 280.5, 1100000.0),
            CandleStickItem(now - (2 * dayMs), 280.5, 285.0, 279.0, 282.0, 1600000.0),
            CandleStickItem(now - (1 * dayMs), 282.0, 288.0, 281.5, 284.5, 1850000.0)
        )
    }

    override fun calculateOverlays(candles: List<CandleStickItem>, overlayName: String): List<Double> {
        return candles.map { it.close }
    }
}
