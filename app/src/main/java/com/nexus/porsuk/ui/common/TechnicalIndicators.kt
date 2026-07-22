package com.nexus.porsuk.ui.common

/**
 * Saf Kotlin teknik analiz indikatörleri.
 * Harici kütüphane gerektirmez — fiyat listesinden hesaplanır.
 */
object TechnicalIndicators {

    // ─── RSI (Relative Strength Index, 14 periyot) ───────────────────────────
    fun rsi(prices: List<Float>, period: Int = 14): Double {
        if (prices.size < period + 1) return 50.0
        val changes = prices.zipWithNext { a, b -> (b - a).toDouble() }
        var avgGain = changes.take(period).filter { it > 0 }.average().let { if (it.isNaN()) 0.0 else it }
        var avgLoss = changes.take(period).filter { it < 0 }.map { -it }.average().let { if (it.isNaN()) 0.0 else it }
        for (i in period until changes.size) {
            val gain = if (changes[i] > 0) changes[i] else 0.0
            val loss = if (changes[i] < 0) -changes[i] else 0.0
            avgGain = (avgGain * (period - 1) + gain) / period
            avgLoss = (avgLoss * (period - 1) + loss) / period
        }
        if (avgLoss == 0.0) return 100.0
        val rs = avgGain / avgLoss
        return 100.0 - (100.0 / (1 + rs))
    }

    // ─── EMA (Exponential Moving Average) ───────────────────────────────────
    private fun ema(prices: List<Float>, period: Int): Double {
        if (prices.size < period) return prices.lastOrNull()?.toDouble() ?: 0.0
        val k = 2.0 / (period + 1)
        var ema = prices.take(period).average()
        for (i in period until prices.size) {
            ema = prices[i] * k + ema * (1 - k)
        }
        return ema
    }

    // ─── MACD (12-26-9) ──────────────────────────────────────────────────────
    data class MacdResult(
        val macdLine: Double,    // EMA12 - EMA26
        val signalLine: Double,  // EMA9 of MACD line
        val histogram: Double    // MACD - Signal
    )

    fun macd(prices: List<Float>): MacdResult {
        if (prices.size < 26) return MacdResult(0.0, 0.0, 0.0)
        // Compute MACD line series
        val macdSeries = mutableListOf<Float>()
        for (endIdx in 26..prices.size) {
            val slice = prices.subList(0, endIdx)
            val m = ema(slice, 12) - ema(slice, 26)
            macdSeries.add(m.toFloat())
        }
        val latestMacd = macdSeries.lastOrNull()?.toDouble() ?: 0.0
        val signalLine = if (macdSeries.size >= 9) ema(macdSeries, 9) else latestMacd
        return MacdResult(
            macdLine = latestMacd,
            signalLine = signalLine,
            histogram = latestMacd - signalLine
        )
    }

    // ─── Bollinger Bantları (20 periyot, 2 std) ──────────────────────────────
    data class BollingerResult(
        val upper: Double,
        val middle: Double,
        val lower: Double,
        val percentB: Double  // (fiyat - lower) / (upper - lower), 0-1 arası
    )

    fun bollinger(prices: List<Float>, period: Int = 20, multiplier: Double = 2.0): BollingerResult {
        if (prices.size < period) {
            val last = prices.lastOrNull()?.toDouble() ?: 0.0
            return BollingerResult(last, last, last, 0.5)
        }
        val slice = prices.takeLast(period)
        val mean = slice.average()
        val std = kotlin.math.sqrt(slice.map { (it - mean) * (it - mean) }.average())
        val upper = mean + multiplier * std
        val lower = mean - multiplier * std
        val last = prices.last().toDouble()
        val pctB = if (upper > lower) (last - lower) / (upper - lower) else 0.5
        return BollingerResult(upper = upper, middle = mean, lower = lower, percentB = pctB)
    }

    // ─── Sinyal Yorumlama ─────────────────────────────────────────────────────
    enum class Signal { BUY, SELL, NEUTRAL }

    data class TechnicalSignals(
        val rsiValue: Double,
        val rsiSignal: Signal,
        val macdSignal: Signal,
        val bollingerSignal: Signal,
        val bollingerPercentB: Double,
        val overallSignal: Signal
    )

    fun compute(prices: List<Float>): TechnicalSignals {
        val rsiVal = rsi(prices)
        val rsiSig = when {
            rsiVal < 30 -> Signal.BUY
            rsiVal > 70 -> Signal.SELL
            else        -> Signal.NEUTRAL
        }
        val macdResult = macd(prices)
        val macdSig = when {
            macdResult.histogram > 0 && macdResult.macdLine > 0 -> Signal.BUY
            macdResult.histogram < 0 && macdResult.macdLine < 0 -> Signal.SELL
            else -> Signal.NEUTRAL
        }
        val boll = bollinger(prices)
        val bollSig = when {
            boll.percentB < 0.05 -> Signal.BUY   // Fiyat alt banda yakın
            boll.percentB > 0.95 -> Signal.SELL  // Fiyat üst banda yakın
            else                 -> Signal.NEUTRAL
        }
        // Çoğunluk oylaması
        val buyCount  = listOf(rsiSig, macdSig, bollSig).count { it == Signal.BUY }
        val sellCount = listOf(rsiSig, macdSig, bollSig).count { it == Signal.SELL }
        val overall = when {
            buyCount >= 2  -> Signal.BUY
            sellCount >= 2 -> Signal.SELL
            else           -> Signal.NEUTRAL
        }
        return TechnicalSignals(
            rsiValue = rsiVal,
            rsiSignal = rsiSig,
            macdSignal = macdSig,
            bollingerSignal = bollSig,
            bollingerPercentB = boll.percentB,
            overallSignal = overall
        )
    }
}
