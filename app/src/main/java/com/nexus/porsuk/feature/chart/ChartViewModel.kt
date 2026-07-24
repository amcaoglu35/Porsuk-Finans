package com.nexus.porsuk.feature.chart

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.ChartRepository
import com.nexus.porsuk.domain.repository.DrawingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Porsuk Professional Chart Center — ViewModel
 *
 * 10 Zaman diliminde 7 farklı grafik türünü, çizim araçlarını ve portföy alış/satış/temettü katmanını yönetir.
 */
@HiltViewModel
class ChartViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val chartRepository: ChartRepository,
    private val drawingRepository: DrawingRepository
) : ViewModel() {

    private val symbol: String = savedStateHandle["symbol"] ?: "THYAO.IS"

    private val _uiState = MutableStateFlow(ChartUiState(symbol = symbol))
    val uiState: StateFlow<ChartUiState> = _uiState.asStateFlow()

    init {
        loadCandles(symbol, _uiState.value.selectedTimeFrame, _uiState.value.selectedChartType)
        loadPortfolioMarkers(symbol)
        loadSavedDrawings(symbol)
    }

    fun selectTimeFrame(timeFrame: ChartTimeFrame) {
        _uiState.update { it.copy(selectedTimeFrame = timeFrame, isLoading = true) }
        loadCandles(_uiState.value.symbol, timeFrame, _uiState.value.selectedChartType)
    }

    fun selectChartType(chartType: ChartType) {
        _uiState.update { it.copy(selectedChartType = chartType, isLoading = true) }
        loadCandles(_uiState.value.symbol, _uiState.value.selectedTimeFrame, chartType)
    }

    fun selectTool(tool: DrawingToolType) {
        _uiState.update { it.copy(selectedTool = tool) }
    }

    fun togglePortfolioOverlay() {
        _uiState.update { it.copy(showPortfolioOverlay = !it.showPortfolioOverlay) }
    }

    private fun loadCandles(targetSymbol: String, tf: ChartTimeFrame, type: ChartType) {
        viewModelScope.launch {
            chartRepository.getCandles(targetSymbol, tf, type).collect { list ->
                _uiState.update { it.copy(candles = list, isLoading = false) }
            }
        }
    }

    private fun loadPortfolioMarkers(targetSymbol: String) {
        viewModelScope.launch {
            chartRepository.getPortfolioOverlayMarkers(targetSymbol).collect { markers ->
                _uiState.update { it.copy(portfolioMarkers = markers) }
            }
        }
    }

    private fun loadSavedDrawings(targetSymbol: String) {
        viewModelScope.launch {
            drawingRepository.getSavedDrawings(targetSymbol).collect { list ->
                _uiState.update { it.copy(drawings = list) }
            }
        }
    }
}
