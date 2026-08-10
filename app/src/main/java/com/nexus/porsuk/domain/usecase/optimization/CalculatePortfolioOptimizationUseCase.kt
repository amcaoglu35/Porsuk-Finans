package com.nexus.porsuk.domain.usecase.optimization

import com.nexus.porsuk.data.repository.FinanceRepository
import com.nexus.porsuk.domain.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import kotlin.math.sqrt

data class OptimizationResult(
    val sharpeRatio: Double,
    val totalVolatility: Double,
    val assetMetrics: List<AssetRiskMetric>,
    val suggestions: List<String>
)

data class AssetRiskMetric(
    val symbol: String,
    val volatility: Double,
    val weight: Double
)

class CalculatePortfolioOptimizationUseCase @Inject constructor(
    private val repository: FinanceRepository
) {
    operator fun invoke(): Flow<OptimizationResult> = flow {
        val assets = repository.getConsolidatedAssetsFlow().first()
        if (assets.isEmpty()) {
            emit(OptimizationResult(0.0, 0.0, emptyList(), listOf("Portföyünüz boş, analiz yapılamadı.")))
            return@flow
        }

        val totalValue = assets.sumOf { it.totalValue }
        val assetMetrics = mutableListOf<AssetRiskMetric>()
        
        var portfolioReturn = 0.0
        var portfolioVariance = 0.0

        assets.forEach { asset ->
            val history = repository.getStockHistory(asset.symbol).first().map { it.price }
            val weight = if (totalValue > 0) asset.totalValue / totalValue else 0.0
            
            val volatility = if (history.size > 2) {
                val returns = history.zipWithNext { a, b -> (b - a) / a }
                val mean = returns.average()
                val variance = returns.map { (it - mean) * (it - mean) }.average()
                sqrt(variance) * sqrt(252.0) // Annualized
            } else 0.15 // Default fallback

            assetMetrics.add(AssetRiskMetric(asset.symbol, volatility, weight))
            portfolioVariance += (weight * volatility) * (weight * volatility) // Simplified, ignoring correlation for now
        }

        val totalVolatility = sqrt(portfolioVariance)
        val riskFreeRate = 0.45 // Example: 45% (TR context)
        val portfolioAnnReturn = assets.sumOf { (it.profitPercent / 100.0) } // Simplified
        val sharpe = if (totalVolatility > 0) (portfolioAnnReturn - riskFreeRate) / totalVolatility else 0.0

        val suggestions = mutableListOf<String>()
        if (sharpe < 1.0) suggestions.add("Portföy risk/getiri oranı düşük. Volatilitesi yüksek varlıkları azaltmayı düşünün.")
        if (totalVolatility > 0.30) suggestions.add("Portföy oynaklığı yüksek. Daha dengeli sektörlere yönelin.")

        emit(OptimizationResult(sharpe, totalVolatility, assetMetrics, suggestions))
    }.flowOn(Dispatchers.Default)
}
