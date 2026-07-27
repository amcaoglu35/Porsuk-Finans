package com.nexus.porsuk.ui.chart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.data.repository.FinanceRepository
import com.nexus.porsuk.data.remote.ChartAiEngine
import com.nexus.porsuk.domain.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChartUiState(
    val symbol: String = "",
    val compareSymbols: List<String> = emptyList(),
    val candles: List<CandleStickItem> = emptyList(),
    val compareCandles: Map<String, List<CandleStickItem>> = emptyMap(),
    val indicators: Map<IndicatorType, List<Double>> = emptyMap(),
    val settings: ChartSettings = ChartSettings(),
    val aiAnalysis: AiChartAnalysis? = null,
    val isLoading: Boolean = false,
    val isAiLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class ChartViewModel @Inject constructor(
    private val repository: FinanceRepository,
    private val aiEngine: ChartAiEngine
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChartUiState())
    val uiState: StateFlow<ChartUiState> = _uiState.asStateFlow()

    fun loadData(symbol: String, timeFrame: ChartTimeFrame) {
        viewModelScope.launch {
            _uiState.update { it.copy(symbol = symbol, isLoading = true, errorMessage = null) }
            fetchCandles(symbol)
        }
    }

    fun addComparison(compareSymbol: String) {
        viewModelScope.launch {
            val currentCompare = _uiState.value.compareSymbols.toMutableList()
            if (!currentCompare.contains(compareSymbol)) {
                currentCompare.add(compareSymbol)
                _uiState.update { it.copy(compareSymbols = currentCompare) }
                fetchCompareCandles(compareSymbol)
            }
        }
    }

    private suspend fun fetchCandles(symbol: String) {
        try {
            val result = repository.fetchHistoricalPrices(symbol, "BIST", "2024-01-01", "2024-07-11")
            if (result is com.nexus.porsuk.data.remote.ScrapeResult.Success) {
                val prices = result.data
                val candles = mapPricesToCandles(prices)
                
                // Calculate basic indicators
                val ema20 = calculateEma(prices, 20)
                val indicatorMap = mapOf(IndicatorType.EMA to ema20)
                
                _uiState.update { it.copy(candles = candles, indicators = indicatorMap, isLoading = false) }
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(isLoading = false, errorMessage = e.localizedMessage) }
        }
    }

    private fun calculateEma(prices: List<Double>, period: Int): List<Double> {
        val k = 2.0 / (period + 1)
        val series = mutableListOf<Double>()
        if (prices.isEmpty()) return series
        
        var ema = prices.first()
        series.add(ema)
        for (i in 1 until prices.size) {
            ema = prices[i] * k + ema * (1 - k)
            series.add(ema)
        }
        return series
    }

    private suspend fun fetchCompareCandles(symbol: String) {
        try {
            val result = repository.fetchHistoricalPrices(symbol, "BIST", "2024-01-01", "2024-07-11")
            if (result is com.nexus.porsuk.data.remote.ScrapeResult.Success) {
                val prices = result.data
                val candles = mapPricesToCandles(prices)
                val currentMap = _uiState.value.compareCandles.toMutableMap()
                currentMap[symbol] = candles
                _uiState.update { it.copy(compareCandles = currentMap) }
            }
        } catch (e: Exception) {
            // Log error
        }
    }

    private fun mapPricesToCandles(prices: List<Double>): List<CandleStickItem> {
        return prices.mapIndexed { index, price ->
            CandleStickItem(
                timestamp = System.currentTimeMillis() - ((prices.size - index) * 86400000L),
                open = price * 0.99,
                high = price * 1.01,
                low = price * 0.98,
                close = price,
                volume = 1000000.0
            )
        }
    }

    fun changeChartType(type: ChartType) {
        _uiState.update { it.copy(settings = it.settings.copy(chartType = type)) }
    }

    fun toggleAiAnalysis() {
        val state = _uiState.value
        if (state.candles.isEmpty()) return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isAiLoading = true) }
            val analysis = aiEngine.analyzeChartData(state.symbol, state.candles)
            _uiState.update { it.copy(aiAnalysis = analysis, isAiLoading = false) }
        }
    }
}
