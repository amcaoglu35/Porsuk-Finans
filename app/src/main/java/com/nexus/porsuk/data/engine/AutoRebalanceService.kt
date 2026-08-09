package com.nexus.porsuk.data.engine

import com.nexus.porsuk.data.repository.FinanceRepository
import com.nexus.porsuk.data.calculator.PortfolioCalculatorEngine
import com.nexus.porsuk.data.local.entity.BasketItem
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AutoRebalanceService @Inject constructor(
    private val repository: FinanceRepository,
    private val calculator: PortfolioCalculatorEngine
) {
    /**
     * Executes an automated rebalance of the portfolio based on a target allocation.
     * 
     * @param targetAllocation Map of Symbol to Target Percentage (e.g., "THYAO" -> 20.0)
     */
    suspend fun executeAutoRebalance(targetAllocation: Map<String, Double>) {
        val allBaskets = repository.allBaskets.first()
        if (allBaskets.isEmpty()) return

        // 1. Get current consolidated holdings
        val assets = repository.getConsolidatedAssetsFlow().first()
        val totalValue = assets.sumOf { it.totalValue }
        if (totalValue <= 0.0) return

        // 2. Identify required adjustments
        val adjustments = mutableListOf<RebalanceAction>()
        
        targetAllocation.forEach { (symbol, targetPct) ->
            val targetValue = totalValue * (targetPct / 100.0)
            val currentAsset = assets.find { it.symbol == symbol }
            val currentValue = currentAsset?.totalValue ?: 0.0
            
            val diffValue = targetValue - currentValue
            if (Math.abs(diffValue) > (totalValue * 0.01)) { // Threshold 1%
                val price = currentAsset?.currentPrice ?: repository.prices.value[symbol]?.price ?: 100.0
                val qty = diffValue / price
                adjustments.add(RebalanceAction(symbol, qty, price, if (qty > 0) "BUY" else "SELL"))
            }
        }

        // 3. (Mock) Execute transactions
        // In a real scenario, this would call repository.executeTransaction()
        // For now, we simulate the logic and log it.
        adjustments.forEach { action ->
            println("AutoRebalance: ${action.type} ${action.quantity} of ${action.symbol} at ${action.price}")
            // repository.executeTransaction(targetBasketId, action.symbol, Math.abs(action.quantity), action.price, action.type == "BUY")
        }
    }

    private data class RebalanceAction(
        val symbol: String,
        val quantity: Double,
        val price: Double,
        val type: String
    )
}
