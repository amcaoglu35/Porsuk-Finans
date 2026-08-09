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
import com.nexus.porsuk.ui.dashboard.components.SmartInsightItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OpportunityUiModel(
    val code: String,
    val name: String,
    val price: String,
    val changePct: String,
    val signal: String,
    val isPositive: Boolean
)

data class LiveMarketUiModel(
    val title: String,
    val price: String,
    val change: String,
    val isPos: Boolean,
    val sparkValues: List<Float>,
    val isDataAvailable: Boolean = true
)

data class AiMarketSummaryUiModel(
    val comment: String = "Bankacılık ve savunma sektöründe pozitif görünüm devam ediyor. Portföy dengesi olumlu.",
    val marketScore: String = "78",
    val confidence: String = "%85",
    val riskLevel: String = "Düşük",
    val fearGreedIndex: String = "55 Nötr",
    val marketPulse: String = "68 Pozitif"
)

data class OracleGlowUiModel(
    val title: String = "Oracle Bugün Ne Diyor?",
    val prediction: String = "Piyasalarda pozitif momentum devam ediyor. 3 gün içinde yukarı yönlü hareket beklentisi %62.",
    val confidenceScore: String = "%87"
)

data class DashboardNewsUiModel(
    val title: String,
    val category: String,
    val impact: String,
    val aiConfidence: String,
    val readTime: String
)

data class BasketSummaryUiModel(
    val id: Int,
    val name: String,
    val itemCount: Int,
    val totalValue: Double,
    val market: String
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: FinanceRepository,
    private val settingsManager: SettingsManager,
    private val getPortfolioSummaryUseCase: GetPortfolioSummaryUseCase,
    private val opportunityEngine: com.nexus.porsuk.data.engine.OpportunitySignalEngine,
    private val fearGreedRepository: com.nexus.porsuk.data.repository.FearGreedRepository
) : ViewModel() {

    val watchlist: Flow<List<WatchlistItem>> = repository.watchlist
    val prices: StateFlow<Map<String, PriceSnapshot>> = repository.prices.asStateFlow()
    val allCompanies: Flow<List<Company>> = repository.allCompanies
    val numberFormat: StateFlow<String> = settingsManager.numberFormat
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "TR")
    val allPriceAlerts: Flow<List<PriceAlert>> = repository.getAllPriceAlertsFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    val myBaskets: StateFlow<List<BasketSummaryUiModel>> = combine(
        repository.allBaskets,
        repository.allBasketItems,
        prices
    ) { baskets, allItems, priceMap ->
        baskets.map { basket ->
            val items = allItems.filter { it.basketId == basket.id }
            val totalValue = items.sumOf { item ->
                val currentPrice = priceMap[item.symbol]?.price ?: item.buyPrice
                item.quantity * currentPrice
            }
            BasketSummaryUiModel(
                id = basket.id,
                name = basket.name,
                itemCount = items.size,
                totalValue = totalValue,
                market = basket.market
            )
        }.take(5)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val fearGreedFlow: StateFlow<com.nexus.porsuk.domain.model.FearGreedModel?> = combine(
        prices,
        allCompanies
    ) { priceMap, companies ->
        try {
            fearGreedRepository.calculateFearGreed(priceMap, companies)
        } catch (_: Exception) {
            null
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

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

    val totalGainValue: StateFlow<Double> = portfolioSummary
        .map { it.totalBalance - it.totalCost }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalGainPercent: StateFlow<Double> = portfolioSummary
        .map { if (it.totalCost > 0) ((it.totalBalance - it.totalCost) / it.totalCost) * 100.0 else 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val riskScore: StateFlow<Int> = portfolioSummary
        .map { if (it.totalBalance > 0) 68 else 0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 68)

    val aiHealthScore: StateFlow<Int> = portfolioSummary
        .map { if (it.totalBalance > 0) 85 else 0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 85)

    // Dynamic AI Smart Insights calculated from real portfolio and price data
    val insights: StateFlow<List<SmartInsightItem>> = combine(
        portfolioSummary,
        repository.getConsolidatedAssetsFlow(),
        allCompanies,
        prices
    ) { summary, items, companies, priceMap ->
        val list = mutableListOf<SmartInsightItem>()

        if (items.isNotEmpty()) {
            val companyMap = companies.associateBy { it.symbol }
            val totalValue = summary.totalBalance

            // 1. Sector concentration insight
            val sectorTotals = mutableMapOf<String, Double>()
            items.forEach { item ->
                val comp = companyMap[item.symbol]
                val sector = comp?.sector ?: "Diğer"
                val itemVal = item.quantity * (priceMap[item.symbol]?.price ?: item.currentPrice)
                sectorTotals[sector] = (sectorTotals[sector] ?: 0.0) + itemVal
            }

            val topSector = sectorTotals.maxByOrNull { it.value }
            if (topSector != null && totalValue > 0) {
                val pct = ((topSector.value / totalValue) * 100).toInt()
                list.add(
                    SmartInsightItem(
                        title = "${topSector.key} Sektör Yoğunlaşması",
                        summary = "Portföyün %${pct}'si ${topSector.key} sektöründe yoğunlaşmış.",
                        category = "Portföy",
                        icon = "⚡",
                        impactScore = if (pct > 35) "Yüksek" else "Normal",
                        fullExplanation = "Portföyünüzün toplam varlık dağılımında ${topSector.key} sektörü %${pct} paya sahiptir.",
                        aiCommentary = "Sektörel konsantrasyon ilgili sektör hareketlerinde portföy hassasiyetini artırır.",
                        riskAnalysis = "Sektör yoğunlaşma oranı: %${pct}.",
                        scenarioBullish = "${topSector.key} sektör rallisinde portföy getirisi artış gösterir.",
                        scenarioBearish = "${topSector.key} sektöründeki geri çekilmeler genel portföyü etkileyebilir.",
                        recommendedAction = "Portföyünüzü farklı sektörlerle dengeleyerek riski dağıtın."
                    )
                )
            }

            // 2. Real portfolio gain/loss insight
            val gainPct = if (summary.totalCost > 0) ((summary.totalBalance - summary.totalCost) / summary.totalCost) * 100 else 0.0
            val gainFormatted = String.format(java.util.Locale.US, "%.2f", gainPct)
            val isPos = gainPct >= 0
            list.add(
                SmartInsightItem(
                    title = if (isPos) "Portföy Performansı Pozitif" else "Portföy Performans Takibi",
                    summary = "Toplam portföy kar/zarar oranı: %${gainFormatted}.",
                    category = "Risk",
                    icon = "🛡️",
                    impactScore = if (Math.abs(gainPct) > 10) "Yüksek" else "Orta",
                    fullExplanation = "Mevcut varlıklarınızın maliyet ve güncel fiyat karşılaştırmasına göre kar/zarar durumunuz %${gainFormatted} seviyesindedir.",
                    aiCommentary = if (isPos) "Pozitif trend korunuyor, kar realizasyonu seviyeleri takip edilebilir." else "Maliyet düşürme veya stop-loss stratejileri değerlendirilebilir.",
                    riskAnalysis = "Portföy toplam maliyeti: ₺${String.format(java.util.Locale.US, "%.2f", summary.totalCost)}",
                    scenarioBullish = "Mevcut trend devam ederse yıllık bazda getiri potansiyeli artar.",
                    scenarioBearish = "Piyasa düşüşlerinde destek seviyeleri izlenmelidir.",
                    recommendedAction = "Düzenli bakiye kontrolü yapın ve risk limitlerinizi koruyun."
                )
            )

            // 3. Top mover holding insight
            val topMover = items.maxByOrNull { item ->
                val snap = priceMap[item.symbol]
                snap?.changePercent ?: 0.0
            }
            if (topMover != null) {
                val snap = priceMap[topMover.symbol]
                val changePct = snap?.changePercent ?: 0.0
                val changeFormatted = String.format(java.util.Locale.US, "%.2f", changePct)
                list.add(
                    SmartInsightItem(
                        title = "${topMover.symbol} Varlık Hareketi",
                        summary = "${topMover.symbol} bugün %${changeFormatted} değişim kaydetti.",
                        category = "Haber",
                        icon = "🚀",
                        impactScore = "Orta",
                        fullExplanation = "${topMover.symbol} hissesinin güncel fiyatı ₺${String.format(java.util.Locale.US, "%.2f", snap?.price ?: topMover.currentPrice)} seviyesindedir.",
                        aiCommentary = "Hissedeki fiyat hareketleri hacim ve piyasa genel trendi ile uyumludur.",
                        riskAnalysis = "Günlük değişim oranı: %${changeFormatted}.",
                        scenarioBullish = "Fiyat momentumunun korunması durumunda direnç noktaları test edilebilir.",
                        scenarioBearish = "Düzeltme hareketlerinde ana destek seviyeleri önem kazanır.",
                        recommendedAction = "Hisse haberlerini ve bilanço takvimini takip edin."
                    )
                )
            }

            // 4. Asset count & diversification insight
            list.add(
                SmartInsightItem(
                    title = "Varlık Çeşitliliği",
                    summary = "Portföyünüzde ${items.size} farklı varlık bulunuyor.",
                    category = "Temettü",
                    icon = "💰",
                    impactScore = "Fırsat",
                    fullExplanation = "Portföyünüz ${items.size} farklı enstrümandan oluşmaktadır.",
                    aiCommentary = "Varlık sayısının optimal seviyede tutulması portföy takibini kolaylaştırır.",
                    riskAnalysis = "Varlık sayısı: ${items.size}.",
                    scenarioBullish = "Çeşitlendirilmiş varlıklar piyasa dalgalanmalarına karşı koruma sağlar.",
                    scenarioBearish = "Tekil varlık riskleri toplam portföye sınırlı yansır.",
                    recommendedAction = "Aylık bazda varlık ağırlıklarını gözden geçirin."
                )
            )
        }

        list
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val aiMarketSummary: StateFlow<AiMarketSummaryUiModel> = combine(
        prices,
        portfolioSummary,
        fearGreedFlow
    ) { priceMap, summary, fearGreed ->
        val bistSnap = priceMap["BIST100"] ?: priceMap["XU100"]
        val bistChange = bistSnap?.changePercent ?: 0.0
        val isBistPos = bistChange >= 0

        val gainPct = if (summary.totalCost > 0) ((summary.totalBalance - summary.totalCost) / summary.totalCost) * 100 else 0.0

        val score = (70 + (bistChange * 2).toInt()).coerceIn(30, 95)
        val confidenceVal = if (summary.totalBalance > 0) 85 else 60
        val riskText = if (gainPct < -5) "Yüksek" else if (gainPct < 0) "Orta" else "Düşük"
        
        val fearGreedText = fearGreed?.let {
            "${it.score} ${it.label}"
        } ?: (if (bistChange > 1.0) "65 Açgözlü" else if (bistChange < -1.0) "35 Korku" else "50 Nötr")

        val marketPulseText = if (isBistPos) "${(60 + bistChange * 5).toInt().coerceIn(40, 90)} Pozitif" else "${(40 + bistChange * 5).toInt().coerceIn(10, 50)} Negatif"

        val comment = if (isBistPos) {
            "BİST-100 endeksi %${String.format(java.util.Locale.US, "%.2f", bistChange)} yükselişle pozitif seyrini sürdürüyor. Portföy dengesi korunduğu gözleniyor."
        } else {
            "Piyasalarda %${String.format(java.util.Locale.US, "%.2f", Math.abs(bistChange))} oranında geri çekilme hakim. Portföy risk marjları takip edilmelidir."
        }

        AiMarketSummaryUiModel(
            comment = comment,
            marketScore = score.toString(),
            confidence = "%${confidenceVal}",
            riskLevel = riskText,
            fearGreedIndex = fearGreedText,
            marketPulse = marketPulseText
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AiMarketSummaryUiModel())

    val oracleGlow: StateFlow<OracleGlowUiModel> = prices.map { priceMap ->
        val bistSnap = priceMap["BIST100"] ?: priceMap["XU100"]
        val bistChange = bistSnap?.changePercent ?: 0.0
        val isBistPos = bistChange >= 0

        val pred = if (isBistPos) {
            "Piyasalarda yukarı yönlü pozitif momentum hakim. Kısa vadeli yükseliş beklentisi %${(60 + bistChange * 3).toInt().coerceIn(50, 90)}."
        } else {
            "Piyasalarda yatay-aşağı yönlü konsolidasyon süreci gözleniyor. Destek seviyelerinin korunması kritik."
        }
        val conf = "%${(75 + Math.abs(bistChange) * 2).toInt().coerceIn(70, 95)}"

        OracleGlowUiModel(
            title = "Oracle Piyasa Analizi",
            prediction = pred,
            confidenceScore = conf
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), OracleGlowUiModel())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val opportunities: StateFlow<List<OpportunityUiModel>> = prices
        .mapLatest { priceMap ->
            val stockList = listOf(
                Triple("ASELS", "Aselsan", "BIST"),
                Triple("THYAO", "Türk Hava Yolları", "BIST"),
                Triple("KCHOL", "Koç Holding", "BIST"),
                Triple("AKBNK", "Akbank", "BIST")
            )
            
            stockList.map { (symbol, name, market) ->
                val signal = opportunityEngine.getSignalForStock(symbol, market)
                val snap = priceMap[symbol]
                
                val isPos = snap?.let { it.changePercent >= 0 } ?: signal.isPositive
                val formattedChange = snap?.let {
                    if (it.changePercent >= 0) "%${String.format(java.util.Locale.US, "%.2f", it.changePercent)}"
                    else "-%${String.format(java.util.Locale.US, "%.2f", Math.abs(it.changePercent))}"
                } ?: "%0.00"

                OpportunityUiModel(
                    code = symbol,
                    name = name,
                    price = if (snap != null) "₺${String.format(java.util.Locale.US, "%.2f", snap.price)}" else "₺0.00",
                    changePct = formattedChange,
                    signal = signal.recommendation,
                    isPositive = isPos
                )
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val liveMarkets: StateFlow<List<LiveMarketUiModel>> = prices.map { priceMap ->
        val bistSnap = priceMap["BIST100"] ?: priceMap["XU100"]
        val usdSnap = priceMap["USD/TRY"] ?: priceMap["USDTRY"]
        val eurSnap = priceMap["EUR/USD"] ?: priceMap["EURUSD"]
        val goldSnap = priceMap["ALTIN/GR"] ?: priceMap["GAUTRY"]
        val brentSnap = priceMap["BRENT"]
        val btcSnap = priceMap["BITCOIN"] ?: priceMap["BTCUSD"]

        fun formatMarket(title: String, defaultPrice: String, defaultChange: String, defaultPos: Boolean, snap: PriceSnapshot?): LiveMarketUiModel {
            if (snap == null) return LiveMarketUiModel(title, defaultPrice, defaultChange, defaultPos, listOf(40f, 42f, 45f, 48f, 50f), isDataAvailable = false)
            val isPos = snap.changePercent >= 0
            val changeStr = if (isPos) "%${String.format(java.util.Locale.US, "%.2f", snap.changePercent)}"
            else "-%${String.format(java.util.Locale.US, "%.2f", Math.abs(snap.changePercent))}"
            return LiveMarketUiModel(
                title = title,
                price = String.format(java.util.Locale.US, "%.2f", snap.price),
                change = changeStr,
                isPos = isPos,
                sparkValues = if (isPos) listOf(40f, 42f, 45f, 48f, 50f) else listOf(50f, 48f, 45f, 42f, 40f)
            )
        }

        listOf(
            formatMarket("BIST 100", "10.456,87", "%1,35", true, bistSnap),
            formatMarket("USD/TRY", "32,65", "%0,42", true, usdSnap),
            formatMarket("EUR/USD", "1,0850", "-%0,15", false, eurSnap),
            formatMarket("ALTIN/GR", "2.395,45", "%0,31", true, goldSnap),
            formatMarket("BRENT", "84,20", "%0,75", true, brentSnap),
            formatMarket("BITCOIN", "67.450,00", "%2,10", true, btcSnap)
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val newsList: StateFlow<List<DashboardNewsUiModel>> = flow {
        val defaultNews = listOf(
            DashboardNewsUiModel("BIST 100 rekor tazeledi: Bankacılık öncülüğünde yükseliş", "Piyasalar", "Yüksek Olumlu", "%89 Güven", "3 dk okuma"),
            DashboardNewsUiModel("Merkez Bankası faiz kararı metninde enflasyon vurgusu", "Makro", "Nötr Etki", "%92 Güven", "4 dk okuma")
        )
        try {
            val dbNews = repository.getNews("BIST").firstOrNull()
            if (!dbNews.isNullOrEmpty()) {
                val mapped = dbNews.take(4).map { n ->
                    DashboardNewsUiModel(
                        title = n.title,
                        category = "Piyasalar",
                        impact = "Olumlu",
                        aiConfidence = "%88 Güven",
                        readTime = "3 dk okuma"
                    )
                }
                emit(mapped)
            } else {
                emit(defaultNews)
            }
        } catch (_: Exception) {
            emit(defaultNews)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        refreshAllData()
    }

    fun toggleWatchlist(symbol: String) {
        viewModelScope.launch {
            try {
                val currentWatchlist = watchlist.firstOrNull() ?: emptyList()
                val item = currentWatchlist.find { it.symbol == symbol }
                if (item != null) {
                    repository.removeFromWatchlist(item)
                } else {
                    repository.addToWatchlist(symbol)
                }
            } catch (_: Exception) {
            }
        }
    }

    fun refreshAllData() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                repository.refreshExchangeRates()
                
                // Fetch VIX for Fear & Greed
                repository.refreshPrice("^VIX", "INDEX")

                // Fetch Opportunities Stocks
                listOf("ASELS", "THYAO", "KCHOL", "AKBNK").forEach {
                    repository.refreshPrice(it, "BIST")
                }
            } catch (_: Exception) {
            } finally {
                _isRefreshing.value = false
            }
        }
    }
}
