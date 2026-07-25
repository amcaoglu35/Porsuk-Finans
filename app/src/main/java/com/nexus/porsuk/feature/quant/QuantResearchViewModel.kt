package com.nexus.porsuk.feature.quant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Porsuk Quantitative AI Research & Alpha Factory Platform — ViewModel
 */
@HiltViewModel
class QuantResearchViewModel @Inject constructor(
    private val researchRepository: ResearchRepository,
    private val factorRepository: FactorRepository,
    private val statisticsRepository: StatisticsRepository,
    private val datasetRepository: DatasetRepository,
    private val workspaceRepository: QuantWorkspaceRepository,
    private val quantRepository: QuantRepository,
    private val experimentRepository: ExperimentRepository,
    private val validationRepository: ValidationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuantResearchUiState())
    val uiState: StateFlow<QuantResearchUiState> = _uiState.asStateFlow()

    init {
        loadQuantPlatformData()
    }

    fun selectTab(tab: QuantPlatformTab) {
        _uiState.update { it.copy(activeTab = tab) }
    }

    fun selectFactorCategory(category: FactorCategory?) {
        _uiState.update { it.copy(selectedFactorCategory = category) }
    }

    fun changeCombinationStrategy(strategy: FactorCombinationStrategy) {
        viewModelScope.launch {
            _uiState.update { it.copy(selectedCombinationStrategy = strategy) }
            val symbols = listOf("THYAO.IS", "GARAN.IS", "AKBNK.IS", "EREGL.IS", "TUPRS.IS")
            val combined = factorRepository.combineFactors(symbols, strategy)
            _uiState.update { it.copy(combinationResults = combined) }
        }
    }

    fun selectAcademicModel(modelType: AcademicModelType, symbol: String = "THYAO.IS") {
        viewModelScope.launch {
            _uiState.update { it.copy(selectedAcademicModel = modelType) }
            val result = statisticsRepository.calculateAcademicModel(symbol, modelType)
            _uiState.update { it.copy(academicModelResult = result) }
        }
    }

    fun runWalkForwardValidation(strategyId: String = "bist_multi_factor_alpha") {
        viewModelScope.launch {
            val result = validationRepository.runWalkForwardAnalysis(strategyId, inSampleMonths = 24, outOfSampleMonths = 6)
            _uiState.update { it.copy(walkForwardResult = result) }
        }
    }

    fun runBootstrapValidation(strategyId: String = "bist_multi_factor_alpha") {
        viewModelScope.launch {
            val result = validationRepository.runBootstrapSimulation(strategyId, simulationsCount = 1000)
            _uiState.update { it.copy(bootstrapResult = result) }
        }
    }

    fun evaluateMlModel(modelId: String) {
        viewModelScope.launch {
            val result = quantRepository.runMlModelEvaluation(modelId)
            _uiState.update { it.copy(activeMlEvaluation = result) }
        }
    }

    fun saveWorkspaceNotes(notes: String) {
        viewModelScope.launch {
            researchRepository.saveWorkspaceNotes(notes)
        }
    }

    fun saveExperiment(title: String, params: Map<String, String>, metrics: Map<String, Double>, notes: String) {
        viewModelScope.launch {
            experimentRepository.saveExperiment(title, params, metrics, notes)
        }
    }

    fun deleteExperiment(experimentId: String) {
        viewModelScope.launch {
            experimentRepository.deleteExperiment(experimentId)
        }
    }

    fun switchWorkspace(workspaceId: String) {
        viewModelScope.launch {
            workspaceRepository.switchWorkspace(workspaceId)
        }
    }

    private fun loadQuantPlatformData() {
        viewModelScope.launch {
            launch {
                researchRepository.getActiveResearchWorkspace().collect { ws ->
                    _uiState.update { it.copy(activeWorkspace = ws, isLoading = false) }
                }
            }

            launch {
                factorRepository.getFactorMetrics().collect { factors ->
                    _uiState.update { it.copy(factorMetrics = factors) }
                }
            }

            launch {
                factorRepository.getAlphaFactorDefinitions().collect { defs ->
                    _uiState.update { it.copy(alphaFactorDefs = defs) }
                }
            }

            launch {
                val symbols = listOf("THYAO.IS", "GARAN.IS", "AKBNK.IS", "EREGL.IS", "TUPRS.IS")
                val combined = factorRepository.combineFactors(symbols, FactorCombinationStrategy.IC_WEIGHTED)
                _uiState.update { it.copy(combinationResults = combined) }
            }

            launch {
                val defaultAcademicResult = statisticsRepository.calculateAcademicModel("THYAO.IS", AcademicModelType.FAMA_FRENCH_5)
                _uiState.update { it.copy(academicModelResult = defaultAcademicResult) }
            }

            launch {
                val wf = validationRepository.runWalkForwardAnalysis("bist_multi_factor_alpha", 24, 6)
                val rw = validationRepository.runRollingWindowAnalysis("THYAO.IS", 60)
                val bs = validationRepository.runBootstrapSimulation("bist_multi_factor_alpha", 1000)
                _uiState.update { it.copy(walkForwardResult = wf, rollingWindowResult = rw, bootstrapResult = bs) }
            }

            launch {
                val decay = statisticsRepository.getFactorDecay("f_momentum_12m")
                val persistence = statisticsRepository.getFactorPersistence("f_momentum_12m")
                val corr = statisticsRepository.getFactorCorrelationMatrix()
                val attr = statisticsRepository.getPerformanceAttribution("PORTFOLIO_ALPHA_1")
                _uiState.update {
                    it.copy(
                        factorDecay = decay,
                        factorPersistence = persistence,
                        correlationMatrix = corr,
                        performanceAttribution = attr
                    )
                }
            }

            launch {
                datasetRepository.getFeatureStoreDefinitions().collect { feats ->
                    _uiState.update { it.copy(featureDefinitions = feats) }
                }
            }

            launch {
                quantRepository.getMlModelConfigs().collect { configs ->
                    _uiState.update { it.copy(mlModelConfigs = configs) }
                }
            }

            launch {
                quantRepository.getFutureReadySuite().collect { future ->
                    _uiState.update { it.copy(futureSuite = future) }
                }
            }

            launch {
                experimentRepository.getSavedExperiments().collect { exps ->
                    _uiState.update { it.copy(savedExperiments = exps) }
                }
            }

            launch {
                statisticsRepository.getStatisticalAnalysis("THYAO.IS / PGSUS.IS").collect { stats ->
                    _uiState.update { it.copy(statisticalResult = stats) }
                }
            }

            launch {
                statisticsRepository.getPortfolioResearchMetrics().collect { port ->
                    _uiState.update { it.copy(portfolioResearch = port) }
                }
            }

            launch {
                datasetRepository.getAvailableDatasets().collect { ds ->
                    _uiState.update { it.copy(datasets = ds) }
                }
            }

            launch {
                workspaceRepository.getSavedWorkspaces().collect { saved ->
                    _uiState.update { it.copy(savedWorkspaces = saved) }
                }
            }
        }
    }
}
