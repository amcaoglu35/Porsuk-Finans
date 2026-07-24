package com.nexus.porsuk.feature.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.ScanHistoryRepository
import com.nexus.porsuk.domain.repository.ScannerFilterRepository
import com.nexus.porsuk.domain.repository.ScannerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Porsuk Smart Scanner Engine — ViewModel
 *
 * 11 Hazır tarama stratejisini, 10 piyasayı ve gelişmiş çoklu kriter filtreleri yönetir.
 */
@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val scannerRepository: ScannerRepository,
    private val filterRepository: ScannerFilterRepository,
    private val scanHistoryRepository: ScanHistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScannerUiState())
    val uiState: StateFlow<ScannerUiState> = _uiState.asStateFlow()

    init {
        runScan()
    }

    fun selectPreset(preset: ScanPresetCategory) {
        _uiState.update { it.copy(selectedPreset = preset, isLoading = true) }
        viewModelScope.launch {
            scanHistoryRepository.recordScanPreset(preset)
        }
        runScan()
    }

    fun selectMarket(market: ScanMarketType) {
        _uiState.update { it.copy(selectedMarket = market, isLoading = true) }
        runScan()
    }

    fun updateFilters(newCriteria: ScannerFilterCriteria) {
        _uiState.update { it.copy(filterCriteria = newCriteria, isLoading = true) }
        viewModelScope.launch {
            filterRepository.saveFilterCriteria(newCriteria)
        }
        runScan()
    }

    private fun runScan() {
        val state = _uiState.value
        viewModelScope.launch {
            scannerRepository.executeScan(state.selectedPreset, state.selectedMarket, state.filterCriteria).collect { results ->
                _uiState.update { it.copy(scanResults = results, isLoading = false) }
            }
        }
    }
}
