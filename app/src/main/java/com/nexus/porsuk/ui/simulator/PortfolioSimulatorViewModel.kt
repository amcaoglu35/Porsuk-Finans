package com.nexus.porsuk.ui.simulator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.data.repository.FinanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SimulatorUiState(
    val totalBalance: Double = 0.0,
    val monthlyContribution: Double = 5000.0,
    val projectedYears: Int = 5,
    val estimatedAnnualReturnPct: Double = 25.0,
    val projectedFutureValue: Double = 0.0,
    val isLoading: Boolean = false
)

@HiltViewModel
class PortfolioSimulatorViewModel @Inject constructor(
    private val repository: FinanceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SimulatorUiState())
    val uiState: StateFlow<SimulatorUiState> = _uiState.asStateFlow()

    init {
        calculateProjection()
    }

    fun updateContribution(contribution: Double) {
        _uiState.update { it.copy(monthlyContribution = contribution) }
        calculateProjection()
    }

    fun updateYears(years: Int) {
        _uiState.update { it.copy(projectedYears = years) }
        calculateProjection()
    }

    fun updateReturnPct(returnPct: Double) {
        _uiState.update { it.copy(estimatedAnnualReturnPct = returnPct) }
        calculateProjection()
    }

    private fun calculateProjection() {
        val state = _uiState.value
        val months = state.projectedYears * 12
        val monthlyRate = (state.estimatedAnnualReturnPct / 100.0) / 12.0
        var futureVal = state.totalBalance

        for (i in 1..months) {
            futureVal = (futureVal + state.monthlyContribution) * (1.0 + monthlyRate)
        }

        _uiState.update { it.copy(projectedFutureValue = futureVal) }
    }
}
