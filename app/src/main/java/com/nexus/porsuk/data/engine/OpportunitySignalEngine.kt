package com.nexus.porsuk.data.engine

import com.nexus.porsuk.data.repository.FinanceRepository
import com.nexus.porsuk.data.remote.ScrapeResult
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

data class OpportunitySignal(
    val recommendation: String,
    val confidence: Int,
    val reasons: List<String>,
    val isPositive: Boolean
)

@Singleton
class OpportunitySignalEngine @Inject constructor(
    private val repository: FinanceRepository
) {
    // Simple memory cache for signals (10 minutes)
    private val signalCache = mutableMapOf<String, Pair<Long, OpportunitySignal>>()
    private val cacheDurationMs = 10 * 60 * 1000L

    suspend fun getSignalForStock(symbol: String, market: String): OpportunitySignal {
        val now = System.currentTimeMillis()
        signalCache[symbol]?.let { (timestamp, signal) ->
            if (now - timestamp < cacheDurationMs) return signal
        }

        // 1. Fetch Technical Data (Past 1 month)
        val historyResult = repository.fetchHistoricalPrices(symbol, market, "1mo", "1d")
        val historicalPrices = if (historyResult is ScrapeResult.Success) historyResult.data else emptyList()
        
        // 2. Fetch Fundamental Data
        val info = repository.getCachedInfo(symbol).firstOrNull()
        
        val reasons = mutableListOf<String>()
        var score = 50 // Base score 0-100

        // Merge historical with current price for fresh technicals
        val currentPrice = repository.prices.value[symbol]?.price ?: 0.0
        val finalPrices = if (currentPrice > 0.0) historicalPrices + currentPrice else historicalPrices

        // Technical Analysis (Simple Heuristics)
        if (finalPrices.size >= 14) {
            val series = Ta4jTechnicalCalculator.createBarSeries(symbol, finalPrices)
            val indicators = Ta4jTechnicalCalculator.calculateIndicators(series)
            
            val rsi = indicators.rsi ?: 50.0
            val macdHist = indicators.macdHist ?: 0.0

            if (rsi < 35) {
                score += 25
                reasons.add("Aşırı Satım Bölgesi (RSI: ${"%.1f".format(rsi)})")
            } else if (rsi < 45) {
                score += 10
                reasons.add("Göreceli Ucuz Bölge (RSI: ${"%.1f".format(rsi)})")
            } else if (rsi > 70) {
                score -= 20
                reasons.add("Aşırı Alım Bölgesi (RSI: ${"%.1f".format(rsi)})")
            }

            if (macdHist > 0) {
                score += 15
                reasons.add("Trend Pozitif (MACD)")
            } else {
                score -= 5
            }
        }

        // Fundamental Analysis
        info?.peRatio?.let { pe ->
            if (pe > 0 && pe < 12) {
                score += 15
                reasons.add("Cazip F/K Oranı (${"%.1f".format(pe)})")
            } else if (pe > 30) {
                score -= 15
                reasons.add("Yüksek Değerleme Riski (${"%.1f".format(pe)})")
            }
        }

        // Daily Momentum
        val snap = repository.prices.value[symbol]
        if (snap != null) {
            if (snap.changePercent > 3.0) {
                score += 10
                reasons.add("Güçlü Yükseliş İvmesi (%${"%.2f".format(snap.changePercent)})")
            } else if (snap.changePercent < -3.0) {
                score -= 15
                reasons.add("Sert Satış Baskısı (%${"%.2f".format(snap.changePercent)})")
            }
        }

        val recommendation = when {
            score >= 85 -> "Güçlü Alım"
            score >= 68 -> "Alım Sinyali"
            score >= 45 -> "Nötr"
            else -> "Dikkat"
        }

        val finalSignal = OpportunitySignal(
            recommendation = recommendation,
            confidence = score.coerceIn(40, 99),
            reasons = if (reasons.isEmpty()) listOf("Yatay piyasa seyri") else reasons,
            isPositive = score >= 50
        )
        
        signalCache[symbol] = now to finalSignal
        return finalSignal
    }
}
