package com.nexus.porsuk.feature.screener

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Porsuk Screener Pro Ultimate — ViewModel
 *
 * Specification Pattern tabanlı çoklu kriter filtrelemeyi, 10 akıllı paketi ve Room veritabanı kayıtlarını yönetir.
 */
@HiltViewModel
class ScreenerUltimateViewModel @Inject constructor(
    private val screenerRepository: ScreenerRepository,
    private val presetRepository: PresetRepository,
    private val filterRepository: FilterRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScreenerUltimateUiState())
    val uiState: StateFlow<ScreenerUltimateUiState> = _uiState.asStateFlow()

    init {
        loadPresetBundles()
        runUltimateScan()
    }

    fun selectPreset(preset: SmartFilterPresetCategory) {
        _uiState.update {
            it.copy(
                selectedPreset = preset,
                filterCriteria = it.filterCriteria.copy(presetCategory = preset),
                isLoading = true
            )
        }
        runUltimateScan()
    }

    fun applyCustomCriteria(criteria: ScreenerUltimateCriteria) {
        _uiState.update { it.copy(filterCriteria = criteria, isLoading = true) }
        runUltimateScan()
    }

    private fun loadPresetBundles() {
        viewModelScope.launch {
            presetRepository.getPresetBundles().collect { bundles ->
                _uiState.update { it.copy(presetsList = bundles) }
            }
        }
    }

    private fun runUltimateScan() {
        val criteria = _uiState.value.filterCriteria
        viewModelScope.launch {
            screenerRepository.executeScan(criteria).collect { res ->
                _uiState.update { it.copy(results = res, isLoading = false) }
            }
        }
    }
}
