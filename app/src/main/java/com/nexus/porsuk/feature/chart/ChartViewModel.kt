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

    private val initialSymbol: String = savedStateHandle["symbol"] ?: "THYAO.IS"

    private val _uiState = MutableStateFlow(ChartUiState(symbol = initialSymbol))
    val uiState: StateFlow<ChartUiState> = _uiState.asStateFlow()

    init {
        loadData(initialSymbol, _uiState.value.selectedTimeFrame)
        loadPortfolioMarkers(initialSymbol)
        loadSavedDrawings(initialSymbol)
    }

    fun loadData(targetSymbol: String, tf: ChartTimeFrame) {
        _uiState.update { it.copy(symbol = targetSymbol, selectedTimeFrame = tf, isLoading = true) }
        viewModelScope.launch {
            chartRepository.getCandles(targetSymbol, tf, _uiState.value.selectedChartType).collect { list ->
                _uiState.update { it.copy(candles = list, isLoading = false) }
            }
        }
    }

    fun selectTimeFrame(timeFrame: ChartTimeFrame) {
        loadData(_uiState.value.symbol, timeFrame)
    }

    fun selectChartType(chartType: ChartType) {
        _uiState.update { it.copy(selectedChartType = chartType, isLoading = true) }
        loadCandles(_uiState.value.symbol, _uiState.value.selectedTimeFrame, chartType)
    }

    fun changeChartType(type: ChartType) {
        selectChartType(type)
    }

    fun selectTool(tool: DrawingToolType) {
        _uiState.update { it.copy(selectedTool = tool) }
    }

    fun toggleAiAnalysis() {
        if (_uiState.value.aiAnalysis != null) {
            _uiState.update { it.copy(aiAnalysis = null) }
            return
        }
        
        _uiState.update { it.copy(isAiLoading = true) }
        // Actual AI implementation would go here. For now, dummy logic to fix compilation.
        viewModelScope.launch {
             // Mock delay
             kotlinx.coroutines.delay(1000)
             _uiState.update { it.copy(
                 isAiLoading = false,
                 aiAnalysis = AiChartAnalysis(
                     trend = "Yükseliş",
                     pattern = "Bayrak",
                     supportLevels = listOf(300.0, 310.0),
                     resistanceLevels = listOf(330.0, 340.0),
                     riskScore = 40,
                     confidence = 85,
                     scenario = "Fiyatın 320 seviyesini kırması durumunda 350 hedefi radarda.",
                     signal = TechnicalSignalType.BUY
                 )
             )}
        }
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
