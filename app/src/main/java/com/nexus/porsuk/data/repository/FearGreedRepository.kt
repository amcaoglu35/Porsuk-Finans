package com.nexus.porsuk.data.repository

import com.nexus.porsuk.data.local.entity.Company
import com.nexus.porsuk.data.local.entity.PriceSnapshot
import com.nexus.porsuk.domain.model.FearGreedModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FearGreedRepository @Inject constructor() {
    
    /**
     * Calculates Fear & Greed score based on VIX and Market Breadth.
     * This is a pure calculation based on provided snapshots to avoid refresh loops.
     */
    fun calculateFearGreed(
        priceMap: Map<String, PriceSnapshot>,
        allCompanies: List<Company>
    ): FearGreedModel {
        // 1. Get VIX from map or use baseline fallback
        val vixValue = priceMap["^VIX"]?.price ?: 15.8

        // 2. Calculate Market Breadth (Percentage of rising stocks in BIST)
        val bistCompanies = allCompanies.filter { it.market == "BIST" || it.market == "IST" }
        val totalCount = bistCompanies.size.coerceAtLeast(1)
        val upCount = bistCompanies.count { 
            (priceMap[it.symbol]?.changePercent ?: it.changePercent) > 0 
        }
        val breadthPct = (upCount.toDouble() / totalCount) * 100.0

        // 3. Composite Scoring (0-100)
        // VIX: 10 (Greed) to 40 (Fear) -> mapped to 50 (Greed) to 0 (Fear)
        val vixComponent = ((40.0 - vixValue) / 30.0 * 50.0).coerceIn(0.0, 50.0)
        
        // Breadth: 0% (Fear) to 100% (Greed) -> mapped to 0 to 50
        val breadthComponent = (breadthPct / 100.0) * 50.0
        
        val score = (vixComponent + breadthComponent).toInt().coerceIn(0, 100)
        
        val label = when {
            score >= 80 -> "Aşırı Hırs"
            score >= 60 -> "Hırs"
            score >= 40 -> "Nötr"
            score >= 20 -> "Korku"
            else -> "Aşırı Korku"
        }

        return FearGreedModel(
            score = score,
            label = label,
            vixValue = vixValue,
            breadthPct = breadthPct
        )
    }
}
