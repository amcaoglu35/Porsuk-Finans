package com.nexus.porsuk.ui.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.data.local.SettingsManager
import com.nexus.porsuk.data.local.entity.*
import com.nexus.porsuk.data.remote.ScrapeResult
import com.nexus.porsuk.data.remote.YahooFinancePublicService
import com.nexus.porsuk.data.repository.FinanceRepository
import com.nexus.porsuk.ui.fund.Region
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class PortfolioRange(val label: String) {
    WEEK("1H"), MONTH("1A"), THREE_MONTHS("3A"), YEAR("1Y"), ALL("Tümü")
}

data class RegionSummary(
    val label: String,
    val flag: String,
    val market: String,
    val totalValue: Double,
    val allocationPercent: Float,
    val isSector: Boolean = false
)

data class MoverUiModel(
    val symbol: String,
    val currentPrice: Double,
    val changePercent: Double,
    val market: String
)

data class BasketPerformanceUiModel(
    val id: Int,
    val name: String,
    val market: String,
    val totalValue: Double,
    val changePercent: Double
)

data class DividendEntry(
    val symbol: String,
    val date: Long,
    val yield: Double?,
    val quantity: Double = 0.0,
    val projectedPayout: Double = 0.0
)

data class RiskMetrics(
    val sharpeRatio: Double = 0.0,
    val maxDrawdown: Double = 0.0,
    val volatility: Double = 0.0,
    val hasSufficientData: Boolean = true
)

data class AnalysisUiState(
    val totalPortfolioValue: Double = 0.0,
    val totalChangePercent: Double = 0.0,
    val selectedRange: PortfolioRange = PortfolioRange.THREE_MONTHS,
    val regionBreakdown: List<RegionSummary> = emptyList(),
    val bestPerformer: MoverUiModel? = null,
    val worstPerformer: MoverUiModel? = null,
    val basketPerformances: List<BasketPerformanceUiModel> = emptyList(),
    val aiSummary: String? = null,
    val isAiLoading: Boolean = false,
    val hasGeminiKey: Boolean = false,
    val basketCount: Int = 0,
    val isLoading: Boolean = true,
    val portfolioHistory: List<Float> = emptyList(),
    val riskMetrics: RiskMetrics = RiskMetrics(),
    val benchmarkChangePercent: Double = 0.0,
    val benchmarkLabel: String = "BIST-100",
    val targetAllocation: Map<String, Float> = emptyMap(),
    val dividendCalendar: List<DividendEntry> = emptyList(),
    val realizedPnL: Double = 0.0,
    val unrealizedPnL: Double = 0.0,
    val vixValue: Double? = null
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AnalysisViewModel @Inject constructor(
    private val repository: FinanceRepository,
    private val settingsManager: SettingsManager
) : ViewModel() {

    private val _selectedRange = MutableStateFlow(PortfolioRange.THREE_MONTHS)
    val allCompanies = repository.allCompanies
    
    private val _aiSummary = MutableStateFlow<String?>(null)
    private val _isAiLoading = MutableStateFlow(false)

    private val _aiRebalance = MutableStateFlow<String?>(null)
    val aiRebalance: StateFlow<String?> = _aiRebalance

    private val _isRebalanceLoading = MutableStateFlow(false)
    val isRebalanceLoading: StateFlow<Boolean> = _isRebalanceLoading

    private val _portfolioHealthCheckResult = MutableStateFlow<String>("")
    val portfolioHealthCheckResult: StateFlow<String> = _portfolioHealthCheckResult

    private val _isHealthChecking = MutableStateFlow(false)
    val isHealthChecking: StateFlow<Boolean> = _isHealthChecking

    private val _screenerResult = MutableStateFlow<String?>(null)
    val screenerResult: StateFlow<String?> = _screenerResult

    private val _isScreenerLoading = MutableStateFlow(false)
    val isScreenerLoading: StateFlow<Boolean> = _isScreenerLoading

    private val _aiRecommendations = MutableStateFlow<String?>(null)
    val aiRecommendations: StateFlow<String?> = _aiRecommendations

    private val _isRecsLoading = MutableStateFlow(false)
    val isRecsLoading: StateFlow<Boolean> = _isRecsLoading

    private val _benchmarkChangePct = MutableStateFlow(0.0)
    private val _vixValue = MutableStateFlow<Double?>(null)
    private val _targetAllocation = MutableStateFlow<Map<String, Float>>(emptyMap())
    val targetAllocation: StateFlow<Map<String, Float>> = _targetAllocation

    val numberFormat = settingsManager.numberFormat.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "TR")

    private val yahooPublicService = YahooFinancePublicService()

    init {
        viewModelScope.launch {
            settingsManager.targetAllocationJson.collect { json ->
                if (json.isNotBlank()) {
                    try {
                        val map = mutableMapOf<String, Float>()
                        val obj = org.json.JSONObject(json)
                        obj.keys().forEach { key -> map[key] = obj.getDouble(key).toFloat() }
                        _targetAllocation.value = map
                    } catch (_: Exception) {}
                }
            }
        }
        fetchBenchmark()
        fetchVix()
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    private val combinedDataFlow: Flow<List<Pair<Basket, List<BasketItem>>>> = repository.allBaskets.flatMapLatest { baskets ->
        if (baskets.isEmpty()) flowOf(emptyList())
        else {
            val flows = baskets.map { basket ->
                repository.getBasketItems(basket.id).map { items -> basket to items }
            }
            combine(flows) { it.toList() }
        }
    }

    val uiState: StateFlow<AnalysisUiState> = combine(
        combinedDataFlow,
        repository.allCompanies,
        repository.prices,
        _selectedRange,
        _aiSummary,
        _isAiLoading,
        repository.getPortfolioHistory(),
        repository.exchangeRates,
        repository.getAllCachedInfo(),
        repository.getAllTransactionsFlow(),
        _vixValue
    ) { args ->
        @Suppress("UNCHECKED_CAST")
        val basketWithItems = args[0] as List<Pair<Basket, List<BasketItem>>>
        @Suppress("UNCHECKED_CAST")
        val companies = args[1] as List<Company>
        @Suppress("UNCHECKED_CAST")
        val prices = args[2] as Map<String, PriceSnapshot>
        val range = args[3] as PortfolioRange
        val aiSummary = args[4] as String?
        val isAiLoading = args[5] as Boolean
        @Suppress("UNCHECKED_CAST")
        val historyEntries = args[6] as List<PortfolioHistoryEntry>
        @Suppress("UNCHECKED_CAST")
        val rates = args[7] as Map<String, Double>
        @Suppress("UNCHECKED_CAST")
        val allInfo = args[8] as List<CachedCompanyInfo>
        @Suppress("UNCHECKED_CAST")
        val transactions = args[9] as List<PortfolioTransaction>
        val vixVal = args[10] as Double?

        val companyMap = companies.associateBy { it.symbol }
        val infoMap = allInfo.associateBy { it.symbol }

        val usdRate = rates["USD"] ?: 34.5
        val eurRate = rates["EUR"] ?: 37.2
        var totalPortfolioValue = 0.0
        var totalPortfolioCost = 0.0

        val marketValues = mutableMapOf<String, Double>()
        val sectorValues = mutableMapOf<String, Double>()
        val allHoldings = mutableListOf<MoverUiModel>()
        val dividendHoldings = mutableListOf<DividendEntry>()

        basketWithItems.forEach { (_, items) ->
            items.forEach { item ->
                val company = companyMap[item.symbol]
                val currentPrice = prices[item.symbol]?.price ?: company?.currentPrice ?: 0.0
                val rate = when (company?.market?.uppercase()) {
                    "NASDAQ", "NYSE" -> usdRate
                    "FRA", "EURONEXT" -> eurRate
                    else -> 1.0
                }
                val value = item.quantity * currentPrice * rate

                sectorValues[company?.sector ?: "Diğer"] = (sectorValues[company?.sector ?: "Diğer"] ?: 0.0) + value

                infoMap[item.symbol]?.let { info ->
                    val nextDiv = info.nextDividendDate
                    if (nextDiv != null && nextDiv > System.currentTimeMillis()) {
                        val yield = info.dividendYield ?: 0.0
                        val payout = item.quantity * currentPrice * (yield / 100.0) * rate

                        val existingIndex = dividendHoldings.indexOfFirst { it.symbol == item.symbol }
                        if (existingIndex >= 0) {
                            val existing = dividendHoldings[existingIndex]
                            dividendHoldings[existingIndex] = existing.copy(
                                quantity = existing.quantity + item.quantity,
                                projectedPayout = existing.projectedPayout + payout
                            )
                        } else {
                            dividendHoldings.add(DividendEntry(
                                symbol = item.symbol,
                                date = nextDiv,
                                yield = info.dividendYield,
                                quantity = item.quantity,
                                projectedPayout = payout
                            ))
                        }
                    }
                }
            }
        }

        val basketPerformances = basketWithItems.map { (basket, items) ->
            val rate = when (basket.market.uppercase()) {
                "NASDAQ", "NYSE" -> usdRate
                "FRA", "EURONEXT" -> eurRate
                else -> 1.0
            }
            var bValue = 0.0
            var bCost = 0.0
            items.forEach { item ->
                val company = companyMap[item.symbol]
                val currentPrice = prices[item.symbol]?.price ?: company?.currentPrice ?: 0.0
                val value = item.quantity * currentPrice
                val cost = item.quantity * item.buyPrice

                bValue += value
                bCost += cost
                totalPortfolioValue += value * rate
                totalPortfolioCost += cost * rate

                marketValues[basket.market] = (marketValues[basket.market] ?: 0.0) + (value * rate)

                allHoldings.add(MoverUiModel(
                    symbol = item.symbol,
                    currentPrice = currentPrice,
                    changePercent = if (item.buyPrice > 0) (currentPrice - item.buyPrice) / item.buyPrice * 100 else 0.0,
                    market = basket.market
                ))
            }

            BasketPerformanceUiModel(
                id = basket.id,
                name = basket.name,
                market = basket.market,
                totalValue = bValue * rate,
                changePercent = if (bCost > 0) (bValue - bCost) / bCost * 100 else 0.0
            )
        }.sortedByDescending { it.changePercent }

        val regionBreakdown = marketValues.map { (market, value) ->
            val region = Region.values().find { it.market == market }
            RegionSummary(
                label = region?.label ?: market,
                flag = region?.flag ?: "🌐",
                market = market,
                totalValue = value,
                allocationPercent = if (totalPortfolioValue > 0) (value / totalPortfolioValue).toFloat() else 0f
            )
        }.sortedByDescending { it.totalValue }

        val sectorBreakdown = sectorValues.map { (sector, value) ->
            RegionSummary(
                label = sector,
                flag = "📁",
                market = sector,
                totalValue = value,
                allocationPercent = if (totalPortfolioValue > 0) (value / totalPortfolioValue).toFloat() else 0f,
                isSector = true
            )
        }.sortedByDescending { it.totalValue }

        val combinedBreakdown = regionBreakdown + sectorBreakdown

        val best = allHoldings.maxByOrNull { it.changePercent }
        val worst = allHoldings.minByOrNull { it.changePercent }

        val geminiKey = settingsManager.getGeminiApiKey()

        // Real portfolio history calculation: snapshots first, or transaction ledger reconstruction
        val realHistory = when {
            historyEntries.isNotEmpty() -> historyEntries.map { it.totalValue.toFloat() }
            transactions.isNotEmpty() -> {
                val sortedTx = transactions.sortedBy { it.timestamp }
                var runningTotal = 0.0
                val points = mutableListOf<Float>()
                sortedTx.forEach { tx ->
                    val txValue = tx.quantity * tx.price
                    if (tx.isBuy) runningTotal += txValue else runningTotal -= txValue
                    points.add(runningTotal.coerceAtLeast(0.0).toFloat())
                }
                points
            }
            else -> emptyList()
        }
        val calculatedRiskMetrics = calculateRiskMetrics(realHistory)

        val realizedPnL = transactions.filter { !it.isBuy }.sumOf { it.realizedPnL }
        val unrealizedPnL = totalPortfolioValue - totalPortfolioCost

        AnalysisUiState(
            totalPortfolioValue = totalPortfolioValue,
            totalChangePercent = if (totalPortfolioCost > 0) (totalPortfolioValue - totalPortfolioCost) / totalPortfolioCost * 100 else 0.0,
            selectedRange = range,
            regionBreakdown = combinedBreakdown,
            bestPerformer = best,
            worstPerformer = worst,
            basketPerformances = basketPerformances,
            aiSummary = aiSummary,
            isAiLoading = isAiLoading,
            hasGeminiKey = !geminiKey.isNullOrBlank(),
            basketCount = basketWithItems.size,
            isLoading = false,
            portfolioHistory = realHistory,
            riskMetrics = calculatedRiskMetrics,
            benchmarkChangePercent = _benchmarkChangePct.value,
            benchmarkLabel = "BIST-100",
            targetAllocation = _targetAllocation.value,
            dividendCalendar = dividendHoldings.sortedBy { it.date },
            realizedPnL = realizedPnL,
            unrealizedPnL = unrealizedPnL,
            vixValue = vixVal
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AnalysisUiState())

    fun onRangeSelected(range: PortfolioRange) {
        _selectedRange.value = range
    }

    private fun calculateRiskMetrics(history: List<Float>): RiskMetrics {
        if (history.size < 2) {
            return RiskMetrics(
                sharpeRatio = 0.0,
                maxDrawdown = 0.0,
                volatility = 0.0,
                hasSufficientData = false
            )
        }
        val returns = history.zipWithNext { a, b -> if (a > 0f) (b - a) / a else 0f }
        val avg = returns.average()
        val variance = returns.map { (it - avg) * (it - avg) }.average()
        val volatility = kotlin.math.sqrt(variance) * 100.0
        val riskFreeRate = 0.0003
        val sharpe = if (volatility > 0) ((avg - riskFreeRate) / kotlin.math.sqrt(variance)) * kotlin.math.sqrt(252.0) else 0.0

        var peak = history.first()
        var maxDD = 0.0
        history.forEach { v ->
            if (v > peak) peak = v
            val dd = if (peak > 0) (peak - v) / peak * 100.0 else 0.0
            if (dd > maxDD) maxDD = dd
        }
        return RiskMetrics(
            sharpeRatio = sharpe,
            maxDrawdown = maxDD,
            volatility = volatility,
            hasSufficientData = true
        )
    }

    private fun fetchBenchmark() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val res = yahooPublicService.fetchPrice("XU100", "BIST")
                if (res is ScrapeResult.Success) {
                    _benchmarkChangePct.value = res.data.changePercent
                }
            } catch (_: Exception) {}
        }
    }

    private fun fetchVix() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val res = yahooPublicService.fetchPrice("^VIX", "INDEX")
                if (res is ScrapeResult.Success && res.data.price > 0) {
                    _vixValue.value = res.data.price
                } else {
                    _vixValue.value = null
                }
            } catch (_: Exception) {
                _vixValue.value = null
            }
        }
    }

    fun runPortfolioHealthCheck() {
        viewModelScope.launch {
            _isHealthChecking.value = true
            _isHealthChecking.value = false
        }
    }

    fun runStockScreener(query: String = "") {
        viewModelScope.launch {
            _isScreenerLoading.value = true
            _isScreenerLoading.value = false
        }
    }

    fun generateRebalanceReport() {
        viewModelScope.launch {
            _isRebalanceLoading.value = true
            _isRebalanceLoading.value = false
        }
    }
}
