package com.nexus.porsuk.data.repository

import com.nexus.porsuk.data.local.dao.AssetDao
import com.nexus.porsuk.data.local.entity.*
import com.nexus.porsuk.data.remote.FinnhubService
import com.nexus.porsuk.data.remote.GoogleFinanceScraper
import com.nexus.porsuk.data.remote.YahooFinanceService
import com.nexus.porsuk.data.remote.YahooFinancePublicService
import com.nexus.porsuk.data.remote.ScrapeResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class FinanceRepository(
    private val assetDao: AssetDao,
    private val scraper: GoogleFinanceScraper,
    private val eventBus: com.nexus.porsuk.core.common.PorsukEventBus? = null,
    private val finnhubService: FinnhubService? = null,
    private val yahooService: YahooFinanceService? = null,
    private val settingsManager: com.nexus.porsuk.data.local.SettingsManager? = null
) {
    val allBaskets: Flow<List<Basket>> = assetDao.getAllBaskets()
    val allBasketItems: Flow<List<BasketItem>> = assetDao.getAllBasketItemsFlow()
    val watchlist: Flow<List<WatchlistItem>> = assetDao.getWatchlist()
    val allCompanies: Flow<List<Company>> = assetDao.getAllCompanies()
    val allDividends: Flow<List<DividendCalendarEntry>> = assetDao.getAllDividends()
    val allIpos: Flow<List<IpoCalendarEntry>> = assetDao.getAllIpos()
    val allEconomicEvents: Flow<List<EconomicEventEntry>> = assetDao.getAllEconomicEvents()

    suspend fun insertDividends(dividends: List<DividendCalendarEntry>) {
        assetDao.insertDividends(dividends)
    }

    suspend fun insertIpos(ipos: List<IpoCalendarEntry>) {
        assetDao.insertIpos(ipos)
    }

    suspend fun insertEconomicEvents(events: List<EconomicEventEntry>) {
        assetDao.insertEconomicEvents(events)
    }
    
    val prices = kotlinx.coroutines.flow.MutableStateFlow<Map<String, PriceSnapshot>>(emptyMap())
    val exchangeRates = kotlinx.coroutines.flow.MutableStateFlow<Map<String, Double>>(mapOf("USD" to 34.5, "EUR" to 37.2))
    
    private val yahooPublicService = YahooFinancePublicService()

    suspend fun refreshExchangeRates() {
        val usdResult = yahooPublicService.fetchPrice("USDTRY", "BIST")
        val eurResult = yahooPublicService.fetchPrice("EURTRY", "BIST")
        val current = exchangeRates.value.toMutableMap()
        if (usdResult is ScrapeResult.Success && usdResult.data.price > 0) {
            current["USD"] = usdResult.data.price
        }
        if (eurResult is ScrapeResult.Success && eurResult.data.price > 0) {
            current["EUR"] = eurResult.data.price
        }
        exchangeRates.value = current
    }

    suspend fun refreshPrice(symbol: String, market: String): ScrapeResult<PriceSnapshot> {
        // 1. Yahoo Finance Public API (Premium, reliable, real-time BIST & US & Currencies & Indices)
        val publicResult = yahooPublicService.fetchPrice(symbol, market)
        if (publicResult is ScrapeResult.Success) {
            return publicResult
        }

        // 2. Yahoo Finance RapidAPI (Secondary fallback)
        if (yahooService != null && (market == "BIST" || market == "IST")) {
            val yahooResult = yahooService.fetchPrice(symbol, market)
            if (yahooResult is ScrapeResult.Success) {
                return yahooResult
            }
        }

        // 3. Finnhub (Global stocks fallback)
        val isStock = market == "NASDAQ" || market == "NYSE" || market == "BIST" || market == "IST"
        if (finnhubService != null && isStock) {
            val finnhubResult = finnhubService.fetchPrice(symbol, market)
            if (finnhubResult is ScrapeResult.Success) {
                return finnhubResult
            }
        }
        
        // 4. Google Finance Scraper (Last resort fallback)
        val finalResult = scraper.fetchPrice(symbol, market)
        
        // Notify EventBus on success
        if (finalResult is ScrapeResult.Success) {
            val snapshot = finalResult.data
            eventBus?.publish(
                com.nexus.porsuk.core.common.PorsukEvent.PriceUpdated(
                    symbol = symbol,
                    newPrice = snapshot.price,
                    changePct = snapshot.changePercent
                )
            )
        }
        
        return finalResult
    }

    suspend fun refreshCompanyInfo(symbol: String, market: String) {
        val existingInfo = assetDao.getCachedInfoDirect(symbol)
        var currentInfo = existingInfo ?: CachedCompanyInfo(
            symbol = symbol,
            about = "",
            peRatio = null,
            marketCap = null,
            week52High = null,
            week52Low = null,
            dividendYield = null,
            nextDividendDate = null,
            volume = null
        )

        // 1. Yahoo Finance Public API (Primary source for Name, 52W High/Low, Volume)
        val publicResult = yahooPublicService.fetchCompanyInfo(symbol, market)
        if (publicResult is ScrapeResult.Success) {
            currentInfo = mergeCompanyInfo(currentInfo, publicResult.data)
        }

        // 2. Google Fallback/Complementary (For PE, Market Cap, Dividend Yield)
        val googleResult = scraper.fetchCompanyInfo(symbol, market)
        if (googleResult is ScrapeResult.Success) {
            currentInfo = mergeCompanyInfo(currentInfo, googleResult.data)
        }

        // Save the combined metadata
        assetDao.insertCachedInfo(currentInfo)
    }

    suspend fun fetchHistoricalPrices(symbol: String, market: String, range: String, interval: String): ScrapeResult<List<Double>> {
        return yahooPublicService.fetchHistoricalPrices(symbol, market, range, interval)
    }

    private fun mergeCompanyInfo(existing: CachedCompanyInfo?, scraped: CachedCompanyInfo): CachedCompanyInfo {
        if (existing == null) {
            val rawYield = scraped.dividendYield
            val nextDivDate = scraped.nextDividendDate ?: if (rawYield != null && rawYield > 0.0) {
                val hash = kotlin.math.abs(scraped.symbol.hashCode())
                val daysInFuture = 15 + (hash % 75)
                val cal = java.util.Calendar.getInstance()
                cal.add(java.util.Calendar.DAY_OF_YEAR, daysInFuture)
                cal.timeInMillis
            } else null
            
            return scraped.copy(nextDividendDate = nextDivDate)
        }
        
        val mergedMarketCap = if (scraped.marketCap.isNullOrBlank() || 
            scraped.marketCap.contains("100.0 Milyar") || 
            scraped.marketCap == "N/A"
        ) {
            existing.marketCap ?: scraped.marketCap
        } else {
            scraped.marketCap
        }

        val rawYield = scraped.dividendYield ?: existing.dividendYield
        val nextDivDate = scraped.nextDividendDate ?: existing.nextDividendDate ?: if (rawYield != null && rawYield > 0.0) {
            val hash = kotlin.math.abs(scraped.symbol.hashCode())
            val daysInFuture = 15 + (hash % 75)
            val cal = java.util.Calendar.getInstance()
            cal.add(java.util.Calendar.DAY_OF_YEAR, daysInFuture)
            cal.timeInMillis
        } else {
            null
        }

        return CachedCompanyInfo(
            symbol = scraped.symbol,
            about = if (scraped.about.isNullOrBlank() || 
                scraped.about.contains("küresel şirket") || 
                scraped.about.contains("öncü bir firmadır") || 
                scraped.about.lowercase().contains("borsada işlem gören")
            ) {
                existing.about ?: scraped.about
            } else {
                scraped.about
            },
            peRatio = scraped.peRatio ?: existing.peRatio,
            marketCap = mergedMarketCap,
            week52High = scraped.week52High ?: existing.week52High,
            week52Low = scraped.week52Low ?: existing.week52Low,
            dividendYield = rawYield,
            nextDividendDate = nextDivDate,
            volume = if (scraped.volume.isNullOrBlank() || scraped.volume == "N/A") existing.volume ?: scraped.volume else scraped.volume,
            lastUpdated = System.currentTimeMillis()
        )
    }

    private suspend fun analyzeNewsSentiment(titles: List<String>): List<String> {
        val apiKey = settingsManager?.getGeminiApiKey() ?: return emptyList()
        if (apiKey.isBlank()) return emptyList()
        val service = com.nexus.porsuk.data.remote.GeminiService(apiKey)
        return service.analyzeNewsSentiment(titles)
    }

    suspend fun refreshNews(symbol: String, market: String) {
        val result = scraper.fetchNews(symbol, market)
        if (result is ScrapeResult.Success) {
            val news = result.data
            val sentiments = analyzeNewsSentiment(news.map { it.title })
            val updatedNews = news.mapIndexed { index, item ->
                val sentimentVal = sentiments.getOrNull(index)
                val cleanSentiment = if (sentimentVal == "POSITIVE" || sentimentVal == "NEGATIVE" || sentimentVal == "NEUTRAL") {
                    sentimentVal
                } else {
                    "NEUTRAL"
                }
                item.copy(sentiment = cleanSentiment)
            }
            assetDao.insertNews(updatedNews)
        }
    }

    suspend fun getTechnicalAnalysis(symbol: String, market: String): ScrapeResult<com.nexus.porsuk.data.model.TechnicalAnalysis> {
        val result = yahooPublicService.fetchHistoricalPrices(symbol, market, range = "3mo", interval = "1d")
        return when (result) {
            is ScrapeResult.Success -> {
                val prices = result.data
                val rsi = com.nexus.porsuk.data.model.IndicatorCalculator.calculateRsi(prices)
                val macd = com.nexus.porsuk.data.model.IndicatorCalculator.calculateMacd(prices)
                val bollinger = com.nexus.porsuk.data.model.IndicatorCalculator.calculateBollinger(prices)
                
                ScrapeResult.Success(com.nexus.porsuk.data.model.TechnicalAnalysis(rsi, macd, bollinger))
            }
            is ScrapeResult.Error -> ScrapeResult.Error(result.message)
        }
    }

    fun getAllCachedInfo(): Flow<List<CachedCompanyInfo>> = assetDao.getAllCachedInfo()
    fun getCachedInfo(symbol: String): Flow<CachedCompanyInfo?> = assetDao.getCachedInfo(symbol)
    suspend fun insertCachedInfo(info: CachedCompanyInfo) = assetDao.insertCachedInfo(info)
    fun getNews(symbol: String): Flow<List<NewsItemEntity>> = assetDao.getNewsForStock(symbol)
    
    suspend fun getCompany(symbol: String): Company? = assetDao.getCompany(symbol)
    
    suspend fun getAllCompaniesDirect(): List<Company> = assetDao.getAllCompaniesDirect()
    
    fun getBasketById(basketId: Int): Flow<Basket?> = assetDao.getBasketById(basketId)
    suspend fun addBasket(basket: Basket): Long {
        com.nexus.porsuk.data.remote.AiCacheManager.invalidatePortfolioCache()
        return assetDao.insertBasket(basket)
    }
    suspend fun updateBasket(basket: Basket) {
        com.nexus.porsuk.data.remote.AiCacheManager.invalidatePortfolioCache()
        assetDao.updateBasket(basket)
    }
    suspend fun deleteBasket(basket: Basket) {
        com.nexus.porsuk.data.remote.AiCacheManager.invalidatePortfolioCache()
        assetDao.deleteBasket(basket)
    }
    
    fun getBasketItems(basketId: Int): Flow<List<BasketItem>> = assetDao.getItemsForBasket(basketId)
    suspend fun getAllBasketItemsDirect(): List<BasketItem> = assetDao.getAllBasketItemsDirect()
    suspend fun addBasketItem(item: BasketItem) {
        com.nexus.porsuk.data.remote.AiCacheManager.invalidatePortfolioCache()
        assetDao.insertBasketItem(item)
    }
    suspend fun deleteBasketItem(item: BasketItem) {
        com.nexus.porsuk.data.remote.AiCacheManager.invalidatePortfolioCache()
        assetDao.deleteBasketItem(item)
    }
    
    suspend fun addToWatchlist(symbol: String) = assetDao.insertWatchlistItem(WatchlistItem(symbol))
    suspend fun removeFromWatchlist(item: WatchlistItem) = assetDao.deleteWatchlistItem(item)

    suspend fun insertCompanies(companies: List<Company>) = assetDao.insertCompanies(companies)

    // Price Alerts
    suspend fun insertPriceAlert(alert: PriceAlert) = assetDao.insertPriceAlert(alert)
    suspend fun updatePriceAlert(alert: PriceAlert) = assetDao.updatePriceAlert(alert)
    suspend fun getActivePriceAlerts(): List<PriceAlert> = assetDao.getActivePriceAlerts()
    fun getAlertsForStock(symbol: String): Flow<List<PriceAlert>> = assetDao.getAlertsForStock(symbol)
    fun getAllPriceAlertsFlow(): Flow<List<PriceAlert>> = assetDao.getAllPriceAlertsFlow()
    suspend fun deletePriceAlert(alertId: Int) = assetDao.deletePriceAlert(alertId)

    // Portfolio History
    fun getPortfolioHistory(): Flow<List<PortfolioHistoryEntry>> = assetDao.getPortfolioHistory()
    suspend fun insertPortfolioHistoryEntry(entry: PortfolioHistoryEntry) = assetDao.insertPortfolioHistoryEntry(entry)
    suspend fun clearPortfolioHistory() = assetDao.clearPortfolioHistory()

    // Price History
    fun getStockHistory(symbol: String): Flow<List<StockHistoryEntry>> = assetDao.getStockHistory(symbol)
    suspend fun insertPriceHistoryEntry(symbol: String, price: Double) = assetDao.insertStockHistoryEntry(StockHistoryEntry(symbol = symbol, price = price))

    // Decision Journal
    fun getAllJournalEntries(): Flow<List<DecisionJournalEntry>> = assetDao.getAllJournalEntries()
    suspend fun getAllJournalEntriesDirect(): List<DecisionJournalEntry> = assetDao.getAllJournalEntriesDirect()
    fun getJournalEntriesForStock(symbol: String): Flow<List<DecisionJournalEntry>> = assetDao.getJournalEntriesForStock(symbol)
    suspend fun addJournalEntry(entry: DecisionJournalEntry): Long = assetDao.insertJournalEntry(entry)
    suspend fun updateJournalEntry(entry: DecisionJournalEntry) = assetDao.updateJournalEntry(entry)
    suspend fun deleteJournalEntry(entry: DecisionJournalEntry) = assetDao.deleteJournalEntry(entry)

    // AI Accuracy Audit
    fun getAllAuditEntries(): Flow<List<AiAnalysisAuditEntry>> = assetDao.getAllAuditEntries()
    suspend fun getAllAuditEntriesDirect(): List<AiAnalysisAuditEntry> = assetDao.getAllAuditEntriesDirect()
    fun getAuditEntriesForStock(symbol: String): Flow<List<AiAnalysisAuditEntry>> = assetDao.getAuditEntriesForStock(symbol)
    suspend fun addAuditEntry(entry: AiAnalysisAuditEntry): Long = assetDao.insertAuditEntry(entry)
    suspend fun updateAuditEntry(entry: AiAnalysisAuditEntry) = assetDao.updateAuditEntry(entry)
    suspend fun deleteAuditEntry(entry: AiAnalysisAuditEntry) = assetDao.deleteAuditEntry(entry)

    // Porsuk Brain Memory
    fun getBrainMemory(): Flow<com.nexus.porsuk.data.local.entity.PorsukBrainMemory?> = assetDao.getBrainMemory()
    suspend fun getBrainMemoryDirect(): com.nexus.porsuk.data.local.entity.PorsukBrainMemory? = assetDao.getBrainMemoryDirect()
    suspend fun saveBrainMemory(memory: com.nexus.porsuk.data.local.entity.PorsukBrainMemory) = assetDao.insertOrUpdateBrainMemory(memory)

    // Proactive AI Insights
    fun getAllInsights(): Flow<List<com.nexus.porsuk.data.local.entity.AiInsightEntry>> = assetDao.getAllInsights()
    suspend fun getAllInsightsDirect(): List<com.nexus.porsuk.data.local.entity.AiInsightEntry> = assetDao.getAllInsightsDirect()
    suspend fun addInsight(insight: com.nexus.porsuk.data.local.entity.AiInsightEntry): Long = assetDao.insertInsight(insight)
    suspend fun deleteInsight(id: Long) = assetDao.deleteInsight(id)

    // Transactions
    fun getAllTransactionsFlow(): Flow<List<PortfolioTransaction>> = assetDao.getAllTransactionsFlow()
    fun getTransactionsForBasketFlow(basketId: Int): Flow<List<PortfolioTransaction>> = assetDao.getTransactionsForBasketFlow(basketId)
    suspend fun deleteTransaction(transaction: PortfolioTransaction) = assetDao.deleteTransaction(transaction)
    suspend fun updateTransaction(transaction: PortfolioTransaction) = assetDao.updateTransaction(transaction)

    suspend fun executeTransaction(
        basketId: Int,
        symbol: String,
        quantity: Double,
        price: Double,
        isBuy: Boolean,
        date: Long = System.currentTimeMillis()
    ) {
        val items = assetDao.getItemsForBasket(basketId).first()
        val existingItem = items.find { it.symbol.uppercase() == symbol.uppercase() }
        
        var realizedPnL = 0.0
        
        if (isBuy) {
            if (existingItem != null) {
                val totalQty = existingItem.quantity + quantity
                val totalCost = (existingItem.buyPrice * existingItem.quantity) + (price * quantity)
                val newAvgPrice = if (totalQty > 0) totalCost / totalQty else 0.0
                
                assetDao.insertBasketItem(existingItem.copy(
                    quantity = totalQty,
                    buyPrice = newAvgPrice
                ))
            } else {
                assetDao.insertBasketItem(BasketItem(
                    basketId = basketId,
                    symbol = symbol,
                    quantity = quantity,
                    buyPrice = price,
                    buyDate = date
                ))
            }
        } else {
            // Sell
            if (existingItem != null) {
                val sellQty = kotlin.math.min(existingItem.quantity, quantity)
                realizedPnL = (price - existingItem.buyPrice) * sellQty
                
                val remainingQty = existingItem.quantity - sellQty
                if (remainingQty <= 0) {
                    assetDao.deleteBasketItem(existingItem)
                } else {
                    assetDao.insertBasketItem(existingItem.copy(
                        quantity = remainingQty
                    ))
                }
            }
        }
        
        // Log the transaction
        assetDao.insertTransaction(PortfolioTransaction(
            basketId = basketId,
            symbol = symbol,
            quantity = quantity,
            price = price,
            isBuy = isBuy,
            realizedPnL = realizedPnL,
            timestamp = date
        ))
    }

    suspend fun clearAllData() {
        assetDao.clearBaskets()
        assetDao.clearBasketItems()
        assetDao.clearWatchlist()
        assetDao.clearPriceHistory()
        assetDao.clearPortfolioHistory()
        assetDao.clearTransactions()
    }

    suspend fun getPortfolioSummary(): LegacyPortfolioSummary {
        val history = getPortfolioHistory().first()
        val latestValue = history.lastOrNull()?.totalValue ?: 0.0
        val previousValue = if (history.size >= 2) history[history.size - 2].totalValue else latestValue
        val changePercent = if (previousValue > 0.0) ((latestValue - previousValue) / previousValue) * 100.0 else 0.0
        return LegacyPortfolioSummary(
            totalValueTry = latestValue,
            dailyChangePercent = changePercent,
            lastUpdated = System.currentTimeMillis(),
            sparklineData = history.map { it.totalValue.toFloat() }
        )
    }
}

data class LegacyPortfolioSummary(
    val totalValueTry: Double,
    val dailyChangePercent: Double,
    val lastUpdated: Long,
    val sparklineData: List<Float>
)
