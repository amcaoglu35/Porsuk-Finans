package com.nexus.porsuk.feature.reporting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.ReportingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReportingUiState(
    val savedReports: List<EnterpriseReport> = emptyList(),
    val isGenerating: Boolean = false,
    val selectedTab: Int = 0,
    val portfolioData: PortfolioReportData? = null,
    val aiData: AiReportData? = null,
    val riskData: RiskReportData? = null,
    val taxData: TaxReportData? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class ReportingViewModel @Inject constructor(
    private val repository: ReportingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportingUiState())
    val uiState: StateFlow<ReportingUiState> = _uiState.asStateFlow()

    init {
        loadSavedReports()
        loadInitialData()
    }

    private fun loadSavedReports() {
        viewModelScope.launch {
            repository.getSavedReports().collect { reports ->
                _uiState.update { it.copy(savedReports = reports) }
            }
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            val portfolio = repository.getPortfolioReportData()
            val ai = repository.getAiReportData()
            val risk = repository.getRiskReportData()
            val tax = repository.getTaxReportData()
            
            _uiState.update { 
                it.copy(
                    portfolioData = portfolio,
                    aiData = ai,
                    riskData = risk,
                    taxData = tax
                )
            }
        }
    }

    fun selectTab(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }

    fun generateReport(type: ReportType, format: ReportFormat) {
        viewModelScope.launch {
            _uiState.update { it.copy(isGenerating = true) }
            try {
                repository.generateReport(type, format)
                _uiState.update { it.copy(isGenerating = false) }
                // Trigger file open or share logic
            } catch (e: Exception) {
                _uiState.update { it.copy(isGenerating = false, errorMessage = e.localizedMessage) }
            }
        }
    }

    fun deleteReport(reportId: String) {
        viewModelScope.launch {
            repository.deleteReport(reportId)
        }
    }
}
