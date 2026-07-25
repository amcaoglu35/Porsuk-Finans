package com.nexus.porsuk.feature.institutional

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Institutional Holdings & Insider Intelligence Platform — ViewModel
 */
@HiltViewModel
class InstitutionalViewModel @Inject constructor(
    private val institutionRepository: InstitutionRepository,
    private val insiderRepository: InsiderRepository,
    private val ownershipRepository: OwnershipRepository,
    private val fundFlowRepository: FundFlowRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(InstitutionalUiState())
    val uiState: StateFlow<InstitutionalUiState> = _uiState.asStateFlow()

    init {
        loadInstitutionalData(_uiState.value.selectedSymbol)
    }

    fun selectTab(tab: InstitutionalTab) {
        _uiState.update { it.copy(activeTab = tab) }
    }

    fun selectProvider(provider: InstitutionalProviderType) {
        _uiState.update { it.copy(selectedProvider = provider) }
    }

    fun filterByInsiderRole(role: InsiderRoleType?) {
        _uiState.update { it.copy(selectedRoleFilter = role) }
    }

    fun changeSymbol(symbol: String) {
        _uiState.update { it.copy(selectedSymbol = symbol, isLoading = true) }
        loadInstitutionalData(symbol)
    }

    private fun loadInstitutionalData(symbol: String) {
        viewModelScope.launch {
            launch {
                institutionRepository.getTopInstitutionalInvestors().collect { investors ->
                    _uiState.update { it.copy(topInvestors = investors, isLoading = false) }
                }
            }

            launch {
                institutionRepository.getFundHoldings(symbol).collect { holdings ->
                    _uiState.update { it.copy(fundHoldings = holdings) }
                }
            }

            launch {
                val buyers = institutionRepository.getTopBuyers(symbol)
                val sellers = institutionRepository.getTopSellers(symbol)
                _uiState.update { it.copy(topBuyers = buyers, topSellers = sellers) }
            }

            launch {
                insiderRepository.getRecentInsiderTrades(symbol).collect { trades ->
                    _uiState.update { it.copy(insiderTrades = trades) }
                }
            }

            launch {
                val netAct = insiderRepository.getNetInsiderActivity(symbol)
                _uiState.update { it.copy(netInsiderActivity = netAct) }
            }

            launch {
                ownershipRepository.getOwnershipBreakdown(symbol).collect { breakdown ->
                    _uiState.update { it.copy(ownershipBreakdown = breakdown) }
                }
            }

            launch {
                ownershipRepository.getOwnershipHistory(symbol).collect { hist ->
                    _uiState.update { it.copy(ownershipHistory = hist) }
                }
            }

            launch {
                fundFlowRepository.getWhaleAlerts().collect { alerts ->
                    _uiState.update { it.copy(whaleAlerts = alerts) }
                }
            }

            launch {
                fundFlowRepository.getSmartMoneyFlow(symbol).collect { flow ->
                    _uiState.update { it.copy(smartMoneyFlow = flow) }
                }
            }

            launch {
                val commentary = fundFlowRepository.getSmartMoneyAiCommentary(symbol)
                _uiState.update { it.copy(aiCommentary = commentary) }
            }

            launch {
                fundFlowRepository.getFutureStubs().collect { stubs ->
                    _uiState.update { it.copy(futureStubs = stubs) }
                }
            }
        }
    }
}
