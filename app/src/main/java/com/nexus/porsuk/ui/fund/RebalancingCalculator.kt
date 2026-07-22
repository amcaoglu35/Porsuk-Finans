package com.nexus.porsuk.ui.fund

import java.util.Locale

enum class RebalanceTradeType {
    BUY, SELL, HOLD
}

data class RebalanceInput(
    val symbol: String,
    val currentQuantity: Double,
    val currentPrice: Double,
    val targetPercent: Double
)

data class RebalanceResultItem(
    val symbol: String,
    val currentQuantity: Double,
    val currentPrice: Double,
    val currentVal: Double,
    val currentPercent: Double,
    val targetPercent: Double,
    val targetVal: Double,
    val deltaVal: Double,
    val deltaQuantity: Double,
    val tradeType: RebalanceTradeType
) {
    fun getActionSummary(): String {
        return when (tradeType) {
            RebalanceTradeType.BUY -> "+${String.format(Locale.US, "%.1f", deltaQuantity)} Adet Al (${String.format(Locale.US, "%.1f", deltaVal)} TL)"
            RebalanceTradeType.SELL -> "${String.format(Locale.US, "%.1f", Math.abs(deltaQuantity))} Adet Sat (${String.format(Locale.US, "%.1f", Math.abs(deltaVal))} TL)"
            RebalanceTradeType.HOLD -> "Dengede (Değişim yok)"
        }
    }
}

object RebalancingCalculator {

    fun calculate(inputs: List<RebalanceInput>): List<RebalanceResultItem> {
        if (inputs.isEmpty()) return emptyList()

        val totalVal = inputs.sumOf { it.currentQuantity * it.currentPrice }.coerceAtLeast(0.01)

        return inputs.map { item ->
            val currentVal = item.currentQuantity * item.currentPrice
            val currentPct = (currentVal / totalVal) * 100.0
            val targetVal = totalVal * (item.targetPercent / 100.0)
            val deltaVal = targetVal - currentVal
            val deltaQty = if (item.currentPrice > 0.0) deltaVal / item.currentPrice else 0.0

            val tradeType = when {
                deltaVal > (totalVal * 0.01) -> RebalanceTradeType.BUY
                deltaVal < -(totalVal * 0.01) -> RebalanceTradeType.SELL
                else -> RebalanceTradeType.HOLD
            }

            RebalanceResultItem(
                symbol = item.symbol,
                currentQuantity = item.currentQuantity,
                currentPrice = item.currentPrice,
                currentVal = currentVal,
                currentPercent = currentPct,
                targetPercent = item.targetPercent,
                targetVal = targetVal,
                deltaVal = deltaVal,
                deltaQuantity = deltaQty,
                tradeType = tradeType
            )
        }
    }

    fun generateEqualWeightTargets(symbols: List<String>): Map<String, Double> {
        if (symbols.isEmpty()) return emptyMap()
        val equalPct = 100.0 / symbols.size
        return symbols.associateWith { equalPct }
    }
}
