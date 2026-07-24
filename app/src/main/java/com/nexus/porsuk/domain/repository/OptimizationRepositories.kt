package com.nexus.porsuk.domain.repository

import com.nexus.porsuk.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * 1. Portföy Optimizasyon Deposu Sözleşmesi (OptimizationRepository)
 */
interface OptimizationRepository {
    fun getEfficientFrontierPoints(): Flow<List<EfficientFrontierPoint>>
    suspend fun runOptimization(strategy: OptimizationStrategyType): List<AssetAllocationItem>
}

/**
 * 2. Varlık Dağılımı Deposu Sözleşmesi (AllocationRepository)
 */
interface AllocationRepository {
    fun getCurrentAllocations(): Flow<List<AssetAllocationItem>>
    suspend fun updateTargetAllocation(symbol: String, targetWeightPct: Double)
}

/**
 * 3. Optimizasyon Risk Deposu Sözleşmesi (OptimizationRiskRepository)
 */
interface OptimizationRiskRepository {
    fun getPortfolioRiskMetrics(): Flow<PortfolioRiskMetrics>
}

/**
 * 4. Senaryo & Stres Testi Deposu Sözleşmesi (OptimizationScenarioRepository)
 */
interface OptimizationScenarioRepository {
    fun getStressTestScenarios(): Flow<List<StressTestScenario>>
    suspend fun runStressTest(scenarioId: String): Double
}

/**
 * 5. Yeniden Dengeleme Deposu Sözleşmesi (RebalancingRepository)
 */
interface RebalancingRepository {
    fun getRebalanceSuggestions(): Flow<List<RebalanceSuggestion>>
}
