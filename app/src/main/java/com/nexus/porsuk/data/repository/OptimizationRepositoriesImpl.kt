package com.nexus.porsuk.data.repository

import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OptimizationRepositoryImpl @Inject constructor() : OptimizationRepository {

    override fun getEfficientFrontierPoints(): Flow<List<EfficientFrontierPoint>> = flowOf(emptyList())

    override suspend fun runOptimization(strategy: OptimizationStrategyType): List<AssetAllocationItem> {
        return emptyList()
    }
}

@Singleton
class AllocationRepositoryImpl @Inject constructor() : AllocationRepository {

    override fun getCurrentAllocations(): Flow<List<AssetAllocationItem>> = flowOf(emptyList())

    override suspend fun updateTargetAllocation(symbol: String, targetWeightPct: Double) {
    }
}

@Singleton
class OptimizationRiskRepositoryImpl @Inject constructor() : OptimizationRiskRepository {
    override fun getPortfolioRiskMetrics(): Flow<PortfolioRiskMetrics> = flowOf(PortfolioRiskMetrics())
}

@Singleton
class OptimizationScenarioRepositoryImpl @Inject constructor() : OptimizationScenarioRepository {

    override fun getStressTestScenarios(): Flow<List<StressTestScenario>> = flowOf(emptyList())

    override suspend fun runStressTest(scenarioId: String): Double = 0.0
}

@Singleton
class RebalancingRepositoryImpl @Inject constructor() : RebalancingRepository {

    override fun getRebalanceSuggestions(): Flow<List<RebalanceSuggestion>> = flowOf(emptyList())
}
