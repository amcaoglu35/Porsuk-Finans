package com.nexus.porsuk.feature.technical

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.domain.model.IndicatorCategory
import com.nexus.porsuk.domain.model.TimeFrame
import com.nexus.porsuk.domain.repository.TechnicalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Porsuk Technical Engine — ViewModel
 *
 * 9 Zaman diliminde 5 farklı indikatör grubu sonuçlarını, destek/direnç seviyelerini ve 5 seviyeli sinyal özetini yönetir.
 */
@HiltViewModel
class TechnicalViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val technicalRepository: TechnicalRepository
) : ViewModel() {

    private val symbol: String = savedStateHandle["symbol"] ?: "THYAO.IS"

    private val _uiState = MutableStateFlow(TechnicalUiState(symbol = symbol))
    val uiState: StateFlow<TechnicalUiState> = _uiState.asStateFlow()

    init {
        loadTechnicalReport(symbol, _uiState.value.selectedTimeFrame)
    }

    fun selectTimeFrame(timeFrame: TimeFrame) {
        _uiState.update { it.copy(selectedTimeFrame = timeFrame, isLoading = true) }
        loadTechnicalReport(_uiState.value.symbol, timeFrame)
    }

    fun selectCategory(category: IndicatorCategory) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    private fun loadTechnicalReport(targetSymbol: String, timeFrame: TimeFrame) {
        viewModelScope.launch {
            technicalRepository.getTechnicalReport(targetSymbol, timeFrame).collect { rep ->
                _uiState.update { it.copy(report = rep, isLoading = false) }
            }
        }
    }
}
