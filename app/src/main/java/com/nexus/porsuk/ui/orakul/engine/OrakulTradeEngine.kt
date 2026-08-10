package com.nexus.porsuk.ui.orakul.engine

import com.nexus.porsuk.data.local.entity.BasketItem
import com.nexus.porsuk.data.local.entity.Company
import com.nexus.porsuk.data.local.entity.PriceSnapshot
import com.nexus.porsuk.ui.orakul.OrakulDecision
import com.nexus.porsuk.ui.orakul.RebalanceTrade

object OrakulTradeEngine {

    fun calculateRebalanceTrades(
        basketId: Int,
        decisions: List<OrakulDecision>,
        companies: List<Company>,
        allItems: List<BasketItem>,
        exchangeRates: Map<String, Double>,
        pricesMap: Map<String, PriceSnapshot>,
        investmentAmount: String
    ): List<RebalanceTrade> {
        if (decisions.isEmpty()) return emptyList()

        val companyMap = companies.associateBy { it.symbol }
        val itemMap = allItems.associateBy { it.symbol.uppercase() }

        val usdRate = exchangeRates["USD"] ?: 34.5
        val eurRate = exchangeRates["EUR"] ?: 37.2
        
        var totalValue = 0.0
        allItems.forEach { item ->
            val company = companyMap[item.symbol]
            val currentPrice = pricesMap[item.symbol]?.price ?: company?.currentPrice ?: item.buyPrice
            val rate = when (company?.market?.uppercase()) {
                "NASDAQ", "NYSE" -> usdRate
                "FRA", "EURONEXT" -> eurRate
                else -> 1.0
            }
            totalValue += item.quantity * currentPrice * rate
        }

        if (totalValue <= 0.0) {
            val budgetStr = investmentAmount.replace(Regex("[^0-9]"), "")
            totalValue = budgetStr.toDoubleOrNull() ?: 10000.0
        }

        val trades = mutableListOf<RebalanceTrade>()

        decisions.forEach { decision ->
            val company = companyMap[decision.symbol]
            val currentPrice = pricesMap[decision.symbol]?.price ?: company?.currentPrice ?: 100.0
            val rate = when (company?.market?.uppercase()) {
                "NASDAQ", "NYSE" -> usdRate
                "FRA", "EURONEXT" -> eurRate
                else -> 1.0
            }

            val existingItem = itemMap[decision.symbol.uppercase()]
            val currentQty = existingItem?.quantity ?: 0.0

            val targetWeight = if (decision.decision == "SAT") 0.0 else decision.weight
            
            val targetValueInCurrency = (totalValue * (targetWeight / 100.0)) / rate
            val targetQtyRaw = if (currentPrice > 0) targetValueInCurrency / currentPrice else 0.0
            val targetQty = kotlin.math.round(targetQtyRaw).coerceAtLeast(0.0)

            val tradeQty = targetQty - currentQty
            val valueDiff = tradeQty * currentPrice * rate

            if (tradeQty != 0.0 || targetWeight > 0.0) {
                trades.add(RebalanceTrade(
                    symbol = decision.symbol,
                    currentQty = currentQty,
                    currentPrice = currentPrice,
                    targetWeight = targetWeight,
                    targetQty = targetQty,
                    tradeQty = tradeQty,
                    valueDiff = valueDiff,
                    decision = decision.decision
                ))
            }
        }

        allItems.forEach { item ->
            val sym = item.symbol.uppercase()
            if (decisions.none { it.symbol.uppercase() == sym }) {
                val company = companyMap[item.symbol]
                val currentPrice = pricesMap[item.symbol]?.price ?: company?.currentPrice ?: item.buyPrice
                val rate = when (company?.market?.uppercase()) {
                    "NASDAQ", "NYSE" -> usdRate
                    "FRA", "EURONEXT" -> eurRate
                    else -> 1.0
                }
                val tradeQty = -item.quantity
                val valueDiff = tradeQty * currentPrice * rate

                trades.add(RebalanceTrade(
                    symbol = item.symbol,
                    currentQty = item.quantity,
                    currentPrice = currentPrice,
                    targetWeight = 0.0,
                    targetQty = 0.0,
                    tradeQty = tradeQty,
                    valueDiff = valueDiff,
                    decision = "SAT"
                ))
            }
        }

        return trades
    }
}
