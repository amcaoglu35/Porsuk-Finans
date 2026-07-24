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
 * Porsuk Quant Research Studio — ViewModel
 *
 * Niceliksel faktör analizi, istatistiksel korelasyon matrisi ve araştırma not defterini yönetir.
 */
@HiltViewModel
class QuantResearchViewModel @Inject constructor(
    private val researchRepository: ResearchRepository,
    private val factorRepository: FactorRepository,
    private val statisticsRepository: StatisticsRepository,
    private val datasetRepository: DatasetRepository,
    private val workspaceRepository: QuantWorkspaceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuantResearchUiState())
    val uiState: StateFlow<QuantResearchUiState> = _uiState.asStateFlow()

    init {
        loadQuantData()
    }

    fun selectFactorCategory(category: FactorCategory?) {
        _uiState.update { it.copy(selectedFactorCategory = category) }
    }

    fun saveWorkspaceNotes(notes: String) {
        viewModelScope.launch {
            researchRepository.saveWorkspaceNotes(notes)
        }
    }

    fun switchWorkspace(workspaceId: String) {
        viewModelScope.launch {
            workspaceRepository.switchWorkspace(workspaceId)
        }
    }

    private fun loadQuantData() {
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
