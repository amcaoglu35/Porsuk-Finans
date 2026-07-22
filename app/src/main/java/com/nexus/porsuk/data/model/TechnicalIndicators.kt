package com.nexus.porsuk.data.model

import kotlin.math.sqrt

data class TechnicalAnalysis(
    val rsi: Double?,
    val macd: MacdData?,
    val bollinger: BollingerData?
)

data class MacdData(
    val macd: Double,
    val signal: Double,
    val histogram: Double
)

data class BollingerData(
    val middle: Double,
    val upper: Double,
    val lower: Double
)

object IndicatorCalculator {

    fun calculateRsi(prices: List<Double>, period: Int = 14): Double? {
        if (prices.size <= period) return null
        
        val changes = prices.zipWithNext { a, b -> b - a }
        var avgGain = changes.take(period).filter { it > 0 }.sum() / period
        var avgLoss = changes.take(period).filter { it < 0 }.sum().let { kotlin.math.abs(it) } / period
        
        for (i in period until changes.size) {
            val change = changes[i]
            val gain = if (change > 0) change else 0.0
            val loss = if (change < 0) kotlin.math.abs(change) else 0.0
            
            avgGain = (avgGain * (period - 1) + gain) / period
            avgLoss = (avgLoss * (period - 1) + loss) / period
        }
        
        if (avgLoss == 0.0) return 100.0
        val rs = avgGain / avgLoss
        return 100.0 - (100.0 / (1.0 + rs))
    }

    fun calculateMacd(prices: List<Double>): MacdData? {
        if (prices.size < 35) return null // Need enough data for 26-period EMA
        
        val ema12 = calculateEma(prices, 12)
        val ema26 = calculateEma(prices, 26)
        
        val macdValues = mutableListOf<Double>()
        // MACD is EMA12 - EMA26
        // We need a list of MACD values to calculate the Signal line (EMA9 of MACD)
        for (i in 0 until prices.size) {
            // Simplified: we only care about the latest one for the UI, but we need history for signal
            // This is a complex calculation to do stateless, usually done on a window.
            // For brevity, let's calculate the full series.
        }
        
        // Accurate series calculation
        val fullMacdSeries = mutableListOf<Double>()
        val e12Series = calculateEmaSeries(prices, 12)
        val e26Series = calculateEmaSeries(prices, 26)
        
        for (i in 0 until prices.size) {
            fullMacdSeries.add(e12Series[i] - e26Series[i])
        }
        
        val signalSeries = calculateEmaSeries(fullMacdSeries, 9)
        
        val latestMacd = fullMacdSeries.last()
        val latestSignal = signalSeries.last()
        
        return MacdData(
            macd = latestMacd,
            signal = latestSignal,
            histogram = latestMacd - latestSignal
        )
    }

    fun calculateBollinger(prices: List<Double>, period: Int = 20, k: Double = 2.0): BollingerData? {
        if (prices.size < period) return null
        
        val window = prices.takeLast(period)
        val sma = window.average()
        val variance = window.map { (it - sma) * (it - sma) }.average()
        val stdDev = sqrt(variance)
        
        return BollingerData(
            middle = sma,
            upper = sma + (k * stdDev),
            lower = sma - (k * stdDev)
        )
    }

    private fun calculateEma(values: List<Double>, period: Int): Double {
        val k = 2.0 / (period + 1)
        var ema = values.first()
        for (i in 1 until values.size) {
            ema = values[i] * k + ema * (1 - k)
        }
        return ema
    }
    
    private fun calculateEmaSeries(values: List<Double>, period: Int): List<Double> {
        val k = 2.0 / (period + 1)
        val series = mutableListOf<Double>()
        var ema = values.first()
        series.add(ema)
        for (i in 1 until values.size) {
            ema = values[i] * k + ema * (1 - k)
            series.add(ema)
        }
        return series
    }
}
