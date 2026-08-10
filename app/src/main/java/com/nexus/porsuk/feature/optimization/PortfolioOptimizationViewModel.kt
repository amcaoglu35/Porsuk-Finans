package com.nexus.porsuk.feature.optimization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import com.nexus.porsuk.domain.usecase.optimization.CalculatePortfolioOptimizationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Porsuk Portfolio Optimization & Asset Allocation Engine — ViewModel
 *
 * Markowitz etken sınır, maksimum Sharpe optimizasyonu, varlık dağılımı ve stres testini yönetir.
 */
@HiltViewModel
class PortfolioOptimizationViewModel @Inject constructor(
    private val optimizationRepository: OptimizationRepository,
    private val allocationRepository: AllocationRepository,
    private val optimizationRiskRepository: OptimizationRiskRepository,
    private val scenarioRepository: OptimizationScenarioRepository,
    private val rebalancingRepository: RebalancingRepository,
    private val calculatePortfolioOptimizationUseCase: CalculatePortfolioOptimizationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PortfolioOptimizationUiState())
    val uiState: StateFlow<PortfolioOptimizationUiState> = _uiState.asStateFlow()

    init {
        loadOptimizationData()
        runRealOptimization()
    }

    private fun runRealOptimization() {
        viewModelScope.launch {
            calculatePortfolioOptimizationUseCase().collect { result ->
                _uiState.update { current ->
                    current.copy(
                        riskMetrics = current.riskMetrics.copy(
                            sharpeRatio = result.sharpeRatio,
                            standardDeviationPct = result.totalVolatility * 100.0
                        ),
                        frontierPoints = result.assetMetrics.map { 
                            EfficientFrontierPoint(
                                expectedReturnPct = it.weight * 100.0, // Placeholder mapping
                                volatilityPct = it.volatility * 100.0
                            )
                        },
                        rebalanceSuggestions = result.suggestions.map { 
                            RebalanceSuggestion(symbol = "AI", actionText = it, currentWeightPct = 0.0, targetWeightPct = 0.0, driftPct = 0.0)
                        },
                        isLoading = false,
                        errorMessage = if (result.assetMetrics.isEmpty()) result.suggestions.firstOrNull() else null
                    )
                }
            }
        }
    }

    fun selectStrategy(strategy: OptimizationStrategyType) {
        viewModelScope.launch {
            _uiState.update { it.copy(selectedStrategy = strategy, isLoading = true) }
            val optimizedAllocations = optimizationRepository.runOptimization(strategy)
            _uiState.update { it.copy(allocations = optimizedAllocations, isLoading = false) }
        }
    }

    fun runScenarioStressTest(scenarioId: String) {
        viewModelScope.launch {
            val impact = scenarioRepository.runStressTest(scenarioId)
            _uiState.update { it.copy(selectedScenarioChangePct = impact) }
        }
    }

    private fun loadOptimizationData() {
        viewModelScope.launch {
            launch {
                allocationRepository.getCurrentAllocations().collect { allocs ->
                    _uiState.update { it.copy(allocations = allocs, isLoading = false) }
                }
            }

            launch {
                optimizationRiskRepository.getPortfolioRiskMetrics().collect { metrics ->
                    _uiState.update { it.copy(riskMetrics = metrics) }
                }
            }

            launch {
                optimizationRepository.getEfficientFrontierPoints().collect { pts ->
                    _uiState.update { it.copy(frontierPoints = pts) }
                }
            }

            launch {
                rebalancingRepository.getRebalanceSuggestions().collect { reb ->
                    _uiState.update { it.copy(rebalanceSuggestions = reb) }
                }
            }

            launch {
                scenarioRepository.getStressTestScenarios().collect { scs ->
                    _uiState.update { it.copy(stressTestScenarios = scs) }
                }
            }
        }
    }
}
