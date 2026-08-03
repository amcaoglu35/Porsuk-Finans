package com.nexus.porsuk.domain.usecase.portfolio

import com.nexus.porsuk.data.repository.FinanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

data class PortfolioSummary(
    val totalBalance: Double = 0.0,
    val totalCost: Double = 0.0,
    val totalChangePercent: Double = 0.0
)

class GetPortfolioSummaryUseCase @Inject constructor(
    private val repository: FinanceRepository
) {
    operator fun invoke(): Flow<PortfolioSummary> {
        return repository.getConsolidatedAssetsFlow().map { assets ->
            val balance = assets.sumOf { it.totalValue }
            val cost = assets.sumOf { it.totalCost }
            val changePct = if (cost > 0) ((balance - cost) / cost) * 100.0 else 0.0
            PortfolioSummary(
                totalBalance = balance,
                totalCost = cost,
                totalChangePercent = changePct
            )
        }
    }
}
