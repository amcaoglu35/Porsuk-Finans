package com.nexus.porsuk.feature.ma

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Mergers, Acquisitions & Corporate Events Intelligence Platform — ViewModel
 */
@HiltViewModel
class CorporateEventViewModel @Inject constructor(
    private val corporateEventRepository: CorporateEventRepository,
    private val mergerRepository: MergerRepository,
    private val acquisitionRepository: AcquisitionRepository,
    private val dealAnalyticsRepository: DealAnalyticsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CorporateEventUiState())
    val uiState: StateFlow<CorporateEventUiState> = _uiState.asStateFlow()

    init {
        loadData(_uiState.value.selectedSymbol)
    }

    fun selectTab(tab: CorporateEventTab) {
        _uiState.update { it.copy(activeTab = tab) }
    }

    fun selectDeal(dealId: String) {
        _uiState.update { it.copy(selectedDealId = dealId, isLoading = true) }
        loadDealAnalytics(dealId)
    }

    fun filterByEventType(type: CorporateEventType?) {
        _uiState.update { it.copy(selectedEventTypeFilter = type) }
    }

    fun changeSymbol(symbol: String) {
        _uiState.update { it.copy(selectedSymbol = symbol, isLoading = true) }
        loadData(symbol)
    }

    private fun loadData(symbol: String) {
        viewModelScope.launch {
            launch {
                corporateEventRepository.getCorporateEvents(symbol).collect { events ->
                    _uiState.update { it.copy(corporateEvents = events, isLoading = false) }
                }
            }

            launch {
                mergerRepository.getActiveMergers().collect { mergers ->
                    _uiState.update { it.copy(mergers = mergers) }
                }
            }

            launch {
                acquisitionRepository.getActiveAcquisitions().collect { acquisitions ->
                    val defaultDealId = acquisitions.firstOrNull()?.dealId ?: "deal_a1"
                    _uiState.update { it.copy(acquisitions = acquisitions, selectedDealId = defaultDealId) }
                    loadDealAnalytics(defaultDealId)
                }
            }

            launch {
                dealAnalyticsRepository.getFutureStubs().collect { stubs ->
                    _uiState.update { it.copy(futureStubs = stubs) }
                }
            }
        }
    }

    private fun loadDealAnalytics(dealId: String) {
        viewModelScope.launch {
            launch {
                val impact = dealAnalyticsRepository.getDealImpactAnalysis(dealId)
                _uiState.update { it.copy(impactAnalysis = impact, isLoading = false) }
            }

            launch {
                val ai = dealAnalyticsRepository.getDealAiIntelligence(dealId)
                _uiState.update { it.copy(aiIntelligence = ai) }
            }

            launch {
                val vis = dealAnalyticsRepository.getDealVisuals(dealId)
                _uiState.update { it.copy(visuals = vis) }
            }
        }
    }
}
