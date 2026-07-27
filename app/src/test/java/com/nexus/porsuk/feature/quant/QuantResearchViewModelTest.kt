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
        override fun getResearchSessions() = flowOf(emptyList<ResearchWorkspace>())
        override suspend fun createNewSession(title: String, author: String) = ResearchWorkspace()
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
        override fun getAlphaFactorDefinitions() = flowOf(emptyList<AlphaFactorDefinition>())
        override suspend fun calculateFactorRanking(factorId: String) = FactorRankingResult(factorId, emptyList(), emptyList(), emptyList())
        override suspend fun calculateFactorExposure(symbol: String) = FactorExposureResult(symbol, emptyMap(), MultiFactorCategory.VALUE, 0.0)
        override suspend fun combineFactors(symbols: List<String>, strategy: FactorCombinationStrategy) = emptyList<FactorCombinationResult>()
        override suspend fun saveCustomFactorFormula(title: String, expression: String) = CustomFactorFormula("f_custom", title, expression)
    }

    private val fakeStatisticsRepository = object : StatisticsRepository {
        override fun getStatisticalAnalysis(assetPair: String) = flowOf(StatisticalAnalysisResult())
        override fun getPortfolioResearchMetrics() = flowOf(PortfolioResearchMetrics())
        override suspend fun calculateAcademicModel(symbol: String, modelType: AcademicModelType) = AcademicModelResult(
            symbol, modelType, 0.0, 0.0, false, 0.0, 0.0, 0.0, emptyList(), 0.0, 0.0
        )
        override suspend fun getFactorDecay(factorId: String) = FactorDecayMetrics(factorId, 0.0, 0.0, 0.0, 0.0, 0.0)
        override suspend fun getFactorPersistence(factorId: String) = FactorPersistenceMetrics(factorId, 0.0, 0.0, 0.0, 0.0, 0.0, emptyList())
        override suspend fun getFactorCorrelationMatrix() = FactorCorrelationMatrix(emptyList(), emptyList())
        override suspend fun getPerformanceAttribution(symbolOrPortfolio: String) = PerformanceAttributionResult(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, emptyMap())
    }

    private val fakeDatasetRepository = object : DatasetRepository {
        override fun getAvailableDatasets() = flowOf(listOf(DatasetItem()))
        override suspend fun loadDataset(datasetId: String) = true
        override fun getFeatureStoreDefinitions() = flowOf(emptyList<FeatureDefinition>())
        override suspend fun applyFeatureTransformation(featureId: String, transformation: FeatureTransformationType) = emptyList<Double>()
    }

    private val fakeWorkspaceRepository = object : QuantWorkspaceRepository {
        override fun getSavedWorkspaces() = flowOf(listOf(ResearchWorkspace()))
        override suspend fun switchWorkspace(workspaceId: String) {}
    }

    private val fakeQuantRepository = object : QuantRepository {
        override fun getFutureReadySuite() = flowOf(FutureReadyQuantSuite())
        override fun getMlModelConfigs() = flowOf(emptyList<MlModelConfig>())
        override suspend fun runMlModelEvaluation(modelId: String) = MlEvaluationResult(modelId, MlTaskType.REGRESSION, 0.0, 0.0, 0.0, emptyMap())
    }

    private val fakeExperimentRepository = object : ExperimentRepository {
        override fun getSavedExperiments() = flowOf(emptyList<QuantExperiment>())
        override suspend fun saveExperiment(title: String, params: Map<String, String>, metrics: Map<String, Double>, notes: String) = QuantExperiment(
            "exp_1", title, "v1", params, metrics, notes
        )
        override suspend fun deleteExperiment(experimentId: String) {}
    }

    private val fakeValidationRepository = object : ValidationRepository {
        override suspend fun runWalkForwardAnalysis(strategyId: String, inSampleMonths: Int, outOfSampleMonths: Int) = WalkForwardResult(
            strategyId, inSampleMonths, outOfSampleMonths, 0, false, 0.0, 0.0, 0.0, 0.0, emptyList()
        )
        override suspend fun runRollingWindowAnalysis(symbol: String, windowDays: Int) = RollingWindowResult(
            symbol, windowDays, emptyList(), emptyList(), emptyList()
        )
        override suspend fun runBootstrapSimulation(strategyId: String, simulationsCount: Int) = BootstrapResult(
            simulationsCount, 0.0, 0.0, 0.0, 0.0, 0.0
        )
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
            workspaceRepository = fakeWorkspaceRepository,
            quantRepository = fakeQuantRepository,
            experimentRepository = fakeExperimentRepository,
            validationRepository = fakeValidationRepository
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
