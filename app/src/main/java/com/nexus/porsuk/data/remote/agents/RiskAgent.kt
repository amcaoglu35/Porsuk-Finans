package com.nexus.porsuk.data.remote.agents

import java.util.Locale
import kotlin.math.sqrt

class RiskAgent : PorsukAgent {
    override val agentName: String = "Risk Agent"

    override suspend fun runAnalysis(request: AgentRequest): String {
        val prices = request.historicalPrices
        if (prices.size < 3) {
            return "Risk Analizi: Fiyat verisi kısıtlı. Risk seviyesi Dengeli/Nötr."
        }
        val returns = prices.zipWithNext { a, b -> if (a > 0) (b - a) / a else 0.0 }
        val variance = returns.map { it * it }.average()
        val annualizedVol = sqrt(variance) * sqrt(252.0) * 100.0

        val maxPrice = prices.maxOrNull() ?: 1.0
        val minPrice = prices.minOrNull() ?: 1.0
        val maxDrawdown = if (maxPrice > 0) ((maxPrice - minPrice) / maxPrice) * 100.0 else 0.0

        val levelText = when {
            annualizedVol >= 35.0 -> "YÜKSEK RİSK"
            annualizedVol >= 20.0 -> "ORTA RİSK"
            else -> "DÜŞÜK RİSK"
        }

        return "Risk Seviyesi: $levelText | Yıllık Volatilite: %${String.format(Locale.US, "%.1f", annualizedVol)} | Max Drawdown: %${String.format(Locale.US, "%.1f", maxDrawdown)}."
    }
}
