package com.nexus.porsuk.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.data.local.SettingsManager
import com.nexus.porsuk.data.local.entity.Company
import com.nexus.porsuk.data.local.entity.PriceAlert
import com.nexus.porsuk.data.local.entity.PriceSnapshot
import com.nexus.porsuk.data.local.entity.WatchlistItem
import com.nexus.porsuk.data.repository.FinanceRepository
import com.nexus.porsuk.domain.usecase.portfolio.GetPortfolioSummaryUseCase
import com.nexus.porsuk.domain.usecase.portfolio.PortfolioSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: FinanceRepository,
    private val settingsManager: SettingsManager,
    private val getPortfolioSummaryUseCase: GetPortfolioSummaryUseCase
) : ViewModel() {

    val watchlist: Flow<List<WatchlistItem>> = repository.watchlist
    val prices: StateFlow<Map<String, PriceSnapshot>> = repository.prices.asStateFlow()
    val allCompanies: Flow<List<Company>> = repository.allCompanies
    val numberFormat: StateFlow<String> = settingsManager.numberFormat
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "TR")
    val allPriceAlerts: Flow<List<PriceAlert>> = repository.getAllPriceAlertsFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    val portfolioSummary: StateFlow<PortfolioSummary> = getPortfolioSummaryUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PortfolioSummary()
        )

    val totalBalanceTry: StateFlow<Double> = portfolioSummary
        .map { it.totalBalance }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalChangePercent: StateFlow<Double> = portfolioSummary
        .map { it.totalChangePercent }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun refreshAllData() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                repository.refreshExchangeRates()
            } catch (_: Exception) {
            } finally {
                _isRefreshing.value = false
            }
        }
    }
}
