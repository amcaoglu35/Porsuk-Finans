package com.nexus.porsuk.feature.filings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Porsuk Regulatory Filings & Disclosure Intelligence Platform — ViewModel
 *
 * KAP/SEC resmi bildirimlerini, kategori filtrelemeyi, şirket zaman çizelgesini ve AI özetlerini yönetir.
 */
@HiltViewModel
class RegulatoryFilingViewModel @Inject constructor(
    private val filingRepository: FilingRepository,
    private val disclosureRepository: DisclosureRepository,
    private val documentRepository: DocumentRepository,
    private val classificationRepository: ClassificationRepository,
    private val timelineRepository: TimelineRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegulatoryFilingUiState())
    val uiState: StateFlow<RegulatoryFilingUiState> = _uiState.asStateFlow()

    init {
        loadFilingsData()
    }

    fun selectProvider(provider: FilingProviderType) {
        _uiState.update { it.copy(activeProvider = provider) }
    }

    fun selectCategoryFilter(category: FilingCategory?) {
        _uiState.update { it.copy(selectedCategoryFilter = category) }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun loadAiSummaryForFiling(filing: RegulatoryFiling) {
        viewModelScope.launch {
            val summary = disclosureRepository.getFilingAiSummary(filing.filingId)
            _uiState.update { it.copy(selectedFilingForSummary = filing, activeAiSummary = summary) }
        }
    }

    fun clearAiSummaryModal() {
        _uiState.update { it.copy(selectedFilingForSummary = null, activeAiSummary = null) }
    }

    private fun loadFilingsData() {
        viewModelScope.launch {
            launch {
                filingRepository.getLatestFilings().collect { filingsList ->
                    _uiState.update { it.copy(filings = filingsList, isLoading = false) }
                }
            }

            launch {
                timelineRepository.getCompanyTimeline("THYAO.IS").collect { timeline ->
                    _uiState.update { it.copy(companyTimeline = timeline) }
                }
            }
        }
    }
}
