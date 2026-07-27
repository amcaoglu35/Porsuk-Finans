package com.nexus.porsuk.ui.institutional

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.InstitutionalAnalyticsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InstitutionalUiState(
    val marketOverview: InstitutionalMarketOverview? = null,
    val sectorAnalytics: List<SectorAnalytics> = emptyList(),
    val companyAnalysis: InstitutionalCompanyAnalysis? = null,
    val portfolioAnalytics: InstitutionalPortfolioAnalytics? = null,
    val aiInsights: List<InstitutionalAiInsight> = emptyList(),
    val isLoading: Boolean = false,
    val selectedTab: Int = 0,
    val errorMessage: String? = null
)

@HiltViewModel
class InstitutionalViewModel @Inject constructor(
    private val repository: InstitutionalAnalyticsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(InstitutionalUiState())
    val uiState: StateFlow<InstitutionalUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            combine(
                repository.getMarketOverview(),
                repository.getSectorAnalytics(),
                repository.getPortfolioInstitutionalAnalytics(),
                repository.getInstitutionalAiInsights()
            ) { overview, sectors, portfolio, insights ->
                InstitutionalUiState(
                    marketOverview = overview,
                    sectorAnalytics = sectors,
                    portfolioAnalytics = portfolio,
                    aiInsights = insights,
                    isLoading = false,
                    selectedTab = _uiState.value.selectedTab
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun selectTab(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }

    fun analyzeCompany(symbol: String) {
        viewModelScope.launch {
            repository.getCompanyInstitutionalAnalysis(symbol).collect { analysis ->
                _uiState.update { it.copy(companyAnalysis = analysis) }
            }
        }
    }
}
