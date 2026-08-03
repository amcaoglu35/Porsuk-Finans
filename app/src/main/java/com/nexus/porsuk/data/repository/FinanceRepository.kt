package com.nexus.porsuk.data.repository

import com.nexus.porsuk.data.local.dao.AssetDao
import com.nexus.porsuk.data.local.entity.*
import com.nexus.porsuk.data.remote.*
import com.nexus.porsuk.data.remote.api.NewsApi
import com.nexus.porsuk.data.remote.datasource.FredRemoteDataSource
import com.nexus.porsuk.domain.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FinanceRepository @Inject constructor(
    private val assetDao: AssetDao,
    private val scraper: GoogleFinanceScraper,
    private val eventBus: com.nexus.porsuk.core.common.PorsukEventBus? = null,
    private val finnhubService: FinnhubService? = null,
    private val yahooService: YahooFinanceService? = null,
    private val settingsManager: com.nexus.porsuk.data.local.SettingsManager? = null,
    private val newsApi: NewsApi? = null,
    private val fredRemoteDataSource: FredRemoteDataSource? = null,
    private val tefasFundDao: com.nexus.porsuk.data.local.dao.TefasFundDao? = null
) {
    val allBaskets: Flow<List<Basket>> = assetDao.getAllBaskets()
    val allBasketItems: Flow<List<BasketItem>> = assetDao.getAllBasketItemsFlow()
    val watchlist: Flow<List<WatchlistItem>> = assetDao.getWatchlist()
    val allCompanies: Flow<List<Company>> = assetDao.getAllCompanies()
    val allDividends: Flow<List<DividendCalendarEntry>> = assetDao.getAllDividends()
    val allIpos: Flow<List<IpoCalendarEntry>> = assetDao.getAllIpos()
    val allEconomicEvents: Flow<List<EconomicEventEntry>> = assetDao.getAllEconomicEvents()
    val allTefasFunds: Flow<List<com.nexus.porsuk.data.local.entity.TefasFundEntity>> = tefasFundDao?.getAllActiveFunds() ?: flowOf(emptyList())

    val prices = MutableStateFlow<Map<String, PriceSnapshot>>(emptyMap())
    val exchangeRates = MutableStateFlow<Map<String, Double>>(mapOf("USD" to 34.5, "EUR" to 37.2))
    
    private val yahooPublicService = YahooFinancePublicService()
    private var fmpService: FinancialModelingPrepService? = null

    init {
        settingsManager?.let { sm ->
            val fmpKey = runBlocking { sm.fmpApiKey.first() }
            if (!fmpKey.isNullOrBlank()) {
                fmpService = FinancialModelingPrepService(fmpKey)
            }
        }
    }

    fun getConsolidatedAssetsFlow(): Flow<List<PortfolioAsset>> = combine(
        allBasketItems,
        allBaskets,
        prices,
        allCompanies,
        exchangeRates
    ) { items, baskets, pricesMap, companies, rates ->
        val companyMap = companies.associateBy { it.symbol }
        val basketMap = baskets.associateBy { it.id }
        val usdRate = rates["USD"] ?: 34.5
        val eurRate = rates["EUR"] ?: 37.2

        val grouped = items.groupBy { it.symbol }
        
        grouped.map { (symbol, symbolItems) ->
            val company = companyMap[symbol]
            val currentPrice = pricesMap[symbol]?.price 
                ?: company?.currentPrice?.takeIf { it > 0.0 } 
                ?: symbolItems.first().buyPrice
            
            var totalQty = 0.0
            var totalCostTry = 0.0
            
            symbolItems.forEach { item ->
                val basket = basketMap[item.basketId]
                val rate = when (basket?.market?.uppercase()) {
                    "NASDAQ", "NYSE" -> usdRate
                    "FRA", "EURONEXT" -> eurRate
                    else -> 1.0
                }
                totalQty += item.quantity
                totalCostTry += item.quantity * item.buyPrice * rate
            }
            
            val avgCostTry = if (totalQty > 0) totalCostTry / totalQty else 0.0
            val market = company?.market ?: "BIST"
            val rate = when (market.uppercase()) {
                "NASDAQ", "NYSE" -> usdRate
                "FRA", "EURONEXT" -> eurRate
                else -> 1.0
            }
            
            val totalValueTry = totalQty * currentPrice * rate
            val pnlTry = totalValueTry - totalCostTry
            val pnlPct = if (totalCostTry > 0) (pnlTry / totalCostTry) * 100.0 else 0.0
            
            PortfolioAsset(
                id = symbol.hashCode().toLong(),
                portfolioId = "consolidated",
                symbol = symbol,
                name = company?.name ?: symbol,
                quantity = totalQty,
                averageCost = avgCostTry,
                currentPrice = currentPrice * rate,
                totalValue = totalValueTry,
                totalCost = totalCostTry,
                profitLoss = pnlTry,
                profitPercent = pnlPct,
                assetCategory = AssetCategory.fromSymbol(symbol),
                purchaseDate = symbolItems.minOf { it.buyDate },
                lastUpdated = System.currentTimeMillis()
            )
        }.sortedByDescending { it.totalValue }
    }

    suspend fun refreshFullCompanyDetail(symbol: String) {
        if (fmpService == null) return
        val incRes = fmpService!!.fetchIncomeStatement(symbol)
        if (incRes is ScrapeResult.Success) {
            assetDao.insertIncomeStatements(incRes.data.map {
                IncomeStatementEntity(it.symbol, it.date, it.revenue, it.grossProfit, it.ebitda, it.netIncome, it.eps)
            })
        }
        val balRes = fmpService!!.fetchBalanceSheet(symbol)
        if (balRes is ScrapeResult.Success) {
            assetDao.insertBalanceSheets(balRes.data.map {
                BalanceSheetEntity(it.symbol, it.date, it.totalAssets, it.totalLiabilities, it.totalStockholdersEquity, it.netDebt)
            })
        }
        val cfRes = fmpService!!.fetchCashFlow(symbol)
        if (cfRes is ScrapeResult.Success) {
            assetDao.insertCashFlows(cfRes.data.map {
                CashFlowEntity(it.symbol, it.date, it.netCashProvidedByOperatingActivities, it.freeCashFlow)
            })
        }
        val ratRes = fmpService!!.fetchRatios(symbol)
        if (ratRes is ScrapeResult.Success) {
            assetDao.insertCompanyRatios(ratRes.data.map {
                CompanyRatioEntity(it.symbol, it.date, it.returnOnEquity, it.returnOnAssets, it.priceEarningsRatio, it.priceToBookRatio, it.currentRatio, it.debtEquityRatio)
            })
        }
        try {
            val newsRes = newsApi?.getNews(query = symbol)
            if (newsRes != null && newsRes.status == "ok") {
                val entities = newsRes.articles.map {
                    NewsItemEntity(
                        symbol = symbol,
                        title = it.title,
                        summary = it.description,
                        source = it.source.name,
                        publishedAt = try { 
                            java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.US).parse(it.publishedAt)?.time ?: System.currentTimeMillis() 
                        } catch(e: Exception) { System.currentTimeMillis() },
                        url = it.url,
                        imageUrl = it.urlToImage,
                        sentiment = "NEUTRAL"
                    )
                }
                assetDao.insertNews(entities)
            }
        } catch (e: Exception) { }
    }

    suspend fun refreshNewsByCategory(category: String) {
        try {
            val query = when(category) {
                "Şirket" -> "stock market companies"
                "Sektör" -> "industry sector finance"
                "Ekonomi" -> "economy global market"
                "Dünya Piyasaları" -> "global stock markets"
                "Kripto" -> "crypto bitcoin blockchain"
                "Teknoloji" -> "technology tech stocks"
                "Yapay Zeka" -> "artificial intelligence ai"
                else -> "finance business news"
            }
            val newsRes = newsApi?.getNews(query = query)
            if (newsRes != null && newsRes.status == "ok") {
                val entities = newsRes.articles.map {
                    NewsItemEntity(
                        symbol = "GLOBAL_$category",
                        title = it.title,
                        summary = it.description,
                        source = it.source.name,
                        publishedAt = try { 
                            java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.US).parse(it.publishedAt)?.time ?: System.currentTimeMillis() 
                        } catch(e: Exception) { System.currentTimeMillis() },
                        url = it.url,
                        imageUrl = it.urlToImage,
                        sentiment = "NEUTRAL"
                    )
                }
                assetDao.insertNews(entities)
            }
        } catch (e: Exception) { }
    }

    suspend fun refreshMacroIndicators() {
        if (fredRemoteDataSource == null) return
        val indicators = listOf("FEDFUNDS", "CPIAUCSL", "PPIACO", "GDP", "UNRATE", "GS10", "GS2", "VIXCLS", "DTWEXBGS", "M2SL", "UMCSENT")
        indicators.forEach { seriesId ->
            when (val res = fredRemoteDataSource.getObservations(seriesId)) {
                is com.nexus.porsuk.core.common.NetworkResult.Success -> {
                    val observations = res.data?.observations ?: emptyList()
                    val entities = observations.mapNotNull { obs ->
                        obs.value.toDoubleOrNull()?.let { MacroDataEntity(seriesId, obs.date, it) }
                    }
                    assetDao.insertMacroData(entities)
                }
                else -> {}
            }
        }
    }

    fun getMacroData(seriesId: String): Flow<List<MacroDataEntity>> = assetDao.getMacroData(seriesId)

    suspend fun fetchConsolidatedPerformance(range: String): List<Double> {
        val assets = getConsolidatedAssetsFlow().first()
        if (assets.isEmpty()) return emptyList()
        val totalValuation = assets.sumOf { it.totalValue }
        val weights = assets.associateBy({ it.symbol }, { it.totalValue / totalValuation })
        val historicalResults = assets.map { asset ->
            val res = fetchHistoricalPrices(asset.symbol, asset.assetCategory.name, range, "1d")
            asset.symbol to (if (res is ScrapeResult.Success) res.data else emptyList<Double>())
        }
        val maxSize = historicalResults.maxOfOrNull { it.second.size } ?: 0
        if (maxSize == 0) return emptyList()
        val consolidatedLine = MutableList(maxSize) { 0.0 }
        historicalResults.forEach { (symbol, prices) ->
            val weight = weights[symbol] ?: 0.0
            for (i in 0 until maxSize) {
                val priceIdx = if (prices.size == maxSize) i else (i * prices.size / maxSize).coerceIn(0, prices.size - 1)
                val price = prices.getOrNull(priceIdx) ?: prices.lastOrNull() ?: 0.0
                consolidatedLine[i] += price * weight
            }
        }
        return consolidatedLine
    }

    suspend fun refreshExchangeRates() {
        val pairs = listOf(
            "USD" to "USDTRY",
            "EUR" to "EURTRY",
            "GBP" to "GBPTRY",
            "CHF" to "CHFTRY",
            "JPY" to "JPYTRY",
            "CAD" to "CADTRY",
            "AUD" to "AUDTRY"
        )
        val current = exchangeRates.value.toMutableMap()
        pairs.forEach { (code, sym) ->
            val res = yahooPublicService.fetchPrice(sym, "FOREX")
            if (res is ScrapeResult.Success && res.data.price > 0) {
                current[code] = res.data.price
                prices.update { it + (sym to res.data) }
            }
        }
        exchangeRates.value = current
    }

    suspend fun refreshPrice(symbol: String, market: String): ScrapeResult<PriceSnapshot> {
        if (fmpService != null) {
            val fmpResult = fmpService!!.fetchPrice(symbol, market)
            if (fmpResult is ScrapeResult.Success) {
                prices.update { it + (symbol to fmpResult.data) }
                return fmpResult
            }
        }
        val publicResult = yahooPublicService.fetchPrice(symbol, market)
        if (publicResult is ScrapeResult.Success) {
            prices.update { it + (symbol to publicResult.data) }
            return publicResult
        }
        val finalResult = scraper.fetchPrice(symbol, market)
        if (finalResult is ScrapeResult.Success) {
            eventBus?.publish(com.nexus.porsuk.core.common.PorsukEvent.PriceUpdated(symbol, finalResult.data.price, finalResult.data.changePercent))
        }
        return finalResult
    }

    suspend fun refreshCompanyInfo(symbol: String, market: String) {
        val existingInfo = assetDao.getCachedInfoDirect(symbol)
        var currentInfo = existingInfo ?: CachedCompanyInfo(symbol, "", null, null, null, null, null, null, null)
        if (fmpService != null) {
            val fmpRes = fmpService!!.fetchCompanyProfiles(listOf(symbol))
            if (fmpRes is ScrapeResult.Success && fmpRes.data.isNotEmpty()) currentInfo = mergeCompanyInfo(currentInfo, fmpRes.data.first())
        }
        val publicResult = yahooPublicService.fetchCompanyInfo(symbol, market)
        if (publicResult is ScrapeResult.Success) currentInfo = mergeCompanyInfo(currentInfo, publicResult.data)
        assetDao.insertCachedInfo(currentInfo)
    }

    suspend fun fetchHistoricalPrices(symbol: String, market: String, range: String, interval: String): ScrapeResult<List<Double>> {
        return yahooPublicService.fetchHistoricalPrices(symbol, market, range, interval)
    }

    private fun mergeCompanyInfo(existing: CachedCompanyInfo?, scraped: CachedCompanyInfo): CachedCompanyInfo {
        if (existing == null) return scraped
        return CachedCompanyInfo(
            symbol = scraped.symbol,
            about = if (scraped.about.isNotBlank()) scraped.about else existing.about,
            peRatio = scraped.peRatio ?: existing.peRatio,
            marketCap = if (!scraped.marketCap.isNullOrBlank()) scraped.marketCap else existing.marketCap,
            week52High = scraped.week52High ?: existing.week52High,
            week52Low = scraped.week52Low ?: existing.week52Low,
            dividendYield = scraped.dividendYield ?: existing.dividendYield,
            nextDividendDate = scraped.nextDividendDate ?: existing.nextDividendDate,
            volume = if (!scraped.volume.isNullOrBlank()) scraped.volume else existing.volume,
            lastUpdated = System.currentTimeMillis()
        )
    }

    fun getAllCachedInfo(): Flow<List<CachedCompanyInfo>> = assetDao.getAllCachedInfo()
    fun getCachedInfo(symbol: String): Flow<CachedCompanyInfo?> = assetDao.getCachedInfo(symbol)
    suspend fun insertCachedInfo(info: CachedCompanyInfo) = assetDao.insertCachedInfo(info)
    fun getNews(symbol: String): Flow<List<NewsItemEntity>> = assetDao.getNewsForStock(symbol)
    fun getIncomeStatements(symbol: String): Flow<List<IncomeStatementEntity>> = assetDao.getIncomeStatements(symbol)
    fun getBalanceSheets(symbol: String): Flow<List<BalanceSheetEntity>> = assetDao.getBalanceSheets(symbol)
    fun getCashFlows(symbol: String): Flow<List<CashFlowEntity>> = assetDao.getCashFlows(symbol)
    fun getCompanyRatios(symbol: String): Flow<List<CompanyRatioEntity>> = assetDao.getCompanyRatios(symbol)
    
    suspend fun getAiOracleData(symbol: String): Map<String, Any> {
        return mapOf(
            "income" to getIncomeStatements(symbol).first(),
            "balance" to getBalanceSheets(symbol).first(),
            "flows" to getCashFlows(symbol).first(),
            "ratios" to getCompanyRatios(symbol).first(),
            "price" to (prices.value[symbol]?.price ?: 0.0),
            "news" to getNews(symbol).first()
        )
    }

    suspend fun getMacroIndicators(): Map<String, String> {
        return mapOf(
            "TCMB_FAIZ" to "50.0%", "TCMB_ENFLASYON" to "71.6%", "FED_FAIZ" to "5.25-5.50%",
            "USD_TRY" to (exchangeRates.value["USD"]?.toString() ?: "34.5"),
            "EUR_TRY" to (exchangeRates.value["EUR"]?.toString() ?: "37.2")
        )
    }
    
    suspend fun getCompany(symbol: String): Company? = assetDao.getCompany(symbol)
    suspend fun getAllCompaniesDirect(): List<Company> = assetDao.getAllCompaniesDirect()
    
    fun getBasketById(basketId: Int): Flow<Basket?> = assetDao.getBasketById(basketId)
    suspend fun addBasket(basket: Basket): Long = assetDao.insertBasket(basket)
    suspend fun updateBasket(basket: Basket) = assetDao.updateBasket(basket)
    suspend fun deleteBasket(basket: Basket) = assetDao.deleteBasket(basket)
    
    fun getBasketItems(basketId: Int): Flow<List<BasketItem>> = assetDao.getItemsForBasket(basketId)
    suspend fun addBasketItem(item: BasketItem) = assetDao.insertBasketItem(item)
    suspend fun deleteBasketItem(item: BasketItem) = assetDao.deleteBasketItem(item)
    suspend fun getAllBasketItemsDirect(): List<BasketItem> = assetDao.getAllBasketItemsDirect()
    
    suspend fun addToWatchlist(symbol: String) = assetDao.insertWatchlistItem(WatchlistItem(symbol))
    suspend fun removeFromWatchlist(item: WatchlistItem) = assetDao.deleteWatchlistItem(item)

    suspend fun insertCompanies(companies: List<Company>) = assetDao.insertCompanies(companies)

    suspend fun insertPriceAlert(alert: PriceAlert) = assetDao.insertPriceAlert(alert)
    fun getAlertsForStock(symbol: String): Flow<List<PriceAlert>> = assetDao.getAlertsForStock(symbol)
    fun getAllPriceAlertsFlow(): Flow<List<PriceAlert>> = assetDao.getAllPriceAlertsFlow()
    suspend fun deletePriceAlert(alertId: Int) = assetDao.deletePriceAlert(alertId)

    fun getPortfolioHistory(): Flow<List<PortfolioHistoryEntry>> = assetDao.getPortfolioHistory()
    suspend fun insertPortfolioHistoryEntry(entry: PortfolioHistoryEntry) = assetDao.insertPortfolioHistoryEntry(entry)

    fun getStockHistory(symbol: String): Flow<List<StockHistoryEntry>> = assetDao.getStockHistory(symbol)
    suspend fun insertPriceHistoryEntry(symbol: String, price: Double) = assetDao.insertStockHistoryEntry(StockHistoryEntry(symbol = symbol, price = price))

    fun getAllTransactionsFlow(): Flow<List<com.nexus.porsuk.data.local.entity.PortfolioTransaction>> = assetDao.getAllTransactionsFlow()
    suspend fun deleteTransaction(transaction: com.nexus.porsuk.data.local.entity.PortfolioTransaction) = assetDao.deleteTransaction(transaction)

    suspend fun executeTransaction(basketId: Int, symbol: String, quantity: Double, price: Double, isBuy: Boolean) {
        val items = assetDao.getItemsForBasket(basketId).first()
        val existingItem = items.find { it.symbol.equals(symbol, ignoreCase = true) }
        var realizedPnL = 0.0
        if (isBuy) {
            if (existingItem != null) {
                val totalQty = existingItem.quantity + quantity
                val totalCost = (existingItem.buyPrice * existingItem.quantity) + (price * quantity)
                assetDao.insertBasketItem(existingItem.copy(quantity = totalQty, buyPrice = if (totalQty > 0) totalCost / totalQty else 0.0))
            } else {
                assetDao.insertBasketItem(BasketItem(basketId = basketId, symbol = symbol, quantity = quantity, buyPrice = price, buyDate = System.currentTimeMillis()))
            }
        } else {
            if (existingItem != null) {
                val sellQty = Math.min(existingItem.quantity, quantity)
                realizedPnL = (price - existingItem.buyPrice) * sellQty
                if (existingItem.quantity - sellQty <= 0) assetDao.deleteBasketItem(existingItem)
                else assetDao.insertBasketItem(existingItem.copy(quantity = existingItem.quantity - sellQty))
            }
        }
        assetDao.insertTransaction(com.nexus.porsuk.data.local.entity.PortfolioTransaction(basketId = basketId, symbol = symbol, quantity = quantity, price = price, isBuy = isBuy, realizedPnL = realizedPnL))
    }

    suspend fun getActivePriceAlerts(): List<com.nexus.porsuk.data.local.entity.PriceAlert> = assetDao.getActivePriceAlerts()
    suspend fun updatePriceAlert(alert: com.nexus.porsuk.data.local.entity.PriceAlert) = assetDao.updatePriceAlert(alert)

    suspend fun getTechnicalAnalysis(symbol: String, market: String): com.nexus.porsuk.data.remote.ScrapeResult<com.nexus.porsuk.data.model.TechnicalAnalysis> {
        return com.nexus.porsuk.data.remote.ScrapeResult.Success(com.nexus.porsuk.data.model.TechnicalAnalysis(58.4, null, null))
    }

    suspend fun updateTransaction(transaction: com.nexus.porsuk.data.local.entity.PortfolioTransaction) {
        assetDao.insertTransaction(transaction) // Simple update via insert (REPLACE)
    }

    suspend fun clearAllData() {
        assetDao.clearBaskets()
        assetDao.clearBasketItems()
        assetDao.clearWatchlist()
        assetDao.clearPriceHistory()
        assetDao.clearPortfolioHistory()
        assetDao.clearTransactions()
    }

    suspend fun refreshNews(symbol: String, market: String) {
        val result = scraper.fetchNews(symbol, market)
        if (result is ScrapeResult.Success) {
            assetDao.insertNews(result.data)
        }
    }

    suspend fun getAiOracleReport(
        symbol: String,
        price: Double,
        income: List<IncomeStatementEntity>,
        ratios: List<CompanyRatioEntity>
    ): com.nexus.porsuk.ui.orakul.OracleHisseReport {
        val apiKey = settingsManager?.getGeminiApiKey()?.takeIf { it.isNotBlank() }
            ?: throw Exception("Gemini API anahtarı bulunamadı")
        val service = GeminiService(apiKey)
        val reportJson = service.getAiOracleReport(symbol, price, income, ratios)
        
        val obj = org.json.JSONObject(reportJson)
        val swotObj = obj.optJSONObject("swot")
        val outlookObj = obj.optJSONObject("outlook")

        return com.nexus.porsuk.ui.orakul.OracleHisseReport(
            aiScore = obj.optInt("aiScore", 0),
            riskScore = obj.optInt("riskScore", 0),
            growthPotential = obj.optInt("growthPotential", 0),
            dividendScore = obj.optInt("dividendScore", 0),
            financialHealth = obj.optInt("financialHealth", 0),
            momentum = obj.optInt("momentum", 0),
            volatility = obj.optInt("volatility", 0),
            liquidity = obj.optInt("liquidity", 0),
            qualityScore = obj.optInt("qualityScore", 0),
            confidence = obj.optInt("confidence", 0),
            recommendation = obj.optString("recommendation", "HOLD"),
            fairValue = obj.optDouble("fairValue", 0.0),
            strengths = swotObj?.optJSONArray("strengths")?.let { arr -> List(arr.length()) { i -> arr.getString(i) } } ?: emptyList(),
            weaknesses = swotObj?.optJSONArray("weaknesses")?.let { arr -> List(arr.length()) { i -> arr.getString(i) } } ?: emptyList(),
            opportunities = swotObj?.optJSONArray("opportunities")?.let { arr -> List(arr.length()) { i -> arr.getString(i) } } ?: emptyList(),
            risks = swotObj?.optJSONArray("risks")?.let { arr -> List(arr.length()) { i -> arr.getString(i) } } ?: emptyList(),
            shortTermOutlook = outlookObj?.optString("shortTerm", "") ?: "",
            longTermOutlook = outlookObj?.optString("longTerm", "") ?: "",
            investmentThesis = obj.optString("investmentThesis", "")
        )
    }

    suspend fun getOrakulStream(prompt: String): Flow<String> {
        val apiKey = settingsManager?.getGeminiApiKey()?.takeIf { it.isNotBlank() }
            ?: throw Exception("Gemini API anahtarı bulunamadı")
        val service = GeminiService(apiKey)
        return service.getOrakulStream(prompt)
    }
}
