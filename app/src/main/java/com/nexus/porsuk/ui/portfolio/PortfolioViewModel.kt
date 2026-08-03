package com.nexus.porsuk.ui.portfolio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.data.local.SettingsManager
import com.nexus.porsuk.data.local.entity.*
import com.nexus.porsuk.data.remote.GeminiService
import com.nexus.porsuk.data.remote.PortfolioDoctorEngine
import com.nexus.porsuk.data.remote.PortfolioDoctorMetrics
import com.nexus.porsuk.data.remote.ScrapeResult
import com.nexus.porsuk.data.repository.FinanceRepository
import com.nexus.porsuk.domain.model.PortfolioAsset
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@HiltViewModel
class PortfolioViewModel @Inject constructor(
    private val repository: FinanceRepository,
    private val settingsManager: SettingsManager
) : ViewModel() {

    private val basketItemsFlow = repository.allBaskets.flatMapLatest { baskets ->
        val flows = baskets.map { basket ->
            repository.getBasketItems(basket.id).map { items ->
                basket to items
            }
        }
        if (flows.isEmpty()) flowOf(emptyList())
        else combine(flows) { it.toList() }
    }

    val totalBalanceTry: StateFlow<Double> = combine(
        basketItemsFlow,
        repository.prices,
        repository.allCompanies,
        repository.exchangeRates
    ) { basketWithItems, pricesMap, companies, rates ->
        val companyMap = companies.associateBy { it.symbol }
        val usdRate = rates["USD"] ?: 34.5
        val eurRate = rates["EUR"] ?: 37.2
        val gbpRate = eurRate * 1.165
        basketWithItems.sumOf { (basket, items) ->
            val rate = when (basket.market.uppercase()) {
                "NASDAQ", "NYSE" -> usdRate
                "FRA", "EURONEXT", "ETR", "EPA", "AMS", "BME" -> eurRate
                "LSE" -> gbpRate
                "SWX" -> eurRate * 1.06
                else -> 1.0
            }
            items.sumOf { item ->
                val currentPrice = pricesMap[item.symbol]?.price
                    ?: companyMap[item.symbol]?.currentPrice?.takeIf { it > 0.0 }
                    ?: item.buyPrice
                item.quantity * currentPrice * rate
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

    val totalChangePercent: StateFlow<Double> = combine(
        basketItemsFlow,
        repository.prices,
        repository.allCompanies,
        repository.exchangeRates
    ) { basketWithItems, pricesMap, companies, rates ->
        val companyMap = companies.associateBy { it.symbol }
        val usdRate = rates["USD"] ?: 34.5
        val eurRate = rates["EUR"] ?: 37.2
        val gbpRate = eurRate * 1.165
        var totalValueTry = 0.0
        var totalCostTry = 0.0
        basketWithItems.forEach { (basket, items) ->
            val rate = when (basket.market.uppercase()) {
                "NASDAQ", "NYSE" -> usdRate
                "FRA", "EURONEXT", "ETR", "EPA", "AMS", "BME" -> eurRate
                "LSE" -> gbpRate
                "SWX" -> eurRate * 1.06
                else -> 1.0
            }
            items.forEach { item ->
                val currentPrice = pricesMap[item.symbol]?.price
                    ?: companyMap[item.symbol]?.currentPrice?.takeIf { it > 0.0 }
                    ?: item.buyPrice
                val itemValueTry = item.quantity * currentPrice * rate
                val itemCostTry = item.quantity * item.buyPrice * rate
                totalValueTry += itemValueTry
                totalCostTry += itemCostTry
            }
        }
        if (totalCostTry > 0) (totalValueTry - totalCostTry) / totalCostTry * 100 else 0.0
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

    val numberFormat: StateFlow<String> = settingsManager.numberFormat
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "TR")

    val consolidatedHoldings: StateFlow<List<PortfolioAsset>> = repository.getConsolidatedAssetsFlow()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _selectedChartTimeframe = MutableStateFlow(0)
    val selectedChartTimeframe: StateFlow<Int> = _selectedChartTimeframe.asStateFlow()

    private val _portfolioChartData = MutableStateFlow<List<Double>>(emptyList())
    val portfolioChartData: StateFlow<List<Double>> = _portfolioChartData.asStateFlow()

    val portfolioRiskMetrics: StateFlow<PortfolioDoctorMetrics?> = combine(
        repository.allBasketItems,
        repository.allCompanies
    ) { items, companies ->
        if (items.isEmpty()) null
        else PortfolioDoctorEngine.analyze(items, companies)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _aiPortfolioInsight = MutableStateFlow<String?>(null)
    val aiPortfolioInsight: StateFlow<String?> = _aiPortfolioInsight.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    fun updateChartTimeframe(index: Int) {
        _selectedChartTimeframe.value = index
        viewModelScope.launch {
            val range = when (index) {
                0 -> "1d"
                1 -> "5d"
                2 -> "1mo"
                3 -> "3mo"
                4 -> "6mo"
                5 -> "1y"
                else -> "max"
            }
            val data = repository.fetchConsolidatedPerformance(range)
            _portfolioChartData.value = data
        }
    }

    fun generateAiPortfolioInsight() {
        val assets = consolidatedHoldings.value
        val metrics = portfolioRiskMetrics.value
        if (assets.isNotEmpty() && metrics != null) {
            viewModelScope.launch {
                val apiKey = settingsManager.getGeminiApiKey()
                if (!apiKey.isNullOrBlank()) {
                    val service = GeminiService(apiKey)
                    val insight = service.generatePortfolioAiInsight(assets, metrics)
                    _aiPortfolioInsight.value = insight
                }
            }
        }
    }

    fun refreshAllData() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                val watchlistSymbols = repository.watchlist.first().map { it.symbol }
                val popularSymbols = listOf("THYAO", "EREGL", "TUPRS", "ASELS", "KCHOL", "GARAN", "AKBNK", "BIMAS", "SISE", "PGSUS")
                val basketItems = repository.allBasketItems.first()
                val basketSymbols = basketItems.map { it.symbol }.distinct()
                val allToRefresh = (watchlistSymbols + popularSymbols + basketSymbols).distinct()

                val jobs = allToRefresh.map { symbol ->
                    launch {
                        val company = repository.getCompany(symbol)
                        val market = company?.market ?: "IST"
                        val result = repository.refreshPrice(symbol, market)
                        if (result is ScrapeResult.Success) {
                            repository.prices.update { it + (symbol to result.data) }
                            repository.getCompany(symbol)?.let { comp ->
                                repository.insertCompanies(listOf(comp.copy(
                                    currentPrice = result.data.price,
                                    changePercent = result.data.changePercent,
                                    lastUpdated = System.currentTimeMillis()
                                )))
                            }
                        }
                    }
                }
                jobs.forEach { it.join() }
            } catch (e: Exception) {
                // Ignore network errors during background refresh
            } finally {
                _isRefreshing.value = false
            }
        }
    }
}
