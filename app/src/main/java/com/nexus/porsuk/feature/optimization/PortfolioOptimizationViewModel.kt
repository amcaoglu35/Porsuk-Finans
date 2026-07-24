package com.nexus.porsuk.feature.optimization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
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
    private val rebalancingRepository: RebalancingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PortfolioOptimizationUiState())
    val uiState: StateFlow<PortfolioOptimizationUiState> = _uiState.asStateFlow()

    init {
        loadOptimizationData()
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
