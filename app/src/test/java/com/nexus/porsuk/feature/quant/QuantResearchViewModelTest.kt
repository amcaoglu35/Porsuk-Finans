package com.nexus.porsuk.feature.quant

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
 * Porsuk Quant Research Studio — ViewModel Unit Testleri
 */
@OptIn(ExperimentalCoroutinesApi::class)
class QuantResearchViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val fakeResearchRepository = object : ResearchRepository {
        override fun getActiveResearchWorkspace() = flowOf(ResearchWorkspace(title = "Test Quant Model"))
        override suspend fun saveWorkspaceNotes(notes: String) {}
    }

    private val fakeFactorRepository = object : FactorRepository {
        override fun getFactorMetrics() = flowOf(
            listOf(
                FactorMetric(
                    factorId = "f_pe",
                    name = "P/E Ratio",
                    category = FactorCategory.VALUE,
                    rawValue = 8.0,
                    zScore = -1.0,
                    percentileRank = 80.0,
                    description = "Test factor"
                )
            )
        )
        override suspend fun calculateCompositeFactorScore(symbol: String) = 85.0
    }

    private val fakeStatisticsRepository = object : StatisticsRepository {
        override fun getStatisticalAnalysis(assetPair: String) = flowOf(StatisticalAnalysisResult())
        override fun getPortfolioResearchMetrics() = flowOf(PortfolioResearchMetrics())
    }

    private val fakeDatasetRepository = object : DatasetRepository {
        override fun getAvailableDatasets() = flowOf(listOf(DatasetItem()))
        override suspend fun loadDataset(datasetId: String) = true
    }

    private val fakeWorkspaceRepository = object : QuantWorkspaceRepository {
        override fun getSavedWorkspaces() = flowOf(listOf(ResearchWorkspace()))
        override suspend fun switchWorkspace(workspaceId: String) {}
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
    fun `loadQuantData updates uiState with active workspace and factor metrics`() = runTest {
        val viewModel = QuantResearchViewModel(
            researchRepository = fakeResearchRepository,
            factorRepository = fakeFactorRepository,
            statisticsRepository = fakeStatisticsRepository,
            datasetRepository = fakeDatasetRepository,
            workspaceRepository = fakeWorkspaceRepository
        )

        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Test Quant Model", state.activeWorkspace.title)
        assertEquals(1, state.factorMetrics.size)
        assertEquals("P/E Ratio", state.factorMetrics[0].name)
        assertEquals(false, state.isLoading)
        assertNotNull(state.statisticalResult)
    }
}
