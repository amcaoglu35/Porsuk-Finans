package com.nexus.porsuk.feature.companydetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.data.repository.FinanceRepository
import com.nexus.porsuk.domain.model.AssetCategory
import com.nexus.porsuk.domain.model.MarketQuote
import com.nexus.porsuk.data.local.entity.NewsEntity
import com.nexus.porsuk.data.local.entity.NewsItemEntity
import com.nexus.porsuk.data.local.entity.IncomeStatementEntity
import com.nexus.porsuk.data.local.entity.BalanceSheetEntity
import com.nexus.porsuk.data.local.entity.CashFlowEntity
import com.nexus.porsuk.data.local.entity.CompanyRatioEntity
import com.nexus.porsuk.domain.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Porsuk Company Detail Module — ViewModel
 *
 * Şirket künyesini, canlı fiyatını, temettülerini, bilançolarını, haberlerini ve AI skor geçmişini yönetir.
 */
@HiltViewModel
class CompanyDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val companyRepository: CompanyRepository,
    private val marketRepository: MarketRepository,
    private val newsRepository: NewsRepository,
    private val aiHistoryRepository: AIHistoryRepository,
    private val watchlistRepository: WatchlistRepository,
    private val financeRepository: FinanceRepository,
    private val settingsManager: com.nexus.porsuk.data.local.SettingsManager
) : ViewModel() {

    private val symbol: String = checkNotNull(savedStateHandle["symbol"])
    private val market: String = savedStateHandle["market"] ?: "IST"

    private val _uiState = MutableStateFlow(CompanyDetailUiState(symbol = symbol))
    val uiState: StateFlow<CompanyDetailUiState> = _uiState.asStateFlow()

    val historicalPrices: StateFlow<List<Double>> = financeRepository.getStockHistory(symbol)
        .map { list -> 
            val pricesList = list.map { it.price }
            generateCandlestickData(pricesList)
            pricesList
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadCompanyDetailData()
        observeWatchlistStatus()
        fetchHistory()
    }

    fun setChartType(type: ChartType) {
        _uiState.update { it.copy(chartType = type) }
    }

    fun setTimeFrame(timeFrame: ChartTimeFrame) {
        _uiState.update { it.copy(selectedTimeFrame = timeFrame) }
        viewModelScope.launch {
            val range = when (timeFrame) {
                ChartTimeFrame.ONE_DAY -> "1d"
                ChartTimeFrame.ONE_WEEK -> "5d"
                ChartTimeFrame.ONE_MONTH -> "1mo"
                ChartTimeFrame.ONE_YEAR -> "1y"
                ChartTimeFrame.ALL -> "max"
            }
            financeRepository.fetchHistoricalPrices(symbol, market, range, "1d")
        }
    }

    private fun generateCandlestickData(prices: List<Double>) {
        if (prices.isEmpty()) return
        val candles = mutableListOf<CandleStickData>()
        val chunkSize = (prices.size / 20).coerceAtLeast(1)
        val chunks = prices.chunked(chunkSize)
        
        var baseTimestamp = System.currentTimeMillis() - (chunks.size * 86400000L)
        chunks.forEach { chunk ->
            val open = chunk.first()
            val close = chunk.last()
            val high = chunk.maxOrNull() ?: open
            val low = chunk.minOrNull() ?: open
            val volume = (high - low) * 1000 + (open * 50)
            candles.add(
                CandleStickData(
                    timestamp = baseTimestamp,
                    open = open,
                    high = high,
                    low = low,
                    close = close,
                    volume = volume
                )
            )
            baseTimestamp += 86400000L
        }
        _uiState.update { it.copy(candleStickList = candles) }
    }

    private fun fetchHistory() {
        viewModelScope.launch {
            financeRepository.fetchHistoricalPrices(symbol, "IST", "1mo", "1d")
        }
    }

    fun selectTab(tab: CompanyDetailTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            val isFav = _uiState.value.isFavorite
            if (isFav) {
                watchlistRepository.removeWatchlistItem(symbol)
            } else {
                watchlistRepository.addWatchlistItem(symbol)
            }
        }
    }

    private fun observeWatchlistStatus() {
        viewModelScope.launch {
            watchlistRepository.isInWatchlist(symbol).collect { isFav ->
                _uiState.update { it.copy(isFavorite = isFav) }
            }
        }
    }

    private fun loadCompanyDetailData() {
        viewModelScope.launch {
            val sdf = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
            val formattedTime = sdf.format(java.util.Date())
            _uiState.update { it.copy(isLoading = true, errorMessage = null, lastUpdatedFormatted = formattedTime) }

            // 1. Refresh real data from API and Save to Room
            launch {
                try {
                    financeRepository.refreshFullCompanyDetail(symbol)
                    financeRepository.refreshPrice(symbol, market)
                    _uiState.update { it.copy(isOffline = false) }
                } catch (e: Exception) {
                    _uiState.update { it.copy(
                        isOffline = true,
                        errorMessage = null, // don't block UI, show offline badge instead
                        isLoading = false
                    )}
                }
            }

            // 2. Observe Room data for UI
            launch {
                financeRepository.getIncomeStatements(symbol).collect { list ->
                    if (list.isNotEmpty()) {
                        val last = list.first()
                        _uiState.update { it.copy(
                            financialSummary = it.financialSummary.copy(
                                revenue = "${String.format(java.util.Locale.US, "%.1f", last.revenue / 1e6)} M",
                                netIncome = "${String.format(java.util.Locale.US, "%.1f", last.netIncome / 1e6)} M",
                                eps = String.format(java.util.Locale.US, "%.2f", last.eps),
                                ebitda = "${String.format(java.util.Locale.US, "%.1f", last.ebitda / 1e6)} M"
                            ),
                            quarterlyPerformance = list.map { QuarterlyBarData(it.date.take(7), it.revenue, it.ebitda, it.netIncome) }.reversed()
                        )}
                    }
                }
            }

            launch {
                financeRepository.getCompanyRatios(symbol).collect { list ->
                    if (list.isNotEmpty()) {
                        val last = list.first()
                        _uiState.update { it.copy(
                            quickMetrics = listOf(
                                QuickMetricItem("ROE", "%${String.format(java.util.Locale.US, "%.1f", last.roe * 100)}"),
                                QuickMetricItem("F/K", String.format(java.util.Locale.US, "%.1f", last.peRatio)),
                                QuickMetricItem("PD/DD", String.format(java.util.Locale.US, "%.1f", last.pbRatio)),
                                QuickMetricItem("Cari Oran", String.format(java.util.Locale.US, "%.1f", last.currentRatio)),
                                QuickMetricItem("Borç/Özkaynak", String.format(java.util.Locale.US, "%.1f", last.debtToEquity))
                            ),
                            financialHealth = it.financialHealth.copy(
                                liquidity = last.currentRatio,
                                leverage = last.debtToEquity,
                                currentRatio = last.currentRatio
                            )
                        )}
                    }
                }
            }

            launch {
                financeRepository.getNews(symbol).collect { list ->
                    _uiState.update { it.copy(news = list.map { e -> 
                        NewsEntity(
                            id = e.id.toLong(),
                            symbol = symbol,
                            title = e.title,
                            summary = e.summary ?: "",
                            source = e.source,
                            publishedAt = e.publishedAt,
                            url = e.url
                        ) 
                    }) }
                }
            }

            // 3. AI Oracle and Analysis
            launch {
                // Wait for financials to be available
                val income = financeRepository.getIncomeStatements(symbol).first()
                val balance = financeRepository.getBalanceSheets(symbol).first()
                val flows = financeRepository.getCashFlows(symbol).first()
                val ratios = financeRepository.getCompanyRatios(symbol).first()
                val currentPrices = financeRepository.prices.value
                val price = currentPrices[symbol]?.price ?: 0.0

                if (income.isNotEmpty()) {
                    val apiKey = settingsManager.getGeminiApiKey()
                    if (!apiKey.isNullOrBlank()) {
                        val service = com.nexus.porsuk.data.remote.GeminiService(apiKey)
                        
                        // Detailed Analysis
                        val analysis = service.getDetailedCompanyAnalysis(symbol, income, balance, flows, ratios)
                        _uiState.update { it.copy(aiSummary = analysis) }

                        // AI Oracle Report
                        val oracleJson = service.getAiOracleReport(symbol, price, income, ratios)
                        try {
                            val obj = org.json.JSONObject(oracleJson)
                            _uiState.update { it.copy(
                                aiOracleReport = AiOracleReport(
                                    aiScore = obj.optInt("aiScore", 0),
                                    riskScore = obj.optInt("riskScore", 0),
                                    confidence = obj.optInt("confidence", 0),
                                    fairValue = obj.optDouble("fairValue", 0.0),
                                    recommendation = obj.optString("recommendation", "HOLD"),
                                    investmentThesis = obj.optString("investmentThesis", "")
                                )
                            )}
                        } catch (e: Exception) {}
                    }
                }
            }

            // 4. Observe Financials and calculate Valuation, Quality, and Risk scorecards
            launch {
                combine(
                    financeRepository.getCompanyRatios(symbol),
                    financeRepository.getIncomeStatements(symbol),
                    financeRepository.getBalanceSheets(symbol),
                    financeRepository.getCashFlows(symbol),
                    historicalPrices
                ) { ratios, incomes, balances, flows, prices ->
                    val lastRatio = ratios.firstOrNull()
                    val lastIncome = incomes.firstOrNull()
                    val lastBalance = balances.firstOrNull()
                    val lastFlow = flows.firstOrNull()
                    val currentPrices = financeRepository.prices.value
                    val price = currentPrices[symbol]?.price ?: (prices.lastOrNull() ?: 0.0)

                    val valuation = calculateValuationModules(lastRatio, incomes, lastBalance, price)
                    val quality = calculateQualityModules(lastRatio, lastIncome, lastBalance)
                    val risk = calculateRiskModules(lastRatio, lastBalance, lastFlow, prices)

                    _uiState.update { state ->
                        state.copy(
                            valuationModules = valuation,
                            qualityModules = quality,
                            riskModules = risk
                        )
                    }
                }.collect()
            }

            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun calculateValuationModules(
        ratio: CompanyRatioEntity?,
        incomes: List<IncomeStatementEntity>,
        balance: BalanceSheetEntity?,
        price: Double
    ): List<ScoreCardData> {
        val lastIncome = incomes.firstOrNull()

        // 1. F/K (P/E Oranı)
        val peVal = when {
            ratio?.peRatio != null && ratio.peRatio != 0.0 -> ratio.peRatio
            price > 0 && lastIncome?.eps != null && lastIncome.eps > 0 -> price / lastIncome.eps
            else -> null
        }
        val peCard = if (peVal != null) {
            val formatted = String.format(java.util.Locale.US, "%.2f", peVal)
            when {
                peVal <= 0 -> ScoreCardData("F/K Oranı", formatted, 0.20, "Zarar Açıklıyor")
                peVal < 10.0 -> ScoreCardData("F/K Oranı", formatted, 0.90, "Cazip (Ucuz)")
                peVal <= 20.0 -> ScoreCardData("F/K Oranı", formatted, 0.65, "Makul")
                else -> ScoreCardData("F/K Oranı", formatted, 0.35, "Pahalı")
            }
        } else {
            ScoreCardData("F/K Oranı", "N/A", 0.0, "Veri Yok")
        }

        // 2. PD/DD (P/B Oranı)
        val pbVal = when {
            ratio?.pbRatio != null && ratio.pbRatio != 0.0 -> ratio.pbRatio
            else -> null
        }
        val pbCard = if (pbVal != null) {
            val formatted = String.format(java.util.Locale.US, "%.2f", pbVal)
            when {
                pbVal <= 0 -> ScoreCardData("PD/DD Oranı", formatted, 0.15, "Negatif Özkaynak")
                pbVal < 1.5 -> ScoreCardData("PD/DD Oranı", formatted, 0.90, "Cazip")
                pbVal <= 3.5 -> ScoreCardData("PD/DD Oranı", formatted, 0.65, "Dengeli")
                else -> ScoreCardData("PD/DD Oranı", formatted, 0.35, "Yüksek")
            }
        } else {
            ScoreCardData("PD/DD Oranı", "N/A", 0.0, "Veri Yok")
        }

        // 3. PEG Oranı (F/K / Yıllık Büyüme %)
        val pegVal = if (peVal != null && peVal > 0 && incomes.size >= 2) {
            val currentIncome = incomes[0].netIncome
            val prevIncome = incomes[1].netIncome
            if (prevIncome > 0 && currentIncome > prevIncome) {
                val growthPercent = ((currentIncome - prevIncome) / prevIncome) * 100.0
                if (growthPercent > 0) peVal / growthPercent else null
            } else null
        } else null

        val pegCard = if (pegVal != null) {
            val formatted = String.format(java.util.Locale.US, "%.2f", pegVal)
            when {
                pegVal < 1.0 -> ScoreCardData("PEG Oranı", formatted, 0.90, "Cazip Büyüme")
                pegVal <= 2.0 -> ScoreCardData("PEG Oranı", formatted, 0.60, "Makul Büyüme")
                else -> ScoreCardData("PEG Oranı", formatted, 0.35, "Yüksek PEG")
            }
        } else {
            ScoreCardData("PEG Oranı", "N/A", 0.0, "Veri Yok")
        }

        // 4. FD/FAVÖK (EV/EBITDA)
        val ebitdaVal = lastIncome?.ebitda
        val evEbitdaCard = if (ebitdaVal != null && ebitdaVal > 0) {
            val netDebt = balance?.netDebt ?: 0.0
            val approxEv = Math.max(0.0, netDebt) + (lastIncome.revenue * 0.8)
            val ratioVal = approxEv / ebitdaVal
            val formatted = String.format(java.util.Locale.US, "%.2f", ratioVal)
            when {
                ratioVal < 8.0 -> ScoreCardData("FD/FAVÖK", formatted, 0.85, "Cazip")
                ratioVal <= 15.0 -> ScoreCardData("FD/FAVÖK", formatted, 0.60, "Makul")
                else -> ScoreCardData("FD/FAVÖK", formatted, 0.35, "Yüksek")
            }
        } else {
            ScoreCardData("FD/FAVÖK", "N/A", 0.0, "Veri Yok")
        }

        return listOf(peCard, pbCard, pegCard, evEbitdaCard)
    }

    private fun calculateQualityModules(
        ratio: CompanyRatioEntity?,
        lastIncome: IncomeStatementEntity?,
        lastBalance: BalanceSheetEntity?
    ): List<ScoreCardData> {
        // 1. Özkaynak Kârlılığı (ROE)
        val roeVal = when {
            ratio?.roe != null && ratio.roe != 0.0 -> ratio.roe
            lastIncome != null && lastBalance != null && lastBalance.totalEquity > 0 -> (lastIncome.netIncome / lastBalance.totalEquity)
            else -> null
        }
        val roeCard = if (roeVal != null) {
            val pct = roeVal * 100.0
            val formatted = String.format(java.util.Locale.US, "%%%.1f", pct)
            when {
                pct >= 20.0 -> ScoreCardData("Özkaynak Kârlılığı (ROE)", formatted, 0.90, "Mükemmel")
                pct >= 10.0 -> ScoreCardData("Özkaynak Kârlılığı (ROE)", formatted, 0.70, "Güçlü")
                pct >= 0.0 -> ScoreCardData("Özkaynak Kârlılığı (ROE)", formatted, 0.40, "Zayıf")
                else -> ScoreCardData("Özkaynak Kârlılığı (ROE)", formatted, 0.15, "Negatif Kârlılık")
            }
        } else {
            ScoreCardData("Özkaynak Kârlılığı (ROE)", "N/A", 0.0, "Veri Yok")
        }

        // 2. Varlık Kârlılığı (ROA)
        val roaVal = when {
            ratio?.roa != null && ratio.roa != 0.0 -> ratio.roa
            lastIncome != null && lastBalance != null && lastBalance.totalAssets > 0 -> (lastIncome.netIncome / lastBalance.totalAssets)
            else -> null
        }
        val roaCard = if (roaVal != null) {
            val pct = roaVal * 100.0
            val formatted = String.format(java.util.Locale.US, "%%%.1f", pct)
            when {
                pct >= 10.0 -> ScoreCardData("Varlık Kârlılığı (ROA)", formatted, 0.85, "Yüksek")
                pct >= 5.0 -> ScoreCardData("Varlık Kârlılığı (ROA)", formatted, 0.65, "Makul")
                else -> ScoreCardData("Varlık Kârlılığı (ROA)", formatted, 0.35, "Düşük")
            }
        } else {
            ScoreCardData("Varlık Kârlılığı (ROA)", "N/A", 0.0, "Veri Yok")
        }

        // 3. Net Kâr Marjı
        val netMarginVal = if (lastIncome != null && lastIncome.revenue > 0) {
            (lastIncome.netIncome / lastIncome.revenue) * 100.0
        } else null

        val marginCard = if (netMarginVal != null) {
            val formatted = String.format(java.util.Locale.US, "%%%.1f", netMarginVal)
            when {
                netMarginVal >= 15.0 -> ScoreCardData("Net Kâr Marjı", formatted, 0.85, "Güçlü Marj")
                netMarginVal >= 5.0 -> ScoreCardData("Net Kâr Marjı", formatted, 0.60, "Orta Marj")
                else -> ScoreCardData("Net Kâr Marjı", formatted, 0.30, "Düşük Marj")
            }
        } else {
            ScoreCardData("Net Kâr Marjı", "N/A", 0.0, "Veri Yok")
        }

        // 4. Borç / Özkaynak (D/E)
        val deVal = when {
            ratio?.debtToEquity != null && ratio.debtToEquity != 0.0 -> ratio.debtToEquity
            lastBalance != null && lastBalance.totalEquity > 0 -> lastBalance.totalLiabilities / lastBalance.totalEquity
            else -> null
        }
        val deCard = if (deVal != null) {
            val formatted = String.format(java.util.Locale.US, "%.2f", deVal)
            when {
                deVal <= 0.8 -> ScoreCardData("Borç / Özkaynak", formatted, 0.90, "Sağlıklı Borç")
                deVal <= 1.5 -> ScoreCardData("Borç / Özkaynak", formatted, 0.60, "Kontrollü")
                else -> ScoreCardData("Borç / Özkaynak", formatted, 0.30, "Yüksek Kaldıraç")
            }
        } else {
            ScoreCardData("Borç / Özkaynak", "N/A", 0.0, "Veri Yok")
        }

        return listOf(roeCard, roaCard, marginCard, deCard)
    }

    private fun calculateRiskModules(
        ratio: CompanyRatioEntity?,
        balance: BalanceSheetEntity?,
        cashFlow: CashFlowEntity?,
        prices: List<Double>
    ): List<ScoreCardData> {
        // 1. Cari Oran (Current Ratio)
        val crVal = when {
            ratio?.currentRatio != null && ratio.currentRatio != 0.0 -> ratio.currentRatio
            else -> null
        }
        val crCard = if (crVal != null) {
            val formatted = String.format(java.util.Locale.US, "%.2f", crVal)
            when {
                crVal >= 1.5 -> ScoreCardData("Cari Oran (Likidite)", formatted, 0.85, "Güvenli Likidite")
                crVal >= 1.0 -> ScoreCardData("Cari Oran (Likidite)", formatted, 0.55, "Hassas Dengede")
                else -> ScoreCardData("Cari Oran (Likidite)", formatted, 0.25, "Likidite Riski")
            }
        } else {
            ScoreCardData("Cari Oran (Likidite)", "N/A", 0.0, "Veri Yok")
        }

        // 2. Borç / Varlık Oranı (Debt to Assets)
        val daVal = if (balance != null && balance.totalAssets > 0) {
            (balance.totalLiabilities / balance.totalAssets) * 100.0
        } else null

        val daCard = if (daVal != null) {
            val formatted = String.format(java.util.Locale.US, "%%%.1f", daVal)
            when {
                daVal <= 40.0 -> ScoreCardData("Borç / Varlık Oranı", formatted, 0.85, "Düşük Risk")
                daVal <= 70.0 -> ScoreCardData("Borç / Varlık Oranı", formatted, 0.60, "Orta Risk")
                else -> ScoreCardData("Borç / Varlık Oranı", formatted, 0.30, "Yüksek Risk")
            }
        } else {
            ScoreCardData("Borç / Varlık Oranı", "N/A", 0.0, "Veri Yok")
        }

        // 3. Fiyat Volatilitesi (Fiyat geçmişinden gerçek standart sapma)
        val volVal = if (prices.size >= 5) {
            val returns = mutableListOf<Double>()
            for (i in 1 until prices.size) {
                val prev = prices[i - 1]
                if (prev > 0) {
                    returns.add((prices[i] - prev) / prev)
                }
            }
            if (returns.isNotEmpty()) {
                val mean = returns.average()
                val variance = returns.sumOf { Math.pow(it - mean, 2.0) } / returns.size
                Math.sqrt(variance) * Math.sqrt(252.0) * 100.0
            } else null
        } else null

        val volCard = if (volVal != null) {
            val formatted = String.format(java.util.Locale.US, "%%%.1f", volVal)
            when {
                volVal < 25.0 -> ScoreCardData("Fiyat Oynaklığı (30G)", formatted, 0.85, "Düşük Volatilite")
                volVal <= 50.0 -> ScoreCardData("Fiyat Oynaklığı (30G)", formatted, 0.60, "Dengeli")
                else -> ScoreCardData("Fiyat Oynaklığı (30G)", formatted, 0.30, "Yüksek Oynaklık")
            }
        } else {
            ScoreCardData("Fiyat Oynaklığı (30G)", "N/A", 0.0, "Veri Yok")
        }

        // 4. Serbest Nakit Akış Gücü (FCF)
        val fcfVal = cashFlow?.freeCashFlow
        val fcfCard = if (fcfVal != null) {
            val formatted = "${String.format(java.util.Locale.US, "%.1f", fcfVal / 1e6)} M"
            if (fcfVal > 0) {
                ScoreCardData("Serbest Nakit Akışı", formatted, 0.85, "Pozitif Akış")
            } else {
                ScoreCardData("Serbest Nakit Akışı", formatted, 0.30, "Negatif Akış")
            }
        } else {
            ScoreCardData("Serbest Nakit Akışı", "N/A", 0.0, "Veri Yok")
        }

        return listOf(crCard, daCard, volCard, fcfCard)
    }
}
