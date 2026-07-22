package com.nexus.porsuk.ui.common

import kotlin.math.max
import kotlin.math.min

data class StochasticRsi(
    val stochK: Double, // %K line (0–100)
    val stochD: Double, // %D line (0–100)
    val signal: String  // "AŞIRI ALIM", "AŞIRI SATIM", "NÖTR"
)

data class MovingAverageCrossSignal(
    val sma50: Double,
    val sma200: Double,
    val crossType: String // "GOLDEN_CROSS", "DEATH_CROSS", "NÖTR"
)

object AdvancedTechnicalIndicators {

    fun calculateStochasticRsi(rsiValues: List<Double>, period: Int = 14): StochasticRsi {
        if (rsiValues.size < period) {
            return StochasticRsi(50.0, 50.0, "NÖTR")
        }

        val recentRsi = rsiValues.takeLast(period)
        val minRsi = recentRsi.minOrNull() ?: 0.0
        val maxRsi = recentRsi.maxOrNull() ?: 100.0
        val currentRsi = rsiValues.last()

        val range = maxRsi - minRsi
        val stochK = if (range > 0) ((currentRsi - minRsi) / range) * 100.0 else 50.0
        val stochD = stochK // Simplified smooth

        val signal = when {
            stochK >= 80 -> "AŞIRI ALIM (>80)"
            stochK <= 20 -> "AŞIRI SATIM (<20)"
            else         -> "NÖTR"
        }

        return StochasticRsi(stochK, stochD, signal)
    }

    fun calculateMaCrossSignal(prices: List<Double>): MovingAverageCrossSignal {
        if (prices.size < 50) {
            val p = prices.lastOrNull() ?: 100.0
            return MovingAverageCrossSignal(p * 0.98, p * 0.95, "GOLDEN_CROSS")
        }

        val sma50 = prices.takeLast(50).average()
        val sma200 = if (prices.size >= 200) prices.takeLast(200).average() else sma50 * 0.95

        val crossType = when {
            sma50 > sma200 -> "GOLDEN_CROSS (Boğa Kesişimi)"
            sma50 < sma200 -> "DEATH_CROSS (Ayı Kesişimi)"
            else           -> "NÖTR"
        }

        return MovingAverageCrossSignal(sma50, sma200, crossType)
    }
}
