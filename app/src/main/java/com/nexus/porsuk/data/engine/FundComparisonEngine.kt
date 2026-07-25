package com.nexus.porsuk.data.engine

import com.nexus.porsuk.domain.model.FundAllocation
import com.nexus.porsuk.domain.model.FundComparison
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FundComparisonEngine @Inject constructor() {
    fun compare(base: FundAllocation, target: FundAllocation): FundComparison {
        val baseHoldings = base.topHoldings.map { it.symbol }.toSet()
        val targetHoldings = target.topHoldings.map { it.symbol }.toSet()
        
        val common = baseHoldings.intersect(targetHoldings)
        
        // Calculate weighted overlap
        var overlap = 0.0
        common.forEach { symbol ->
            val w1 = base.topHoldings.find { it.symbol == symbol }?.weight ?: 0.0
            val w2 = target.topHoldings.find { it.symbol == symbol }?.weight ?: 0.0
            overlap += minOf(w1, w2)
        }
        
        return FundComparison(
            baseFundCode = base.fundCode,
            targetFundCode = target.fundCode,
            overlapPercentage = overlap,
            commonHoldings = common.toList(),
            feeDifference = 0.0, // Should be calculated from intelligence
            performanceDifference = emptyMap()
        )
    }
}
