package com.nexus.porsuk.feature.backtest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.domain.model.BacktestConfig
import com.nexus.porsuk.domain.model.BacktestMode
import com.nexus.porsuk.domain.repository.BacktestRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Porsuk Backtesting Engine — ViewModel
 *
 * Simülasyon parametrelerini, olay-güdümlü backtest yürütmesini ve geçmiş rapor kayıtlarını yönetir.
 */
@HiltViewModel
class BacktestViewModel @Inject constructor(
    private val backtestRepository: BacktestRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BacktestUiState())
    val uiState: StateFlow<BacktestUiState> = _uiState.asStateFlow()

    init {
        runSimulation()
        loadSavedReports()
    }

    fun selectMode(mode: BacktestMode) {
        val updatedConfig = _uiState.value.config.copy(mode = mode)
        _uiState.update { it.copy(config = updatedConfig, isLoading = true) }
        runSimulation()
    }

    fun updateCapital(capital: Double) {
        val updatedConfig = _uiState.value.config.copy(initialCapitalUsd = capital)
        _uiState.update { it.copy(config = updatedConfig, isLoading = true) }
        runSimulation()
    }

    private fun runSimulation() {
        viewModelScope.launch {
            backtestRepository.runBacktest(_uiState.value.config).collect { rep ->
                _uiState.update { it.copy(report = rep, isLoading = false) }
            }
        }
    }

    private fun loadSavedReports() {
        viewModelScope.launch {
            backtestRepository.getSavedReports().collect { list ->
                _uiState.update { it.copy(savedReports = list) }
            }
        }
    }
}
