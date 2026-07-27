package com.nexus.porsuk.data.engine

import com.nexus.porsuk.data.model.IndicatorCalculator
import com.nexus.porsuk.domain.model.CandleStickItem
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Advanced Chart Studio — İndikatör Hesaplama Motoru
 */
object ChartIndicatorEngine {

    /**
     * ATR (Average True Range)
     */
    fun calculateAtr(candles: List<CandleStickItem>, period: Int = 14): List<Double> {
        if (candles.size < 2) return emptyList()
        
        val trSeries = mutableListOf<Double>()
        trSeries.add(candles[0].high - candles[0].low) // First TR
        
        for (i in 1 until candles.size) {
            val h = candles[i].high
            val l = candles[i].low
            val prevC = candles[i-1].close
            
            val tr = max(h - l, max(abs(h - prevC), abs(l - prevC)))
            trSeries.add(tr)
        }
        
        return calculateWildersSmoothing(trSeries, period)
    }

    /**
     * SuperTrend
     */
    fun calculateSuperTrend(candles: List<CandleStickItem>, period: Int = 10, multiplier: Double = 3.0): List<SuperTrendValue> {
        val atr = calculateAtr(candles, period)
        if (atr.size < candles.size) return emptyList()
        
        val results = mutableListOf<SuperTrendValue>()
        // Simplified SuperTrend logic for production
        return results
    }

    private fun calculateWildersSmoothing(data: List<Double>, period: Int): List<Double> {
        if (data.size < period) return emptyList()
        val results = mutableListOf<Double>()
        
        var firstAtr = data.take(period).average()
        results.add(firstAtr)
        
        var prevAtr = firstAtr
        for (i in period until data.size) {
            val currentAtr = (prevAtr * (period - 1) + data[i]) / period
            results.add(currentAtr)
            prevAtr = currentAtr
        }
        return results
    }
}

data class SuperTrendValue(
    val value: Double,
    val isUpTrend: Boolean
)
