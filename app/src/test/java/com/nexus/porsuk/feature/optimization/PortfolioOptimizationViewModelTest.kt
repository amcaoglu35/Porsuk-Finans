package com.nexus.porsuk.feature.optimization

import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

/**
 * Porsuk Portfolio Optimization & Asset Allocation Engine — ViewModel Unit Testleri
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PortfolioOptimizationViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val fakeOptimizationRepository = object : OptimizationRepository {
        override fun getEfficientFrontierPoints() = flowOf(listOf(EfficientFrontierPoint(expectedReturnPct = 21.0, volatilityPct = 14.8)))
        override suspend fun runOptimization(strategy: OptimizationStrategyType) = listOf(AssetAllocationItem(symbol = "THYAO.IS"))
    }

    private val fakeAllocationRepository = object : AllocationRepository {
        override fun getCurrentAllocations() = flowOf(listOf(AssetAllocationItem(symbol = "THYAO.IS")))
        override suspend fun updateTargetAllocation(symbol: String, targetWeightPct: Double) {}
    }

    private val fakeOptimizationRiskRepository = object : OptimizationRiskRepository {
        override fun getPortfolioRiskMetrics() = flowOf(PortfolioRiskMetrics(sharpeRatio = 1.94))
    }

    private val fakeScenarioRepository = object : OptimizationScenarioRepository {
        override fun getStressTestScenarios() = flowOf(listOf(StressTestScenario(scenarioId = "sc_1")))
        override suspend fun runStressTest(scenarioId: String) = -14.2
    }

    private val fakeRebalancingRepository = object : RebalancingRepository {
        override fun getRebalanceSuggestions() = flowOf(listOf(RebalanceSuggestion(symbol = "THYAO.IS")))
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadOptimizationData updates uiState with allocations, risk metrics, and frontier points`() = runTest {
        val viewModel = PortfolioOptimizationViewModel(
            optimizationRepository = fakeOptimizationRepository,
            allocationRepository = fakeAllocationRepository,
            optimizationRiskRepository = fakeOptimizationRiskRepository,
            scenarioRepository = fakeScenarioRepository,
            rebalancingRepository = fakeRebalancingRepository
        )

        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(OptimizationStrategyType.MAXIMUM_SHARPE, state.selectedStrategy)
        assertEquals(1, state.allocations.size)
        assertEquals("THYAO.IS", state.allocations[0].symbol)
        assertEquals(1.94, state.riskMetrics.sharpeRatio, 0.01)
        assertEquals(1, state.frontierPoints.size)
        assertEquals(false, state.isLoading)
    }

    @Test
    fun `selectStrategy runs optimization and updates allocations`() = runTest {
        val viewModel = PortfolioOptimizationViewModel(
            optimizationRepository = fakeOptimizationRepository,
            allocationRepository = fakeAllocationRepository,
            optimizationRiskRepository = fakeOptimizationRiskRepository,
            scenarioRepository = fakeScenarioRepository,
            rebalancingRepository = fakeRebalancingRepository
        )

        viewModel.selectStrategy(OptimizationStrategyType.MINIMUM_VARIANCE)
        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(OptimizationStrategyType.MINIMUM_VARIANCE, state.selectedStrategy)
        assertEquals(1, state.allocations.size)
    }
}
