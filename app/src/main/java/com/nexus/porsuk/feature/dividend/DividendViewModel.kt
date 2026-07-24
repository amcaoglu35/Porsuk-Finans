package com.nexus.porsuk.feature.dividend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.domain.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Porsuk Dividend Intelligence Center — ViewModel
 *
 * Temettü takvimini, 5 temettü kalite skorunu ve pasif gelir tahminlerini yönetir.
 */
@HiltViewModel
class DividendViewModel @Inject constructor(
    private val dividendRepository: DividendRepository,
    private val calendarRepository: DividendCalendarRepository,
    private val portfolioRepository: DividendPortfolioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DividendUiState())
    val uiState: StateFlow<DividendUiState> = _uiState.asStateFlow()

    init {
        loadDividendData()
    }

    private fun loadDividendData() {
        viewModelScope.launch {
            launch {
                dividendRepository.getDividendStocks().collect { list ->
                    _uiState.update { it.copy(stocks = list, isLoading = false) }
                }
            }

            launch {
                calendarRepository.getAllDividendEvents().collect { upcoming ->
                    val items = upcoming.map { ev ->
                        DividendStockItem(
                            symbol = ev.symbol,
                            name = ev.companyName,
                            dividendYieldPct = 5.2,
                            payoutRatioPct = 45.0,
                            exDividendDateText = ev.exDate.toString(),
                            score = DividendScore(82, 85, 80, 78, 88, 83)
                        )
                    }
                    _uiState.update { it.copy(upcomingDividends = items) }
                }
            }

            launch {
                portfolioRepository.getIncomeProjection().collect { proj ->
                    _uiState.update { it.copy(projection = proj) }
                }
            }
        }
    }
}
