package com.nexus.porsuk.data.repository

import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OptimizationRepositoryImpl @Inject constructor() : OptimizationRepository {

    private val defaultPoints = listOf(
        EfficientFrontierPoint(expectedReturnPct = 12.0, volatilityPct = 8.5),
        EfficientFrontierPoint(expectedReturnPct = 16.5, volatilityPct = 11.2),
        EfficientFrontierPoint(expectedReturnPct = 21.0, volatilityPct = 14.8, isOptimalTangencyPoint = true),
        EfficientFrontierPoint(expectedReturnPct = 26.5, volatilityPct = 20.4)
    )

    private val frontierState = MutableStateFlow(defaultPoints)

    override fun getEfficientFrontierPoints(): Flow<List<EfficientFrontierPoint>> = frontierState.asStateFlow()

    override suspend fun runOptimization(strategy: OptimizationStrategyType): List<AssetAllocationItem> {
        return listOf(
            AssetAllocationItem(symbol = "THYAO.IS", name = "Türk Hava Yolları", assetClass = AssetClassType.STOCKS, currentWeightPct = 35.0, targetOptimizedWeightPct = 22.5),
            AssetAllocationItem(symbol = "GARAN.IS", name = "Garanti Bankası", assetClass = AssetClassType.STOCKS, currentWeightPct = 25.0, targetOptimizedWeightPct = 20.0),
            AssetAllocationItem(symbol = "US10Y", name = "ABD 10Y Tahvil", assetClass = AssetClassType.BONDS, currentWeightPct = 20.0, targetOptimizedWeightPct = 30.0),
            AssetAllocationItem(symbol = "XAU-USD", name = "Ons Altın", assetClass = AssetClassType.GOLD, currentWeightPct = 20.0, targetOptimizedWeightPct = 27.5)
        )
    }
}

@Singleton
class AllocationRepositoryImpl @Inject constructor() : AllocationRepository {

    private val defaultAllocations = listOf(
        AssetAllocationItem(symbol = "THYAO.IS", name = "Türk Hava Yolları", assetClass = AssetClassType.STOCKS, currentWeightPct = 35.0, targetOptimizedWeightPct = 22.5),
        AssetAllocationItem(symbol = "GARAN.IS", name = "Garanti Bankası", assetClass = AssetClassType.STOCKS, currentWeightPct = 25.0, targetOptimizedWeightPct = 20.0),
        AssetAllocationItem(symbol = "US10Y", name = "ABD 10Y Tahvil", assetClass = AssetClassType.BONDS, currentWeightPct = 20.0, targetOptimizedWeightPct = 30.0),
        AssetAllocationItem(symbol = "XAU-USD", name = "Ons Altın", assetClass = AssetClassType.GOLD, currentWeightPct = 20.0, targetOptimizedWeightPct = 27.5)
    )

    private val allocationsState = MutableStateFlow(defaultAllocations)

    override fun getCurrentAllocations(): Flow<List<AssetAllocationItem>> = allocationsState.asStateFlow()

    override suspend fun updateTargetAllocation(symbol: String, targetWeightPct: Double) {
        allocationsState.update { current ->
            current.map { if (it.symbol == symbol) it.copy(targetOptimizedWeightPct = targetWeightPct) else it }
        }
    }
}

@Singleton
class OptimizationRiskRepositoryImpl @Inject constructor() : OptimizationRiskRepository {
    private val riskMetricsState = MutableStateFlow(PortfolioRiskMetrics())

    override fun getPortfolioRiskMetrics(): Flow<PortfolioRiskMetrics> = riskMetricsState.asStateFlow()
}

@Singleton
class OptimizationScenarioRepositoryImpl @Inject constructor() : OptimizationScenarioRepository {

    private val defaultScenarios = listOf(
        StressTestScenario(scenarioId = "sc_1", name = "2008 Küresel Finansal Kriz Sıçraması", category = "Tarihsel Şok", expectedPortfolioChangePct = -14.2),
        StressTestScenario(scenarioId = "sc_2", name = "Yüksek Enflasyon & Şok Faiz Artışı", category = "Makro Şok", expectedPortfolioChangePct = -8.5),
        StressTestScenario(scenarioId = "sc_3", name = "Döviz Kuru Sıçraması (Kur Şoku)", category = "FX Şoku", expectedPortfolioChangePct = +6.4)
    )

    private val scenariosState = MutableStateFlow(defaultScenarios)

    override fun getStressTestScenarios(): Flow<List<StressTestScenario>> = scenariosState.asStateFlow()

    override suspend fun runStressTest(scenarioId: String): Double {
        return scenariosState.value.find { it.scenarioId == scenarioId }?.expectedPortfolioChangePct ?: -10.0
    }
}

@Singleton
class RebalancingRepositoryImpl @Inject constructor() : RebalancingRepository {

    private val defaultSuggestions = listOf(
        RebalanceSuggestion(symbol = "THYAO.IS", actionText = "Ağırlık %12.5 azaltılsın", currentWeightPct = 35.0, targetWeightPct = 22.5, driftPct = 12.5),
        RebalanceSuggestion(symbol = "US10Y", actionText = "Ağırlık %10.0 artırılsın", currentWeightPct = 20.0, targetWeightPct = 30.0, driftPct = 10.0)
    )

    private val rebalanceState = MutableStateFlow(defaultSuggestions)

    override fun getRebalanceSuggestions(): Flow<List<RebalanceSuggestion>> = rebalanceState.asStateFlow()
}
