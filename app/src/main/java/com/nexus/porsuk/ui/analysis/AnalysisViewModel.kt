package com.nexus.porsuk.ui.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.data.local.SettingsManager
import com.nexus.porsuk.data.local.entity.*
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
    val isSector: Boolean = false // ITEM 4
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
    val volatility: Double = 0.0
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
    // Faz 1 yenilikleri
    val riskMetrics: RiskMetrics = RiskMetrics(),
    val benchmarkChangePercent: Double = 0.0,     // BIST-100 karşılaştırması
    val benchmarkLabel: String = "BIST-100",
    val targetAllocation: Map<String, Float> = emptyMap(), // Hedef dağılım
    val dividendCalendar: List<DividendEntry> = emptyList(), // ITEM 3
    val realizedPnL: Double = 0.0,                // Satışlardan nakit kar/zarar
    val unrealizedPnL: Double = 0.0               // Bekleyen kağıt üzerindeki kar/zarar
)

class AnalysisViewModel(
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

    // Faz 1 — Benchmark ve Hedef Dağılım
    private val _benchmarkChangePct = MutableStateFlow(0.0)
    private val _targetAllocation = MutableStateFlow<Map<String, Float>>(emptyMap())
    val targetAllocation: StateFlow<Map<String, Float>> = _targetAllocation

    val numberFormat = settingsManager.numberFormat.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "TR")

    init {
        // DataStore'dan kaydedilmiş hedef dağılımı yükleme
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
        // Başlangıçta BIST-100 benchmark çek
        fetchBenchmark()
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
        repository.getAllTransactionsFlow()
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
        val history = args[6] as List<PortfolioHistoryEntry>
        @Suppress("UNCHECKED_CAST")
        val rates = args[7] as Map<String, Double>
        @Suppress("UNCHECKED_CAST")
        val allInfo = args[8] as List<CachedCompanyInfo>
        @Suppress("UNCHECKED_CAST")
        val transactions = args[9] as List<PortfolioTransaction>
        
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
        val random = java.util.Random(range.hashCode().toLong())
        val pointsCount = when (range) {
            PortfolioRange.WEEK -> 7
            PortfolioRange.MONTH -> 15
            PortfolioRange.THREE_MONTHS -> 30
            PortfolioRange.YEAR -> 60
            PortfolioRange.ALL -> 100
        }
        
        val simulatedHistory = mutableListOf<Float>()
        var currentVal = totalPortfolioValue.toFloat()
        if (currentVal <= 0f) {
            currentVal = 100000.0f
        }
        
        simulatedHistory.add(currentVal)
        
        val dailyVolatility = when (range) {
            PortfolioRange.WEEK -> 0.015f
            PortfolioRange.MONTH -> 0.025f
            PortfolioRange.THREE_MONTHS -> 0.04f
            PortfolioRange.YEAR -> 0.08f
            PortfolioRange.ALL -> 0.12f
        }

        val rangeTrend = when (range) {
            PortfolioRange.WEEK -> 0.005f
            PortfolioRange.MONTH -> 0.02f
            PortfolioRange.THREE_MONTHS -> 0.05f
            PortfolioRange.YEAR -> 0.15f
            PortfolioRange.ALL -> 0.25f
        }
        val stepTrend = rangeTrend / pointsCount

        for (i in 1 until pointsCount) {
            val changeFactor = 1f - stepTrend + (random.nextFloat() - 0.5f) * dailyVolatility
            currentVal *= changeFactor
            simulatedHistory.add(0, currentVal)
        }

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
            portfolioHistory = simulatedHistory,
            riskMetrics = calculateRiskMetrics(simulatedHistory),
            benchmarkChangePercent = _benchmarkChangePct.value,
            benchmarkLabel = "BIST-100",
            targetAllocation = _targetAllocation.value,
            dividendCalendar = dividendHoldings.sortedBy { it.date },
            realizedPnL = realizedPnL,
            unrealizedPnL = unrealizedPnL
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AnalysisUiState())

    fun onRangeSelected(range: PortfolioRange) {
        _selectedRange.value = range
    }

    // ─── Risk Metrikleri Hesaplaması ───────────────────────────────────────────
    private fun calculateRiskMetrics(history: List<Float>): RiskMetrics {
        if (history.size < 2) return RiskMetrics()
        val returns = history.zipWithNext { a, b -> if (a > 0f) (b - a) / a else 0f }
        val avg = returns.average()
        val variance = returns.map { (it - avg) * (it - avg) }.average()
        val volatility = kotlin.math.sqrt(variance) * 100.0
        val riskFreeRate = 0.0003 // günlük ~%0.03 (yıllık ~%10)
        val sharpe = if (volatility > 0) ((avg - riskFreeRate) / kotlin.math.sqrt(variance)) * kotlin.math.sqrt(252.0) else 0.0
        // Max Drawdown
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
            volatility = volatility
        )
    }

    // ─── Benchmark (BIST-100) Getirisi ───────────────────────────────────────
    private fun fetchBenchmark() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val client = okhttp3.OkHttpClient()
                val url = "https://query1.finance.yahoo.com/v8/finance/chart/XU100.IS?interval=1d&range=1d"
                val req = okhttp3.Request.Builder()
                    .url(url)
                    .header("User-Agent", "Mozilla/5.0")
                    .build()
                client.newCall(req).execute().use { resp ->
                    if (resp.isSuccessful) {
                        val body = resp.body?.string() ?: return@use
                        val json = org.json.JSONObject(body)
                        val meta = json.optJSONObject("chart")?.optJSONArray("result")
                            ?.optJSONObject(0)?.optJSONObject("meta")
                        val price = meta?.optDouble("regularMarketPrice", 0.0) ?: 0.0
                        val prev = meta?.optDouble("chartPreviousClose", 0.0) ?: 0.0
                        if (prev > 0.0) {
                            _benchmarkChangePct.value = (price - prev) / prev * 100.0
                        }
                    }
                }
            } catch (_: Exception) {}
        }
    }

    // ─── Hedef Dağılım Kaydetme ─────────────────────────────────────────────────
    fun saveTargetAllocation(allocation: Map<String, Float>) {
        _targetAllocation.value = allocation
        viewModelScope.launch {
            try {
                val obj = org.json.JSONObject()
                allocation.forEach { (k, v) -> obj.put(k, v.toDouble()) }
                settingsManager.setTargetAllocationJson(obj.toString())
            } catch (_: Exception) {}
        }
    }

    fun generateAiSummary() {
        val state = uiState.value
        if (!state.hasGeminiKey || state.basketCount == 0) return

        viewModelScope.launch {
            _isAiLoading.value = true
            try {
                val apiKey = settingsManager.getGeminiApiKey()!!
                val prompt = """
                    Sen bir "Borsa Profesörü" karakterisin. Kullanıcının portföy durumunu analiz et ve 1-2 cümlelik, samimi, esprili ve Türkçe bir özet yap.
                    Toplam Değer: ${state.totalPortfolioValue} TL
                    Toplam Değişim: %${state.totalChangePercent}
                    En İyi Performans: ${state.bestPerformer?.symbol} (%${state.bestPerformer?.changePercent})
                    Yatırım tavsiyesi verme.
                """.trimIndent()

                _aiSummary.value = generateContentWithFallback(apiKey, prompt)
            } catch (e: Exception) {
                _aiSummary.value = com.nexus.porsuk.ui.common.GeminiErrorParser.parse(e)
            } finally {
                _isAiLoading.value = false
            }
        }
    }

    fun generateAiRebalance() {
        val state = uiState.value
        if (!state.hasGeminiKey || state.basketCount == 0) return

        viewModelScope.launch {
            _isRebalanceLoading.value = true
            _aiRebalance.value = null
            try {
                val apiKey = settingsManager.getGeminiApiKey()!!

                val baskets = repository.allBaskets.first()
                val companies = repository.allCompanies.first()
                val companyMap = companies.associateBy { it.symbol }
                val allBasketItems = repository.getAllBasketItemsDirect()
                val pricesMap = repository.prices.value

                val portfolioText = StringBuilder()
                baskets.forEach { basket ->
                    portfolioText.append("- Sepet: ${basket.name} (${basket.market})\n")
                    val items = allBasketItems.filter { it.basketId == basket.id }
                    val currency = when (basket.market.uppercase()) {
                        "NASDAQ", "NYSE" -> "USD"
                        "FRA", "EURONEXT" -> "EUR"
                        else -> "TL"
                    }
                    items.forEach { item ->
                        val company = companyMap[item.symbol]
                        val currentPrice = pricesMap[item.symbol]?.price ?: company?.currentPrice ?: item.buyPrice
                        val valNative = item.quantity * currentPrice
                        portfolioText.append("  * Hisse: ${item.symbol}, Sektör: ${company?.sector ?: "Diğer"}, Adet: ${item.quantity}, Alış Fiyatı: ${item.buyPrice} $currency, Güncel Fiyat: $currentPrice $currency, Değer: $valNative $currency\n")
                    }
                }

                val prompt = """
                    ${com.nexus.porsuk.data.local.InvestmentKnowledgeBase.getClassicFormulas()}
                    
                    Sen son derece profesyonel, veri odaklı ve esprili bir "Borsa Profesörü" finansal danışmanısın.
                    Kullanıcının portföyündeki tüm sepetleri ve varlık dağılımını analiz et.
                    
                    Kullanıcının Portföy Verileri:
                    $portfolioText
                    
                    Görevin:
                    1. Portföyün sektörel ve bölgesel dağılım riskini değerlendir.
                    2. Buffett, Graham ve Lynch kriterlerine göre rebalans (dengeleme) önerisi sun. Hangi sektörlerin ağırlığı azaltılmalı, hangileri artırılmalı belirt.
                    3. Önerilerini ve analizlerini en ince ayrıntılarına kadar finansal rasyolarla, sektörel gerekçelerle son derece detaylı ve kapsamlı bir rapor halinde Türkçe olarak yaz. Yatırım tavsiyesi olmadığını (YTD) esprili ama profesyonel bir dille hatırlat.
                """.trimIndent()

                _aiRebalance.value = generateContentWithFallback(apiKey, prompt)
            } catch (e: Exception) {
                _aiRebalance.value = com.nexus.porsuk.ui.common.GeminiErrorParser.parse(e)
            } finally {
                _isRebalanceLoading.value = false
            }
        }
    }

    fun runStockScreener(strategyName: String, customCriteria: String = "") {
        viewModelScope.launch {
            _isScreenerLoading.value = true
            _screenerResult.value = null
            try {
                val apiKey = settingsManager.getGeminiApiKey()
                if (apiKey.isNullOrBlank()) {
                    _screenerResult.value = "Hata: Hisse eleği yapabilmek için lütfen önce Ayarlar sayfasından geçerli bir Gemini API anahtarı kaydedin."
                    _isScreenerLoading.value = false
                    return@launch
                }

                val companies = repository.allCompanies.first()
                val companyInfoList = mutableListOf<String>()
                
                for (company in companies.take(20)) {
                    val info = repository.getCachedInfo(company.symbol).first()
                    val currency = when (company.market.uppercase()) {
                        "NASDAQ", "NYSE" -> "USD"
                        "FRA", "EURONEXT" -> "EUR"
                        else -> "TL"
                    }
                    companyInfoList.add(
                        "Sembol: ${company.symbol}, Ad: ${company.name}, Sektör: ${company.sector}, Fiyat: ${company.currentPrice} $currency, F/K: ${info?.peRatio ?: "Bilinmiyor"}, 52H En Yüksek: ${info?.week52High ?: "Bilinmiyor"}, 52H En Düşük: ${info?.week52Low ?: "Bilinmiyor"}, Temettü Verimi: ${info?.dividendYield ?: "Bilinmiyor"}, Piyasa Değeri: ${info?.marketCap ?: "Bilinmiyor"}"
                    )
                }

                val companiesText = companyInfoList.joinToString("\n")
                
                val prompt = """
                    ${com.nexus.porsuk.data.local.InvestmentKnowledgeBase.getClassicFormulas()}
                    
                    Sen bir "Borsa Profesörü" finans analizcisisin. Aşağıdaki borsa hisselerini incele ve belirtilen filtre kriterlerine en çok uyan hisseleri belirle.
                    
                    Seçilen Strateji: $strategyName
                    Özel Kriter (varsa): $customCriteria
                    
                    Mevcut Hisse Bilgileri:
                    $companiesText
                    
                    Görevin:
                    1. Belirtilen kriterlere (Buffett, Graham, Lynch veya özel filtre) en çok uyan en fazla 3-4 hisseyi seç.
                    2. Seçtiğin her hissenin neden bu stratejiye uygun olduğunu yukarıdaki klasik formülleri veya temel verilerini referans alarak 1-2 cümleyle Türkçe açıkla.
                    3. Anlaşılır, yapılandırılmış bir markdown formatında listele. Yatırım tavsiyesi olmadığını (YTD) hatırlat.
                """.trimIndent()

                _screenerResult.value = generateContentWithFallback(apiKey, prompt)
            } catch (e: Exception) {
                _screenerResult.value = com.nexus.porsuk.ui.common.GeminiErrorParser.parse(e)
            } finally {
                _isScreenerLoading.value = false
            }
        }
    }

    fun generateInvestmentRecommendations() {
        val state = uiState.value
        if (!state.hasGeminiKey) return

        viewModelScope.launch {
            _isRecsLoading.value = true
            _aiRecommendations.value = null
            try {
                val apiKey = settingsManager.getGeminiApiKey()!!

                val companies = repository.allCompanies.first()
                val companyInfoList = mutableListOf<String>()
                
                for (company in companies.take(20)) {
                    val info = repository.getCachedInfo(company.symbol).first()
                    val currency = when (company.market.uppercase()) {
                        "NASDAQ", "NYSE" -> "USD"
                        "FRA", "EURONEXT" -> "EUR"
                        else -> "TL"
                    }
                    companyInfoList.add(
                        "Sembol: ${company.symbol}, Ad: ${company.name}, Sektör: ${company.sector}, Fiyat: ${company.currentPrice} $currency, F/K: ${info?.peRatio ?: "Bilinmiyor"}, Piyasa Değeri: ${info?.marketCap ?: "Bilinmiyor"}"
                    )
                }
                val companiesText = companyInfoList.joinToString("\n")

                val prompt = """
                    ${com.nexus.porsuk.data.local.InvestmentKnowledgeBase.getClassicFormulas()}
                    
                    Sen son derece tecrübeli ve zeki bir "Borsa Profesörü" yatırım danışmanısın.
                    Aşağıda kayıtlı olan hisseleri ve finansal durumlarını incele.
                    
                    Mevcut Hisse Bilgileri:
                    $companiesText
                    
                    Görevin:
                    1. Mevcut piyasa durumuna göre alım yapılması mantıklı olan 3 adet hisseyi "Hisse Önerileri" olarak seç. Her birinin neden seçildiğini Graham/Buffett prensiplerine veya temel rasyolarına dayandırarak Türkçe açıkla.
                    2. Kullanıcı için 1 adet örnek sepet (fon) tasarımı önerisi sun. Örn: "Yapay Zeka Devleri Sepeti" veya "Temettü Emekliliği Sepeti". İçine hangi hisselerden yaklaşık yüzde kaç oranında konulması gerektiğini belirt.
                    3. Bu önerileri son derece şık, maddeli ve yapılandırılmış bir markdown formatında yaz. Yatırım tavsiyesi olmadığını (YTD) esprili ama profesyonel bir dille hatırlat.
                """.trimIndent()

                _aiRecommendations.value = generateContentWithFallback(apiKey, prompt)
            } catch (e: Exception) {
                _aiRecommendations.value = com.nexus.porsuk.ui.common.GeminiErrorParser.parse(e)
            } finally {
                _isRecsLoading.value = false
            }
        }
    }

    private suspend fun generateContentWithFallback(apiKey: String, prompt: String): String {
        val models = com.nexus.porsuk.ui.common.GeminiModels.fallbackList
        var lastException: Exception? = null
        for (modelName in models) {
            try {
                val generativeModel = com.google.ai.client.generativeai.GenerativeModel(
                    modelName = modelName,
                    apiKey = apiKey
                )
                val response = generativeModel.generateContent(prompt)
                val text = response.text
                if (!text.isNullOrBlank()) {
                    return text
                }
            } catch (e: Exception) {
                lastException = e
            }
        }
        throw lastException ?: Exception("Modellerin hiçbirinden yanıt alınamadı.")
    }

    fun runPortfolioHealthCheck() {
        val apiKey = settingsManager.getGeminiApiKey()
        if (apiKey.isNullOrBlank()) {
            _portfolioHealthCheckResult.value = "Hata: Portföy sağlık taraması yapabilmek için lütfen öncelikle Ayarlar sayfasından geçerli bir Gemini API anahtarı kaydedin."
            return
        }
        viewModelScope.launch {
            _isHealthChecking.value = true
            _portfolioHealthCheckResult.value = ""
            try {
                val holdings = repository.getAllBasketItemsDirect()
                val companies = repository.allCompanies.first()
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
}
