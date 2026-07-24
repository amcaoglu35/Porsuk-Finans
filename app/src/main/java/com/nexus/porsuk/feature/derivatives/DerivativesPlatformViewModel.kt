package com.nexus.porsuk.feature.derivatives

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Porsuk Options, Futures & Derivatives Platform — ViewModel
 *
 * Opsiyon zinciri, Black-Scholes Yunan hesaplamaları, opsiyon strateji riski ve VİOP vadeli işlemlerini yönetir.
 */
@HiltViewModel
class DerivativesPlatformViewModel @Inject constructor(
    private val derivativesRepository: DerivativesRepository,
    private val optionsRepository: OptionsRepository,
    private val futuresRepository: FuturesRepository,
    private val greeksRepository: GreeksRepository,
    private val strategyRepository: OptionStrategyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DerivativesPlatformUiState())
    val uiState: StateFlow<DerivativesPlatformUiState> = _uiState.asStateFlow()

    init {
        loadDerivativesData()
    }

    fun selectProvider(provider: DerivativesProviderType) {
        _uiState.update { it.copy(activeProvider = provider) }
    }

    fun selectOptionTypeFilter(type: OptionType?) {
        _uiState.update { it.copy(selectedOptionTypeFilter = type) }
    }

    fun selectStrategy(strategy: OptionStrategyType) {
        val risk = strategyRepository.evaluateStrategyRisk(strategy, 350.0)
        _uiState.update { it.copy(selectedStrategy = strategy, strategyRisk = risk) }
    }

    fun inspectContractGreeks(contract: OptionContract) {
        viewModelScope.launch {
            val greeks = greeksRepository.calculateGreeks(contract, 355.0)
            _uiState.update { it.copy(selectedContractGreeks = greeks) }
        }
    }

    private fun loadDerivativesData() {
        viewModelScope.launch {
            launch {
                optionsRepository.getOptionChain("THYAO.IS").collect { chain ->
                    _uiState.update { it.copy(optionChain = chain, isLoading = false) }
                }
            }

            launch {
                futuresRepository.getFuturesContracts("THYAO.IS").collect { futures ->
                    _uiState.update { it.copy(futuresContracts = futures) }
                }
            }
        }
    }
}
