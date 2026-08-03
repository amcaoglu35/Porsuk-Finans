package com.nexus.porsuk.ui.performance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.data.repository.FinanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AiPerformanceUiState(
    val totalPortfolioValue: Double = 0.0,
    val totalReturnPct: Double = 0.0,
    val benchmarkReturnPct: Double = 0.0,
    val alphaPct: Double = 0.0,
    val isLoading: Boolean = false
)

@HiltViewModel
class AiPerformanceViewModel @Inject constructor(
    private val repository: FinanceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AiPerformanceUiState())
    val uiState: StateFlow<AiPerformanceUiState> = _uiState.asStateFlow()

    init {
        loadPerformance()
    }

    private fun loadPerformance() {
        viewModelScope.launch {
            repository.getPortfolioHistory().collect { history ->
                if (history.size >= 2) {
                    val initial = history.first().totalValue
                    val latest = history.last().totalValue
                    val returnPct = if (initial > 0) (latest - initial) / initial * 100.0 else 0.0
                    val benchmarkPct = 18.5 // BIST 100 benchmark reference
                    _uiState.value = AiPerformanceUiState(
                        totalPortfolioValue = latest,
                        totalReturnPct = returnPct,
                        benchmarkReturnPct = benchmarkPct,
                        alphaPct = returnPct - benchmarkPct,
                        isLoading = false
                    )
                }
            }
        }
    }
}
