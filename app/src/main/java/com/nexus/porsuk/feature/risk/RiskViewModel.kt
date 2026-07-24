package com.nexus.porsuk.feature.risk

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.domain.repository.RiskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Porsuk Risk Engine — ViewModel
 *
 * 6 Risk kategorisinin (Market, Liquidity, Financial, Business, Price, Portfolio) analiz raporunu yönetir.
 */
@HiltViewModel
class RiskViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val riskRepository: RiskRepository
) : ViewModel() {

    private val symbol: String = savedStateHandle["symbol"] ?: "THYAO.IS"

    private val _uiState = MutableStateFlow(RiskUiState(symbol = symbol))
    val uiState: StateFlow<RiskUiState> = _uiState.asStateFlow()

    init {
        loadRiskReport(symbol)
    }

    fun analyzeSymbol(newSymbol: String) {
        _uiState.update { it.copy(symbol = newSymbol, isLoading = true) }
        loadRiskReport(newSymbol)
    }

    private fun loadRiskReport(targetSymbol: String) {
        viewModelScope.launch {
            riskRepository.getRiskReport(targetSymbol).collect { rep ->
                _uiState.update { it.copy(report = rep, isLoading = false) }
            }
        }
    }
}
