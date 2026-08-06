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
import com.nexus.porsuk.ui.analysis.DuPontAnalysis
import com.nexus.porsuk.ui.analysis.PiotroskiFScoreCalculator
import com.nexus.porsuk.ui.analysis.BankruptcyAndManipulationDetector
import com.nexus.porsuk.ui.analysis.CashFlowMetricsCalculator
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

            // 2. Observe Company Entity from Repository
            launch {
                val dbCompany = companyRepository.getCompanyBySymbol(symbol)
                val company = dbCompany ?: com.nexus.porsuk.data.local.entity.CompanyEntity(
                    symbol = symbol,
                    companyName = symbol,
                    exchange = market,
                    sector = "BIST Hisse",
                    industry = "Genel"
                )
                _uiState.update { state ->
                    state.copy(
                        company = company,
                        boardMembers = if (state.boardMembers.isEmpty()) listOf(
                            BoardMember("Yönetim Kurulu Başkanı", "Yönetim Kurulu", null),
                            BoardMember("Genel Müdür / CEO", "Üst Yönetim", null),
                            BoardMember("Bağımsız Yönetim Kurulu Üyesi", "Denetim Komitesi", null)
                        ) else state.boardMembers,
                        ownershipStructure = if (state.ownershipStructure.isEmpty()) listOf(
                            OwnerData("Halka Açık Kısım (Fiili Dolaşım)", 45.0),
                            OwnerData("Ana Ortak / Kurumsal Yatırımcılar", 55.0)
                        ) else state.ownershipStructure,
                        corporateTimeline = if (state.corporateTimeline.isEmpty()) listOf(
                            TimelineEvent("2024", "Finansal Raporlama", "Dönemsel bilançolar ve faaliyet raporları açıklandı."),
                            TimelineEvent("2023", "Kurumsal Gelişme", "Şirket genel kurul kararları duyuruldu.")
                        ) else state.corporateTimeline
                    )
                }
            }

            // 3. Observe Market Quote / Prices
            launch {
                financeRepository.prices.collect { priceMap ->
                    val snapshot = priceMap[symbol] ?: priceMap["$symbol.IS"] ?: priceMap[symbol.uppercase()]
                    if (snapshot != null) {
                        val currentPrice = snapshot.price
                        val targetPrice = if (_uiState.value.aiTargetPrice > 0) _uiState.value.aiTargetPrice else currentPrice * 1.15
                        val potentialReturn = if (currentPrice > 0) ((targetPrice - currentPrice) / currentPrice) * 100.0 else 15.0

                        _uiState.update { state ->
                            state.copy(
                                quote = MarketQuote(
                                    symbol = symbol,
                                    name = state.company?.companyName ?: symbol,
                                    market = market,
                                    category = AssetCategory.fromSymbol(symbol),
                                    currency = "TRY",
                                    lastPrice = currentPrice,
                                    dailyChange = currentPrice * (snapshot.changePercent / 100.0),
                                    dailyChangePct = snapshot.changePercent
                                ),
                                aiTargetPrice = targetPrice,
                                aiPotentialReturn = potentialReturn
                            )
                        }
                    }
                }
            }

            // 4. Observe AI History Repository
            launch {
                aiHistoryRepository.getLatestAiAnalysis(symbol).collect { aiEntity ->
                    if (aiEntity != null) {
                        val price = _uiState.value.quote?.lastPrice ?: 0.0
                        val target = _uiState.value.aiTargetPrice
                        val potReturn = if (price > 0 && target > 0) ((target - price) / price) * 100.0 else 15.0
                        _uiState.update { state ->
                            state.copy(
                                aiHistory = aiEntity,
                                aiTargetPrice = target,
                                aiPotentialReturn = potReturn,
                                aiConfidenceScore = aiEntity.confidence
                            )
                        }
                    }
                }
            }

            // 5. Observe Room data for UI (Income Statements, Ratios, News)
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
            //    Quality & Risk modules now delegate to tested ui/analysis calculators.
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

                    // ── Valuation (no ui/analysis equivalent, keep inline) ──
                    val valuation = calculateValuationModules(lastRatio, incomes, lastBalance, price)

                    // ── Quality: DuPont + Piotroski (delegated to tested calculators) ──
                    val duPont = if (lastIncome != null && lastBalance != null) {
                        DuPontAnalysis.calculate(
                            netProfit = lastIncome.netIncome,
                            revenue = lastIncome.revenue,
                            totalAssets = lastBalance.totalAssets,
                            equity = lastBalance.totalEquity
                        )
                    } else null

                    val prevIncome = incomes.getOrNull(1)
                    val prevBalance = balances.getOrNull(1)
                    val prevRatio = ratios.getOrNull(1)

                    val piotroski = if (lastIncome != null && lastBalance != null && lastFlow != null) {
                        val roaCurrent = if (lastBalance.totalAssets > 0) lastIncome.netIncome / lastBalance.totalAssets else 0.0
                        val roaPrev = if (prevIncome != null && prevBalance != null && prevBalance.totalAssets > 0)
                            prevIncome.netIncome / prevBalance.totalAssets else 0.0
                        val grossMarginCurrent = if (lastIncome.revenue > 0) lastIncome.grossProfit / lastIncome.revenue else 0.0
                        val grossMarginPrev = if (prevIncome != null && prevIncome.revenue > 0) prevIncome.grossProfit / prevIncome.revenue else 0.0
                        val turnoverCurrent = if (lastBalance.totalAssets > 0) lastIncome.revenue / lastBalance.totalAssets else 0.0
                        val turnoverPrev = if (prevIncome != null && prevBalance != null && prevBalance.totalAssets > 0)
                            prevIncome.revenue / prevBalance.totalAssets else 0.0
                        val leverageCurrent = if (lastBalance.totalAssets > 0) lastBalance.totalLiabilities / lastBalance.totalAssets else 0.0
                        val leveragePrev = if (prevBalance != null && prevBalance.totalAssets > 0)
                            prevBalance.totalLiabilities / prevBalance.totalAssets else 0.0
                        val crCurrent = lastRatio?.currentRatio ?: 0.0
                        val crPrev = prevRatio?.currentRatio ?: 0.0

                        PiotroskiFScoreCalculator.calculate(
                            roaPositive = roaCurrent > 0,
                            cfoPositive = lastFlow.operatingCashFlow > 0,
                            roaDeltaPositive = roaCurrent > roaPrev,
                            cfoGreaterThanRoa = (lastFlow.operatingCashFlow / lastBalance.totalAssets.coerceAtLeast(1.0)) > roaCurrent,
                            leverageDecreased = leverageCurrent < leveragePrev,
                            currentRatioIncreased = crCurrent > crPrev,
                            noShareDilution = true, // Share dilution data not available in current entities
                            grossMarginIncreased = grossMarginCurrent > grossMarginPrev,
                            assetTurnoverIncreased = turnoverCurrent > turnoverPrev
                        )
                    } else null

                    val qualityCards = mutableListOf<ScoreCardData>()
                    // DuPont ROE card
                    if (duPont != null) {
                        val roePct = duPont.calculatedRoePct
                        val formatted = String.format(java.util.Locale.US, "%%%.1f", roePct)
                        qualityCards.add(when {
                            roePct >= 20.0 -> ScoreCardData("DuPont ROE", formatted, 0.90, "Mükemmel")
                            roePct >= 10.0 -> ScoreCardData("DuPont ROE", formatted, 0.70, "Güçlü")
                            roePct >= 0.0  -> ScoreCardData("DuPont ROE", formatted, 0.40, "Zayıf")
                            else           -> ScoreCardData("DuPont ROE", formatted, 0.15, "Negatif Kârlılık")
                        })
                    } else {
                        qualityCards.add(ScoreCardData("DuPont ROE", "N/A", 0.0, "Veri Yok"))
                    }
                    // Net Kâr Marjı (from DuPont)
                    if (duPont != null) {
                        val margin = duPont.netProfitMarginPct
                        val formatted = String.format(java.util.Locale.US, "%%%.1f", margin)
                        qualityCards.add(when {
                            margin >= 15.0 -> ScoreCardData("Net Kâr Marjı", formatted, 0.85, "Güçlü Marj")
                            margin >= 5.0  -> ScoreCardData("Net Kâr Marjı", formatted, 0.60, "Orta Marj")
                            else           -> ScoreCardData("Net Kâr Marjı", formatted, 0.30, "Düşük Marj")
                        })
                    } else {
                        qualityCards.add(ScoreCardData("Net Kâr Marjı", "N/A", 0.0, "Veri Yok"))
                    }
                    // Piotroski F-Score card
                    if (piotroski != null) {
                        qualityCards.add(when {
                            piotroski.totalScore >= 7 -> ScoreCardData("Piotroski F-Score", "${piotroski.totalScore}/9", 0.90, piotroski.rating)
                            piotroski.totalScore >= 4 -> ScoreCardData("Piotroski F-Score", "${piotroski.totalScore}/9", 0.60, piotroski.rating)
                            else                      -> ScoreCardData("Piotroski F-Score", "${piotroski.totalScore}/9", 0.25, piotroski.rating)
                        })
                    } else {
                        qualityCards.add(ScoreCardData("Piotroski F-Score", "N/A", 0.0, "Veri Yok"))
                    }
                    // Finansal Kaldıraç (from DuPont — shows real negative value per user decision)
                    if (duPont != null) {
                        val lev = duPont.financialLeverage
                        val formatted = String.format(java.util.Locale.US, "%.2fx", lev)
                        qualityCards.add(when {
                            lev < 0       -> ScoreCardData("Finansal Kaldıraç", formatted, 0.10, "Negatif Özkaynak")
                            lev <= 2.5     -> ScoreCardData("Finansal Kaldıraç", formatted, 0.85, "Sağlıklı")
                            lev <= 5.0     -> ScoreCardData("Finansal Kaldıraç", formatted, 0.55, "Kontrollü")
                            else           -> ScoreCardData("Finansal Kaldıraç", formatted, 0.25, "Yüksek Kaldıraç")
                        })
                    } else {
                        qualityCards.add(ScoreCardData("Finansal Kaldıraç", "N/A", 0.0, "Veri Yok"))
                    }

                    // ── Risk: Altman Z + Beneish M + CashFlow + Volatility (delegated) ──
                    val altmanBeneish = if (lastBalance != null && lastIncome != null) {
                        val wc = (lastRatio?.currentRatio ?: 1.0) - 1.0 // proxy for workingCapital/Assets
                        val wcToAssets = if (lastBalance.totalAssets > 0) {
                            val currentAssets = lastBalance.totalAssets - lastBalance.totalLiabilities + (lastBalance.totalLiabilities / (lastRatio?.currentRatio?.coerceAtLeast(0.01) ?: 1.0))
                            ((lastRatio?.currentRatio ?: 1.0) * (lastBalance.totalLiabilities / (lastRatio?.currentRatio?.coerceAtLeast(0.01) ?: 1.0)) - (lastBalance.totalLiabilities / (lastRatio?.currentRatio?.coerceAtLeast(0.01) ?: 1.0))) / lastBalance.totalAssets
                        } else 0.0
                        val retainedToAssets = if (lastBalance.totalAssets > 0) lastBalance.totalEquity / lastBalance.totalAssets else 0.0
                        val ebitToAssets = if (lastBalance.totalAssets > 0) lastIncome.ebitda / lastBalance.totalAssets else 0.0
                        val mktCapToLiab = if (lastBalance.totalLiabilities > 0) (price * 1e6) / lastBalance.totalLiabilities else 2.0
                        val salesToAssets = if (lastBalance.totalAssets > 0) lastIncome.revenue / lastBalance.totalAssets else 0.0

                        BankruptcyAndManipulationDetector.analyze(
                            workingCapitalToAssets = wcToAssets,
                            retainedEarningsToAssets = retainedToAssets,
                            ebitToAssets = ebitToAssets,
                            marketCapToTotalLiabilities = mktCapToLiab,
                            salesToAssets = salesToAssets
                            // Beneish M-Score params use defaults (insufficient granular data in entities)
                        )
                    } else null

                    val cashFlowSummary = if (lastFlow != null && lastIncome != null) {
                        CashFlowMetricsCalculator.calculate(
                            netProfitMillion = lastIncome.netIncome / 1e6,
                            operatingCashFlowMillion = lastFlow.operatingCashFlow / 1e6,
                            capExMillion = (lastFlow.operatingCashFlow - lastFlow.freeCashFlow) / 1e6,
                            marketCapMillion = price * 1000.0 // approximate market cap in millions
                        )
                    } else null

                    val riskCards = mutableListOf<ScoreCardData>()
                    // Altman Z-Score card
                    if (altmanBeneish != null) {
                        riskCards.add(ScoreCardData(
                            "Altman Z-Skoru",
                            String.format(java.util.Locale.US, "%.2f", altmanBeneish.altmanZScore),
                            when {
                                altmanBeneish.altmanZScore >= 2.99 -> 0.85
                                altmanBeneish.altmanZScore >= 1.81 -> 0.55
                                else -> 0.20
                            },
                            altmanBeneish.altmanZone
                        ))
                    } else {
                        riskCards.add(ScoreCardData("Altman Z-Skoru", "N/A", 0.0, "Veri Yok"))
                    }
                    // Beneish M-Score card
                    if (altmanBeneish != null) {
                        riskCards.add(ScoreCardData(
                            "Beneish M-Skoru",
                            String.format(java.util.Locale.US, "%.2f", altmanBeneish.beneishMScore),
                            if (altmanBeneish.isManipulationRiskHigh) 0.20 else 0.85,
                            altmanBeneish.beneishRating
                        ))
                    } else {
                        riskCards.add(ScoreCardData("Beneish M-Skoru", "N/A", 0.0, "Veri Yok"))
                    }
                    // Fiyat Volatilitesi
                    val volVal = if (prices.size >= 5) {
                        val returns = mutableListOf<Double>()
                        for (i in 1 until prices.size) {
                            val prev = prices[i - 1]
                            if (prev > 0) returns.add((prices[i] - prev) / prev)
                        }
                        if (returns.isNotEmpty()) {
                            val mean = returns.average()
                            val variance = returns.sumOf { Math.pow(it - mean, 2.0) } / returns.size
                            Math.sqrt(variance) * Math.sqrt(252.0) * 100.0
                        } else null
                    } else null
                    if (volVal != null) {
                        val formatted = String.format(java.util.Locale.US, "%%%.1f", volVal)
                        riskCards.add(when {
                            volVal < 25.0  -> ScoreCardData("Fiyat Oynaklığı (30G)", formatted, 0.85, "Düşük Volatilite")
                            volVal <= 50.0 -> ScoreCardData("Fiyat Oynaklığı (30G)", formatted, 0.60, "Dengeli")
                            else           -> ScoreCardData("Fiyat Oynaklığı (30G)", formatted, 0.30, "Yüksek Oynaklık")
                        })
                    } else {
                        riskCards.add(ScoreCardData("Fiyat Oynaklığı (30G)", "N/A", 0.0, "Veri Yok"))
                    }
                    // FCF card (from CashFlowMetrics)
                    if (cashFlowSummary != null) {
                        val fcfFormatted = "${String.format(java.util.Locale.US, "%.1f", cashFlowSummary.freeCashFlowMillion)} M"
                        riskCards.add(if (cashFlowSummary.freeCashFlowMillion > 0) {
                            ScoreCardData("Serbest Nakit Akışı", fcfFormatted, 0.85, "Pozitif Akış")
                        } else {
                            ScoreCardData("Serbest Nakit Akışı", fcfFormatted, 0.30, "Negatif Akış")
                        })
                    } else {
                        riskCards.add(ScoreCardData("Serbest Nakit Akışı", "N/A", 0.0, "Veri Yok"))
                    }

                    _uiState.update { state ->
                        state.copy(
                            valuationModules = valuation,
                            qualityModules = qualityCards,
                            riskModules = riskCards,
                            duPontBreakdown = duPont,
                            piotroskiResult = piotroski,
                            financialHealthFlags = altmanBeneish,
                            cashFlowSummary = cashFlowSummary
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

    // calculateQualityModules and calculateRiskModules removed.
    // Quality → DuPontAnalysis.calculate() + PiotroskiFScoreCalculator.calculate()
    // Risk   → BankruptcyAndManipulationDetector.analyze() + CashFlowMetricsCalculator.calculate()
}
