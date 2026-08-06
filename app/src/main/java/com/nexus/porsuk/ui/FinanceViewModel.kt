package com.nexus.porsuk.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.data.local.entity.*
import com.nexus.porsuk.data.repository.FinanceRepository
import com.nexus.porsuk.data.remote.ScrapeResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

import com.nexus.porsuk.data.local.SettingsManager
import com.nexus.porsuk.data.remote.PortfolioDoctorEngine
import com.nexus.porsuk.data.remote.PortfolioDoctorMetrics
import com.nexus.porsuk.domain.model.PortfolioAsset

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class BasketWithStats(
    val basket: Basket,
    val totalValue: Double,
    val changePercent: Double,
    val itemCount: Int
)

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@HiltViewModel
class FinanceViewModel @Inject constructor(
    private val repository: FinanceRepository,
    private val settingsManager: SettingsManager,
    private val ipoRepository: com.nexus.porsuk.domain.repository.IpoRepository? = null,
    private val corporateActionRepository: com.nexus.porsuk.domain.repository.CorporateActionRepository? = null,
    private val dividendRepositoryPro: com.nexus.porsuk.domain.repository.DividendRepositoryPro? = null
) : ViewModel() {

    val prices: StateFlow<Map<String, PriceSnapshot>> = repository.prices.asStateFlow()
    val exchangeRates: StateFlow<Map<String, Double>> = repository.exchangeRates.asStateFlow()
    val allTefasFunds: StateFlow<List<com.nexus.porsuk.data.local.entity.TefasFundEntity>> = repository.allTefasFunds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun refreshExchangeRates() {
        viewModelScope.launch {
            repository.refreshExchangeRates()
        }
    }

    fun fetchPrice(symbol: String, market: String) {
        viewModelScope.launch {
            repository.refreshPrice(symbol, market)
        }
    }

    enum class ProfitCalculationMode { NOMINAL, INFLATION_ADJUSTED, USD_ADJUSTED }
    private val _profitMode = MutableStateFlow(ProfitCalculationMode.NOMINAL)
    val profitMode: StateFlow<ProfitCalculationMode> = _profitMode.asStateFlow()

    fun setProfitMode(mode: ProfitCalculationMode) {
        _profitMode.value = mode
    }

    private fun getEstimatedUsdRate(timestamp: Long, currentUsdRate: Double): Double {
        val t = timestamp / 1000L
        val t2023 = 1672531200L // Jan 1, 2023
        val t2024 = 1704067200L // Jan 1, 2024
        val t2025 = 1735689600L // Jan 1, 2025
        val now = System.currentTimeMillis() / 1000L
        
        return when {
            t < t2023 -> 18.0
            t < t2024 -> 18.7 + (t - t2023) * (29.8 - 18.7) / (t2024 - t2023)
            t < t2025 -> 29.8 + (t - t2024) * (34.5 - 29.8) / (t2025 - t2024)
            else -> 34.5 + (t - t2025) * (currentUsdRate - 34.5) / (now - t2025).coerceAtLeast(1L)
        }
    }

    val allBaskets = repository.allBaskets
    val allBasketItems = repository.allBasketItems
    val watchlist = repository.watchlist
    val allCompanies = repository.allCompanies
    val allCachedInfo = repository.getAllCachedInfo()

    fun getIncomeStatements(symbol: String): Flow<List<IncomeStatementEntity>> = repository.getIncomeStatements(symbol)
    fun getBalanceSheets(symbol: String): Flow<List<BalanceSheetEntity>> = repository.getBalanceSheets(symbol)
    fun getCashFlows(symbol: String): Flow<List<CashFlowEntity>> = repository.getCashFlows(symbol)
    fun getCompanyRatios(symbol: String): Flow<List<CompanyRatioEntity>> = repository.getCompanyRatios(symbol)
    val numberFormat = settingsManager.numberFormat.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "TR")
    val portfolioHistory: StateFlow<List<PortfolioHistoryEntry>> = repository.getPortfolioHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val xu100History: StateFlow<List<StockHistoryEntry>> = repository.getStockHistory("XU100")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val consolidatedHoldings: StateFlow<List<PortfolioAsset>> = repository.getConsolidatedAssetsFlow()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _selectedChartTimeframe = MutableStateFlow(0) // 0: 1G, 1: 1H, 2: 1A, 3: 3A, 4: 6A, 5: 1Y, 6: Tümü
    val selectedChartTimeframe: StateFlow<Int> = _selectedChartTimeframe.asStateFlow()

    private val _portfolioChartData = MutableStateFlow<List<Double>>(emptyList())
    val portfolioChartData: StateFlow<List<Double>> = _portfolioChartData.asStateFlow()

    val portfolioRiskMetrics: StateFlow<PortfolioDoctorMetrics?> = combine(
        allBasketItems,
        allCompanies
    ) { items, companies ->
        if (items.isEmpty()) null
        else PortfolioDoctorEngine.analyze(items, companies)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _aiPortfolioInsight = MutableStateFlow<String?>(null)
    val aiPortfolioInsight: StateFlow<String?> = _aiPortfolioInsight.asStateFlow()

    fun updateChartTimeframe(index: Int) {
        _selectedChartTimeframe.value = index
        viewModelScope.launch {
            val range = when(index) {
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
                    val service = com.nexus.porsuk.data.remote.GeminiService(apiKey)
                    val insight = service.generatePortfolioAiInsight(assets, metrics)
                    _aiPortfolioInsight.value = insight
                }
            }
        }
    }

    // Historical prices for stock details
    private val _historicalPrices = MutableStateFlow<List<Double>>(emptyList())
    val historicalPrices: StateFlow<List<Double>> = _historicalPrices.asStateFlow()

    private val _isHistoryLoading = MutableStateFlow(false)
    val isHistoryLoading: StateFlow<Boolean> = _isHistoryLoading.asStateFlow()

    fun fetchHistoricalPrices(symbol: String, market: String, range: String, interval: String) {
        viewModelScope.launch {
            _isHistoryLoading.value = true
            val result = repository.fetchHistoricalPrices(symbol, market, range, interval)
            if (result is ScrapeResult.Success) {
                _historicalPrices.value = result.data
            } else {
                _historicalPrices.value = emptyList()
            }
            _isHistoryLoading.value = false
        }
    }

    // Toplam Varlığı ve Maliyeti Hesapla
    private val basketItemsFlow = allBaskets.flatMapLatest { baskets ->
        val flows = baskets.map { basket ->
            repository.getBasketItems(basket.id).map { items ->
                basket to items
            }
        }
        if (flows.isEmpty()) flowOf(emptyList())
        else combine(flows) { it.toList() }
    }

    val totalBalanceTry: StateFlow<Double> = combine(basketItemsFlow, prices, allCompanies, exchangeRates) { basketWithItems, pricesMap, companies, rates ->
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

    val totalUniqueStocksCount: StateFlow<Int> = basketItemsFlow.map { list ->
        list.flatMap { it.second }.map { it.symbol }.distinct().size
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalChangePercent: StateFlow<Double> = combine(basketItemsFlow, prices, allCompanies, exchangeRates, profitMode) { basketWithItems, pricesMap, companies, rates, mode ->
        val companyMap = companies.associateBy { it.symbol }
        val usdRate = rates["USD"] ?: 34.5
        val eurRate = rates["EUR"] ?: 37.2
        val gbpRate = eurRate * 1.165
        var totalValueTry = 0.0
        var totalCostTry = 0.0
        
        // Compound daily inflation rates for BIST (TÜFE ~45% annual) and US (CPI ~3% annual) and EUR (~2.5% annual)
        val dailyTryInflation = 0.001026
        val dailyUsdInflation = 0.000081
        val dailyEurInflation = 0.000067
        
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
                
                when (mode) {
                    ProfitCalculationMode.NOMINAL -> {
                        totalValueTry += itemValueTry
                        totalCostTry += itemCostTry
                    }
                    ProfitCalculationMode.INFLATION_ADJUSTED -> {
                        val durationDays = ((System.currentTimeMillis() - item.buyDate) / (24 * 3600 * 1000)).coerceAtLeast(0L)
                        val inflationFactor = Math.pow(1.0 + when (basket.market.uppercase()) {
                            "NASDAQ", "NYSE" -> dailyUsdInflation
                            "FRA", "EURONEXT" -> dailyEurInflation
                            else -> dailyTryInflation
                        }, durationDays.toDouble())
                        
                        totalValueTry += itemValueTry
                        totalCostTry += itemCostTry * inflationFactor
                    }
                    ProfitCalculationMode.USD_ADJUSTED -> {
                        val buyUsdRate = getEstimatedUsdRate(item.buyDate, usdRate)
                        val costInUsd = if (basket.market.uppercase() == "NASDAQ" || basket.market.uppercase() == "NYSE") {
                            item.quantity * item.buyPrice
                        } else {
                            (item.quantity * item.buyPrice) / buyUsdRate
                        }
                        
                        val valueInUsd = if (basket.market.uppercase() == "NASDAQ" || basket.market.uppercase() == "NYSE") {
                            item.quantity * currentPrice
                        } else {
                            (item.quantity * currentPrice * rate) / usdRate
                        }
                        
                        totalValueTry += valueInUsd
                        totalCostTry += costInUsd
                    }
                }
            }
        }
        if (totalCostTry > 0) (totalValueTry - totalCostTry) / totalCostTry * 100 else 0.0
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

    val portfolioSectorData: StateFlow<List<Pair<String, Double>>> = combine(basketItemsFlow, prices, allCompanies, exchangeRates) { basketWithItems, pricesMap, companies, rates ->
        val companyMap = companies.associateBy { it.symbol }
        val usdRate = rates["USD"] ?: 34.5
        val eurRate = rates["EUR"] ?: 37.2
        val gbpRate = eurRate * 1.165
        
        basketWithItems.flatMap { (basket, items) ->
            val rate = when (basket.market.uppercase()) {
                "NASDAQ", "NYSE" -> usdRate
                "FRA", "EURONEXT", "ETR", "EPA", "AMS", "BME" -> eurRate
                "LSE" -> gbpRate
                "SWX" -> eurRate * 1.06
                else -> 1.0
            }
            items.map { item ->
                val currentPrice = pricesMap[item.symbol]?.price 
                    ?: companyMap[item.symbol]?.currentPrice?.takeIf { it > 0.0 } 
                    ?: item.buyPrice
                val sector = companyMap[item.symbol]?.sector ?: "Bilinmeyen"
                sector to (item.quantity * currentPrice * rate)
            }
        }
        .groupBy { it.first }
        .map { (sector, list) -> sector to list.sumOf { it.second } }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    val basketsWithStats: StateFlow<List<BasketWithStats>> = combine(basketItemsFlow, prices, allCompanies, exchangeRates) { list, pricesMap, companies, rates ->
        val companyMap = companies.associateBy { it.symbol }
        val usdRate = rates["USD"] ?: 34.5
        val eurRate = rates["EUR"] ?: 37.2
        // GBP kurunu EUR üzerinden tahmin et (EUR/GBP ≈ 0.86)
        val gbpRate = eurRate * 1.165
        list.map { (basket, items) ->
            val rate = when (basket.market.uppercase()) {
                "NASDAQ", "NYSE" -> usdRate
                "FRA", "EURONEXT", "ETR", "EPA", "AMS", "BME" -> eurRate
                "LSE" -> gbpRate
                "SWX" -> eurRate * 1.06 // CHF yaklaşık kur
                else -> 1.0
            }
            var totalValue = 0.0
            var totalCost = 0.0
            items.forEach { item ->
                val currentPrice = pricesMap[item.symbol]?.price
                    ?: companyMap[item.symbol]?.currentPrice?.takeIf { it > 0.0 }
                    ?: item.buyPrice
                // Her iki tarafta da rate uygula → % değişim tutarlı olur
                totalValue += item.quantity * currentPrice * rate
                totalCost += item.quantity * item.buyPrice * rate
            }
            val change = if (totalCost > 0) (totalValue - totalCost) / totalCost * 100 else 0.0
            BasketWithStats(basket, totalValue, change, items.size)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _searchResults = MutableStateFlow<List<com.nexus.porsuk.data.model.StockDetails>>(emptyList())
    val searchResults: StateFlow<List<com.nexus.porsuk.data.model.StockDetails>> = _searchResults
    
    // For backward compatibility with older screens
    val allStocks = repository.watchlist.map { list -> list.map { com.nexus.porsuk.data.local.entity.StockAsset(symbol = it.symbol, exchange = "IST", purchasePrice = 0.0, quantity = 0.0, purchaseDate = 0L) } }
    val allFunds = repository.allBaskets

    private val _tickerData = MutableStateFlow<List<Pair<String, Double>>>(emptyList())
    val tickerData: StateFlow<List<Pair<String, Double>>> = _tickerData

    private val _aiAnalysis = MutableStateFlow<String?>(null)
    val aiAnalysis: StateFlow<String?> = _aiAnalysis

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading

    suspend fun getAiOracleReport(
        symbol: String,
        currentPrice: Double,
        income: List<IncomeStatementEntity>,
        ratios: List<CompanyRatioEntity>
    ): String {
        val apiKey = settingsManager.getGeminiApiKey()
        if (apiKey.isNullOrBlank()) {
            return "🔮 **AI Oracle Raporu**\n\nYapay Zeka analizleri için Ayarlar sayfasından geçerli bir Gemini API Key kaydedilmelidir."
        }
        val gemini = com.nexus.porsuk.data.remote.GeminiService(apiKey)
        return gemini.getAiOracleReport(symbol, currentPrice, income, ratios)
    }

    // Faz 2 — Haber Duyarlılık Analizi
    private val _newsSentiment = MutableStateFlow<String?>(null)
    val newsSentiment: StateFlow<String?> = _newsSentiment

    private val _isNewsSentimentLoading = MutableStateFlow(false)
    val isNewsSentimentLoading: StateFlow<Boolean> = _isNewsSentimentLoading

    // Teknik Analiz
    private val _technicalAnalysis = MutableStateFlow<com.nexus.porsuk.data.model.TechnicalAnalysis?>(null)
    val technicalAnalysis: StateFlow<com.nexus.porsuk.data.model.TechnicalAnalysis?> = _technicalAnalysis

    private val _isTechnicalLoading = MutableStateFlow(false)
    val isTechnicalLoading: StateFlow<Boolean> = _isTechnicalLoading

    init {
        viewModelScope.launch {
            // Seed Popular Companies
            val companies = repository.getAllCompaniesDirect()
            val hasEurope = companies.any { it.market == "FRA" }
            if (companies.size < 100 || !hasEurope) {
                val initialList = com.nexus.porsuk.data.remote.ExtendedDatabaseSeeder.getPopularCompanies().map { company ->
                    val seededPrice = when (company.market) {
                        "BIST" -> (40 + (company.symbol.hashCode() % 310)).toDouble()
                        "FRA" -> (10 + (company.symbol.hashCode() % 150)).toDouble()
                        else -> (50 + (company.symbol.hashCode() % 400)).toDouble()
                    }
                    company.copy(currentPrice = seededPrice)
                }
                
                val existingSymbols = companies.map { it.symbol }.toSet()
                val newCompanies = initialList.filter { it.symbol !in existingSymbols }
                
                if (newCompanies.isNotEmpty()) {
                    repository.insertCompanies(newCompanies)
                    
                    // RichOfflineDataEngine'den zengin verilerle seed yap
                    newCompanies.forEach { company ->
                        val richData = com.nexus.porsuk.data.remote.RichOfflineDataEngine.getRichDetailsFor(
                            symbol = company.symbol,
                            name = company.name,
                            price = company.currentPrice,
                            market = company.market
                        )
                        val peRatioDouble = richData.peRatio.toDoubleOrNull()
                        val divYieldDouble = richData.dividendYield.replace("%", "").toDoubleOrNull()
                        val week52HighDouble = richData.week52High.replace("[^0-9.]".toRegex(), "").toDoubleOrNull()
                        val week52LowDouble = richData.week52Low.replace("[^0-9.]".toRegex(), "").toDoubleOrNull()
                        repository.insertCachedInfo(
                            com.nexus.porsuk.data.local.entity.CachedCompanyInfo(
                                symbol = company.symbol,
                                about = richData.about,
                                peRatio = peRatioDouble,
                                marketCap = richData.marketCap,
                                week52High = week52HighDouble,
                                week52Low = week52LowDouble,
                                dividendYield = divYieldDouble,
                                volume = richData.volume,
                                lastUpdated = System.currentTimeMillis()
                            )
                        )
                    }
                }
            }

            // Initial Seed for Sample Basket
            val isSeeded = settingsManager.isSampleSeeded.first()
            if (!isSeeded) {
                allBaskets.first().let { baskets ->
                    if (baskets.isEmpty()) {
                        val seed = com.nexus.porsuk.data.remote.ExtendedDatabaseSeeder.getInitialBasketData()
                        val basketId = repository.addBasket(
                            com.nexus.porsuk.data.local.entity.Basket(
                                name = seed.basketName,
                                market = seed.region
                            )
                        ).toInt()
                        
                        seed.items.forEach { item ->
                            repository.addBasketItem(
                                com.nexus.porsuk.data.local.entity.BasketItem(
                                    basketId = basketId,
                                    symbol = item.symbol,
                                    quantity = item.quantity,
                                    buyPrice = item.buyPrice,
                                    buyDate = System.currentTimeMillis()
                                )
                            )
                        }
                    }
                }
                settingsManager.setSampleSeeded(true)
            }
            
            refreshTicker()
            refreshAllData()
            startAutoRefresh()
        }
    }

    // ─── Otomatik Periyodik Yenileme ──────────────────────────────────────────
    private val AUTO_REFRESH_INTERVAL_MS = 5 * 60 * 1000L // 5 dakika
    private var autoRefreshJob: Job? = null

    private fun startAutoRefresh() {
        autoRefreshJob?.cancel()
        autoRefreshJob = viewModelScope.launch {
            while (true) {
                delay(AUTO_REFRESH_INTERVAL_MS)
                refreshTicker()
                refreshAllData()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        autoRefreshJob?.cancel()
    }

    private suspend fun refreshTicker() {
        val indices = listOf("INDEXBIST:XU100", "USDTRY", "EURTRY")
        val results = mutableListOf<Pair<String, Double>>()
        indices.forEach { code ->
            val symbol = if (code.contains(":")) code.split(":")[1] else code
            val market = if (code.contains(":")) code.split(":")[0] else "CURRENCY"
            val result = repository.refreshPrice(symbol, market)
            if (result is ScrapeResult.Success) {
                results.add(symbol to result.data.price)
                if (symbol == "USDTRY") repository.exchangeRates.update { it + ("USD" to result.data.price) }
                if (symbol == "EURTRY") repository.exchangeRates.update { it + ("EUR" to result.data.price) }
                
                // Track historical price for benchmarking (Item 5)
                if (symbol == "XU100") {
                    repository.insertPriceHistoryEntry(symbol, result.data.price)
                }
            }
        }
        _tickerData.value = results
    }

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    fun refreshAllData() {
        viewModelScope.launch {
            _isRefreshing.value = true
            refreshTicker()
            
            val watchlistSymbols = watchlist.first().map { it.symbol }
            val popularSymbols = listOf("THYAO", "EREGL", "TUPRS", "ASELS", "KCHOL", "GARAN", "AKBNK", "BIMAS", "SISE", "PGSUS")
            
            // Kullanıcının sepetlerindeki TÜM hisseleri de güncelle
            val basketItems = allBasketItems.first()
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
                    // Also refresh company metadata for portfolio assets
                    if (symbol in basketSymbols) {
                        repository.refreshCompanyInfo(symbol, market)
                    }
                }
            }
            jobs.forEach { it.join() }
            
            // Calculate and record current portfolio history
            val baskets = repository.allBaskets.first()
            val companies = repository.allCompanies.first()
            val companyMap = companies.associateBy { it.symbol }
            val currentPrices = repository.prices.value
            val usdRate = repository.exchangeRates.value["USD"] ?: 34.5
            val eurRate = repository.exchangeRates.value["EUR"] ?: 37.2
            val gbpRate = eurRate * 1.165
            
            var totalValue = 0.0
            baskets.forEach { basket ->
                val items = repository.getBasketItems(basket.id).first()
                val rate = when (basket.market.uppercase()) {
                    "NASDAQ", "NYSE" -> usdRate
                    "FRA", "EURONEXT", "ETR", "EPA", "AMS", "BME" -> eurRate
                    "LSE" -> gbpRate
                    "SWX" -> eurRate * 1.06
                    else -> 1.0
                }
                items.forEach { item ->
                    val currentPrice = currentPrices[item.symbol]?.price ?: companyMap[item.symbol]?.currentPrice ?: 0.0
                    totalValue += item.quantity * currentPrice * rate
                }
            }
            
            if (totalValue > 0.0) {
                repository.insertPortfolioHistoryEntry(PortfolioHistoryEntry(totalValue = totalValue))
            }
            
            _isRefreshing.value = false
        }
    }

    fun refreshStockPrice(symbol: String, market: String) {
        viewModelScope.launch {
            val result = repository.refreshPrice(symbol, market)
            if (result is ScrapeResult.Success) {
                repository.prices.update { it + (symbol to result.data) }
                
                // Also update Company table for persistence/offline
                repository.getCompany(symbol)?.let { company ->
                    repository.insertCompanies(listOf(company.copy(
                        currentPrice = result.data.price,
                        changePercent = result.data.changePercent,
                        lastUpdated = System.currentTimeMillis()
                    )))
                }
            }
        }
    }

    fun getCachedInfo(symbol: String) = repository.getCachedInfo(symbol)
    fun getNews(symbol: String) = repository.getNews(symbol)

    fun refreshDetails(symbol: String, market: String) {
        viewModelScope.launch {
            repository.refreshCompanyInfo(symbol, market)
            repository.refreshNews(symbol, market)
            refreshStockPrice(symbol, market)
        }
    }

    fun getAiAnalysis(symbol: String) {
        viewModelScope.launch {
            _isAiLoading.value = true
            val apiKey = settingsManager.getGeminiApiKey()
            if (apiKey.isNullOrBlank()) {
                _aiAnalysis.value = "Hata: Yapay zeka yorumu alabilmek için lütfen önce Ayarlar sayfasından geçerli bir Gemini API anahtarı kaydedin."
                _isAiLoading.value = false
                return@launch
            }
            val info = repository.getCachedInfo(symbol).first()
            val price = repository.prices.value[symbol]
            val news = repository.getNews(symbol).first()
            
            val service = com.nexus.porsuk.data.remote.GeminiService(apiKey)
            _aiAnalysis.value = service.getStockAnalysis(symbol, info, price, news)
            _isAiLoading.value = false
        }
    }

    fun fetchTechnicalAnalysis(symbol: String, market: String) {
        viewModelScope.launch {
            _isTechnicalLoading.value = true
            val result = repository.getTechnicalAnalysis(symbol, market)
            if (result is ScrapeResult.Success) {
                _technicalAnalysis.value = result.data
            }
            _isTechnicalLoading.value = false
        }
    }

    // ─── Faz 2: Haber Duyarlılık Analizi ──────────────────────────────────
    fun analyzeNewsSentiment(symbol: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isNewsSentimentLoading.value = true
            _newsSentiment.value = null
            val apiKey = settingsManager.getGeminiApiKey()
            if (apiKey.isNullOrBlank()) {
                _newsSentiment.value = "API anahtarı gerekli"
                _isNewsSentimentLoading.value = false
                return@launch
            }
            val newsList = repository.getNews(symbol).first()
            if (newsList.isEmpty()) {
                _newsSentiment.value = "Analiz için yeterli haber bulunamadı."
                _isNewsSentimentLoading.value = false
                return@launch
            }
            val headlinesText = newsList.take(8).joinToString("\n") { "- ${it.title}" }
            val prompt = """
                Aşağıdaki haberler $symbol hissesiyle ilgilidir. 
                Her haber için 1-10 arası bir etki skoru ver (10=çok pozitif, 1=çok negatif).
                Ardından genel haber havasını tek sayı ve 2 cümle ile özetle. Türkçe yaz.
                Format:
                GENEL_SKOR: X
                ÖZET: ...
                HABERLER:
                [her haber için]: SKOR: X | BAŞLIK
                Haberler:
                $headlinesText
            """.trimIndent()
            try {
                val service = com.nexus.porsuk.data.remote.GeminiService(apiKey)
                val result = service.chat(prompt)
                _newsSentiment.value = result
            } catch (e: Exception) {
                _newsSentiment.value = "Analiz hatası: ${e.message}"
            }
            _isNewsSentimentLoading.value = false
        }
    }

    fun addBasket(name: String, market: String, category: String = "Dengeli") {
        viewModelScope.launch {
            repository.addBasket(Basket(name = name, market = market, category = category))
        }
    }

    fun renameBasket(basketId: Int, newName: String) {
        viewModelScope.launch {
            val basket = repository.getBasketById(basketId).first()
            if (basket != null) {
                repository.updateBasket(basket.copy(name = newName))
            }
        }
    }

    fun deleteBasket(basketId: Int) {
        viewModelScope.launch {
            val basket = repository.getBasketById(basketId).first()
            if (basket != null) {
                repository.deleteBasket(basket)
            }
        }
    }

    // For compatibility
    fun addFund(name: String, description: String = "") {
        addBasket(name, "IST")
    }

    fun addStockToFund(
        symbol: String,
        exchange: String,
        price: Double,
        quantity: Double,
        date: Long,
        fundId: Int?
    ) {
        viewModelScope.launch {
            repository.addBasketItem(
                com.nexus.porsuk.data.local.entity.BasketItem(
                    basketId = fundId ?: 0,
                    symbol = symbol,
                    quantity = quantity,
                    buyPrice = price,
                    buyDate = date
                )
            )
        }
    }

    fun addToWatchlist(symbol: String) {
        viewModelScope.launch {
            repository.addToWatchlist(symbol)
        }
    }

    fun searchStock(symbol: String, market: String) {
        viewModelScope.launch {
            val result = repository.refreshPrice(symbol, market)
            if (result is ScrapeResult.Success) {
                val details = com.nexus.porsuk.data.model.StockDetails(
                    symbol = symbol,
                    name = symbol, // Will update when details loaded
                    price = result.data.price,
                    currency = "",
                    changeAmount = 0.0,
                    changePercentage = result.data.changePercent
                )
                _searchResults.value = listOf(details)
                checkAndAddCompany(symbol, market)
            } else {
                _searchResults.value = emptyList()
            }
        }
    }

    private suspend fun checkAndAddCompany(symbol: String, market: String) {
        val existing = repository.getCompany(symbol)
        if (existing == null) {
            val yahooPublic = com.nexus.porsuk.data.remote.YahooFinancePublicService()
            val result = yahooPublic.fetchCompanyInfo(symbol, market)
            val companyName = if (result is ScrapeResult.Success) result.data.about else symbol
            
            // Clean domain names for Clearbit logos
            val cleanName = companyName.replace("[^a-zA-Z\\s]".toRegex(), "").trim()
            val domain = cleanName.split(" ").firstOrNull()?.lowercase()?.filter { it.isLetter() } ?: symbol.lowercase()
            val logoUrl = "https://logo.clearbit.com/$domain.com"
            
            repository.insertCompanies(listOf(
                Company(
                    symbol = symbol,
                    name = companyName, 
                    market = market,
                    logoInitials = symbol.take(3),
                    logoUrl = logoUrl,
                    sector = "Genel" // Default sector for new discoveries
                )
            ))
        }
    }

    // Portfolio Health Check
    private val _portfolioHealthCheckResult = MutableStateFlow<String>("")
    val portfolioHealthCheckResult: StateFlow<String> = _portfolioHealthCheckResult

    private val _isHealthChecking = MutableStateFlow(false)
    val isHealthChecking: StateFlow<Boolean> = _isHealthChecking

    fun runPortfolioHealthCheck() {
        val apiKey = settingsManager.getGeminiApiKey()
        if (apiKey.isNullOrBlank()) {
            _portfolioHealthCheckResult.value = "Hata: Portföy sağlık taraması yapabilmek için lütfen öncelikle Ayarlar sayfasından geçerli bir Gemini API anahtarı kaydedin."
            return
        }
        viewModelScope.launch {
            _isHealthChecking.value = true
            try {
                val holdings = repository.getAllBasketItemsDirect()
                val companies = allCompanies.first()
                if (holdings.isEmpty()) {
                    _portfolioHealthCheckResult.value = "Portföyünüzde henüz hisse bulunmuyor. Lütfen bir sepete hisse ekleyin ve tekrar deneyin."
                } else {
                    val service = com.nexus.porsuk.data.remote.GeminiService(apiKey)
                    _portfolioHealthCheckResult.value = service.getPortfolioHealthCheck(holdings, companies)
                }
            } catch (e: Exception) {
                _portfolioHealthCheckResult.value = "Hata oluştu: ${e.localizedMessage ?: "Bilinmeyen hata"}"
            } finally {
                _isHealthChecking.value = false
            }
        }
    }

    // Portfolio Rebalancing
    private val _portfolioRebalanceResult = MutableStateFlow<String>("")
    val portfolioRebalanceResult: StateFlow<String> = _portfolioRebalanceResult

    private val _isRebalancing = MutableStateFlow(false)
    val isRebalancing: StateFlow<Boolean> = _isRebalancing

    fun runPortfolioRebalance() {
        val apiKey = settingsManager.getGeminiApiKey()
        if (apiKey.isNullOrBlank()) {
            _portfolioRebalanceResult.value = "Hata: Portföy dengeleme analizi yapabilmek için lütfen öncelikle Ayarlar sayfasından geçerli bir Gemini API anahtarı kaydedin."
            return
        }
        viewModelScope.launch {
            _isRebalancing.value = true
            try {
                val holdings = repository.getAllBasketItemsDirect()
                val companies = allCompanies.first()
                if (holdings.isEmpty()) {
                    _portfolioRebalanceResult.value = "Portföyünüzde henüz hisse bulunmuyor. Lütfen bir sepete hisse ekleyin ve tekrar deneyin."
                } else {
                    val service = com.nexus.porsuk.data.remote.GeminiService(apiKey)
                    _portfolioRebalanceResult.value = service.getPortfolioRebalanceReport(holdings, companies)
                }
            } catch (e: Exception) {
                _portfolioRebalanceResult.value = "Hata oluştu: ${e.localizedMessage ?: "Bilinmeyen hata"}"
            } finally {
                _isRebalancing.value = false
            }
        }
    }

    // Price Alerts
    fun insertPriceAlert(symbol: String, market: String, targetPrice: Double, isAbove: Boolean) {
        viewModelScope.launch {
            repository.insertPriceAlert(PriceAlert(
                symbol = symbol,
                market = market,
                targetPrice = targetPrice,
                isAbove = isAbove,
                isActive = true
            ))
        }
    }

    fun deletePriceAlert(alertId: Int) {
        viewModelScope.launch {
            repository.deletePriceAlert(alertId)
        }
    }

    fun insertPriceAlertObject(alert: PriceAlert) {
        viewModelScope.launch {
            repository.insertPriceAlert(alert)
        }
    }

    fun getAlertsForStock(symbol: String): Flow<List<PriceAlert>> {
        return repository.getAlertsForStock(symbol)
    }

    val allPriceAlerts: Flow<List<PriceAlert>> = repository.getAllPriceAlertsFlow()

    fun removeFromWatchlist(item: com.nexus.porsuk.data.local.entity.WatchlistItem) {
        viewModelScope.launch {
            repository.removeFromWatchlist(item)
        }
    }

    fun addBasketWithTemplate(name: String, market: String, templateType: String) {
        viewModelScope.launch {
            val category = when (templateType) {
                "BIST_TEMETTU" -> "Temettü Odaklı"
                "TECH_GIANTS" -> "Büyüme Odaklı"
                else -> "Dengeli"
            }
            val basketId = repository.addBasket(Basket(name = name, market = market, category = category))
            when (templateType) {
                "RAY_DALIO" -> {
                    val items = listOf(
                        BasketItem(basketId = basketId.toInt(), symbol = "SPY", quantity = 3.0, buyPrice = 530.0, buyDate = System.currentTimeMillis() - 30 * 24 * 3600 * 1000L),
                        BasketItem(basketId = basketId.toInt(), symbol = "TLT", quantity = 4.21, buyPrice = 95.0, buyDate = System.currentTimeMillis() - 30 * 24 * 3600 * 1000L),
                        BasketItem(basketId = basketId.toInt(), symbol = "IEF", quantity = 1.58, buyPrice = 95.0, buyDate = System.currentTimeMillis() - 30 * 24 * 3600 * 1000L),
                        BasketItem(basketId = basketId.toInt(), symbol = "GLD", quantity = 0.34, buyPrice = 220.0, buyDate = System.currentTimeMillis() - 30 * 24 * 3600 * 1000L),
                        BasketItem(basketId = basketId.toInt(), symbol = "GSG", quantity = 3.57, buyPrice = 21.0, buyDate = System.currentTimeMillis() - 30 * 24 * 3600 * 1000L)
                    )
                    items.forEach { repository.addBasketItem(it) }
                }
                "WARREN_BUFFETT" -> {
                    val items = listOf(
                        BasketItem(basketId = basketId.toInt(), symbol = "VOO", quantity = 9.0, buyPrice = 490.0, buyDate = System.currentTimeMillis() - 60 * 24 * 3600 * 1000L),
                        BasketItem(basketId = basketId.toInt(), symbol = "BIL", quantity = 1.1, buyPrice = 91.5, buyDate = System.currentTimeMillis() - 60 * 24 * 3600 * 1000L)
                    )
                    items.forEach { repository.addBasketItem(it) }
                }
                "BIST_TEMETTU" -> {
                    val items = listOf(
                        BasketItem(basketId = basketId.toInt(), symbol = "TUPRS", quantity = 25.0, buyPrice = 160.0, buyDate = System.currentTimeMillis() - 90 * 24 * 3600 * 1000L),
                        BasketItem(basketId = basketId.toInt(), symbol = "EREGL", quantity = 50.0, buyPrice = 50.0, buyDate = System.currentTimeMillis() - 90 * 24 * 3600 * 1000L),
                        BasketItem(basketId = basketId.toInt(), symbol = "FROTO", quantity = 2.5, buyPrice = 1000.0, buyDate = System.currentTimeMillis() - 90 * 24 * 3600 * 1000L),
                        BasketItem(basketId = basketId.toInt(), symbol = "TOASO", quantity = 11.3, buyPrice = 220.0, buyDate = System.currentTimeMillis() - 90 * 24 * 3600 * 1000L)
                    )
                    items.forEach { repository.addBasketItem(it) }
                }
                "TECH_GIANTS" -> {
                    val items = listOf(
                        BasketItem(basketId = basketId.toInt(), symbol = "AAPL", quantity = 2.5, buyPrice = 220.0, buyDate = System.currentTimeMillis() - 15 * 24 * 3600 * 1000L),
                        BasketItem(basketId = basketId.toInt(), symbol = "MSFT", quantity = 1.2, buyPrice = 420.0, buyDate = System.currentTimeMillis() - 15 * 24 * 3600 * 1000L),
                        BasketItem(basketId = basketId.toInt(), symbol = "GOOG", quantity = 2.7, buyPrice = 180.0, buyDate = System.currentTimeMillis() - 15 * 24 * 3600 * 1000L),
                        BasketItem(basketId = basketId.toInt(), symbol = "NVDA", quantity = 4.1, buyPrice = 120.0, buyDate = System.currentTimeMillis() - 15 * 24 * 3600 * 1000L)
                    )
                    items.forEach { repository.addBasketItem(it) }
                }
            }
        }
    }

    // --- IPO & Corporate Actions Intelligence Platform ---
    private val _ipoIntelligence = MutableStateFlow<com.nexus.porsuk.domain.model.IpoIntelligence?>(null)
    val ipoIntelligence = _ipoIntelligence.asStateFlow()

    private val _corporateActions = MutableStateFlow<List<com.nexus.porsuk.domain.model.CorporateAction>>(emptyList())
    val corporateActions = _corporateActions.asStateFlow()

    private val _dividendAnalytics = MutableStateFlow<com.nexus.porsuk.domain.model.DividendAnalytics?>(null)
    val dividendAnalytics = _dividendAnalytics.asStateFlow()

    fun loadIpoIntelligence(symbol: String) {
        viewModelScope.launch {
            ipoRepository?.getIpoDetail(symbol)?.collect { _ipoIntelligence.value = it }
        }
    }

    fun loadCorporateActions(symbol: String) {
        viewModelScope.launch {
            corporateActionRepository?.getActionsForSymbol(symbol)?.collect { _corporateActions.value = it }
        }
    }

    fun loadDividendAnalytics(symbol: String) {
        viewModelScope.launch {
            dividendRepositoryPro?.getDividendAnalytics(symbol)?.collect { _dividendAnalytics.value = it }
        }
    }
}
