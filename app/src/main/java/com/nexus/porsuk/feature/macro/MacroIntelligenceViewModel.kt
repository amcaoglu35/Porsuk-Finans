package com.nexus.porsuk.feature.macro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Porsuk Macro Intelligence Platform — ViewModel
 *
 * Küresel makroekonomik göstergeleri, merkez bankası politikalarını, tahvil getirilerini ve AI duruşunu yönetir.
 */
@HiltViewModel
class MacroIntelligenceViewModel @Inject constructor(
    private val macroRepository: MacroRepository,
    private val indicatorRepository: MacroIndicatorRepository,
    private val centralBankRepository: CentralBankRepository,
    private val bondRepository: BondRepository,
    private val fxRepository: FXRepository,
    private val commodityRepository: MacroCommodityRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MacroIntelligenceUiState())
    val uiState: StateFlow<MacroIntelligenceUiState> = _uiState.asStateFlow()

    init {
        loadMacroData()
    }

    fun selectTab(tab: MacroDashboardTab) {
        _uiState.update { it.copy(activeTab = tab) }
    }

    fun selectProvider(provider: MacroProviderType) {
        _uiState.update { it.copy(selectedProvider = provider) }
    }

    private fun loadMacroData() {
        viewModelScope.launch {
            launch {
                macroRepository.getMacroAiOutlook().collect { outlook ->
                    _uiState.update { it.copy(aiOutlook = outlook, isLoading = false) }
                }
            }

            launch {
                indicatorRepository.getEconomicIndicators().collect { list ->
                    _uiState.update { it.copy(indicators = list) }
                }
            }

            launch {
                centralBankRepository.getCentralBankPolicies().collect { policies ->
                    _uiState.update { it.copy(centralBankPolicies = policies) }
                }
            }

            launch {
                bondRepository.getGovernmentBondYields().collect { bonds ->
                    _uiState.update { it.copy(bondYields = bonds) }
                }
            }

            launch {
                fxRepository.getMajorFxCrosses().collect { fx ->
                    _uiState.update { it.copy(fxCrosses = fx) }
                }
            }

            launch {
                commodityRepository.getCommodityPrices().collect { items ->
                    _uiState.update { it.copy(commodities = items) }
                }
            }
        }
    }
}
