package com.nexus.porsuk.feature.companydetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.domain.model.AssetCategory
import com.nexus.porsuk.domain.model.MarketQuote
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
    private val watchlistRepository: WatchlistRepository
) : ViewModel() {

    private val symbol: String = checkNotNull(savedStateHandle["symbol"])

    private val _uiState = MutableStateFlow(CompanyDetailUiState(symbol = symbol))
    val uiState: StateFlow<CompanyDetailUiState> = _uiState.asStateFlow()

    init {
        loadCompanyDetailData()
        observeWatchlistStatus()
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
            _uiState.update { it.copy(isLoading = true) }

            // 1. Şirket Künyesi
            launch {
                val companyEntity = companyRepository.getCompanyBySymbol(symbol)
                _uiState.update { it.copy(company = companyEntity) }
            }

            // 2. Canlı / Son Fiyat Kartı Verisi
            launch {
                val quoteResult = marketRepository.refreshQuote(symbol)
                if (quoteResult is com.nexus.porsuk.core.common.NetworkResult.Success) {
                    _uiState.update { it.copy(quote = quoteResult.data) }
                } else {
                    _uiState.update {
                        it.copy(
                            quote = MarketQuote(
                                symbol = symbol,
                                name = symbol,
                                market = "BIST",
                                category = AssetCategory.fromSymbol(symbol),
                                currency = "TRY",
                                lastPrice = 285.50,
                                dailyChange = 4.50,
                                dailyChangePct = 1.60,
                                open = 281.00,
                                high = 288.00,
                                low = 280.00,
                                volume = 45200000,
                                lastUpdateTime = System.currentTimeMillis()
                            )
                        )
                    }
                }
            }

            // 3. Haberler ve KAP Duyuruları
            launch {
                newsRepository.getLatestNews().collect { newsList ->
                    _uiState.update { it.copy(news = newsList) }
                }
            }

            // 4. Orakul AI Skoru ve Mock Redesign Verileri
            launch {
                aiHistoryRepository.getLatestAiAnalysis(symbol).collect { aiItem ->
                    _uiState.update { state ->
                        state.copy(
                            aiHistory = aiItem,
                            aiSummary = "Türk Hava Yolları, güçlü operasyonel performansı ve genişleyen uçuş ağı ile global havacılık sektöründe pazar payını artırmaya devam ediyor. Artan turizm talebi ve kargo gelirlerindeki stabilizasyon finansalları destekliyor.",
                            aiRisks = listOf("Akaryakıt maliyetlerindeki volatilite", "Küresel ekonomik yavaşlama", "Jeopolitik riskler"),
                            aiOpportunities = listOf("Yeni nesil uçak filosu ile verimlilik artışı", "Kargo pazarındaki stratejik büyüme", "Transit yolcu trafiğindeki liderlik"),
                            aiTargetPrice = 345.50,
                            aiPotentialReturn = 21.4,
                            aiAgents = listOf(
                                AiAgentConsensus("Orakul Alpha", null, "BUY", 0.92),
                                AiAgentConsensus("Sentiment Bot", null, "BUY", 0.85),
                                AiAgentConsensus("Value Hunter", null, "STRONG BUY", 0.95),
                                AiAgentConsensus("Risk Manager", null, "HOLD", 0.70),
                                AiAgentConsensus("Technician", null, "BUY", 0.78),
                                AiAgentConsensus("Macro Eye", null, "BUY", 0.82),
                                AiAgentConsensus("Quant Engine", null, "STRONG BUY", 0.94)
                            ),
                            quickMetrics = listOf(
                                QuickMetricItem("F/K", "3.2", 0.5),
                                QuickMetricItem("PD/DD", "0.9", -0.2),
                                QuickMetricItem("ROE", "%55.2", 2.1),
                                QuickMetricItem("ROIC", "%18.4", 1.0),
                                QuickMetricItem("Temettü", "%4.2", 0.0),
                                QuickMetricItem("Borç/FAVÖK", "1.8", -0.5),
                                QuickMetricItem("FCF Yield", "%12.4", 0.8),
                                QuickMetricItem("EPS", "32.40", 4.2),
                                QuickMetricItem("Net Marj", "%32.4", 1.5),
                                QuickMetricItem("Beta", "1.05", null)
                            ),
                            financialSummary = FinancialSummaryData(
                                revenue = "504.2 Mr TL",
                                grossProfit = "182.4 Mr TL",
                                ebitda = "122.4 Mr TL",
                                netIncome = "163.8 Mr TL",
                                eps = "32.40 TL",
                                equity = "295.1 Mr TL",
                                totalAssets = "840.2 Mr TL",
                                totalDebt = "210.5 Mr TL",
                                netDebt = "126.3 Mr TL"
                            ),
                            quarterlyPerformance = listOf(
                                QuarterlyBarData("2024 Q1", 420.0, 95.0, 120.0),
                                QuarterlyBarData("2024 Q2", 450.0, 105.0, 140.0),
                                QuarterlyBarData("2024 Q3", 480.0, 115.0, 155.0),
                                QuarterlyBarData("2024 Q4", 504.2, 122.4, 163.8)
                            ),
                            marginAnalysis = listOf(
                                MarginLineData("2024 Q1", 34.0, 28.0, 22.0),
                                MarginLineData("2024 Q2", 35.5, 30.2, 23.5),
                                MarginLineData("2024 Q3", 36.0, 31.5, 24.0),
                                MarginLineData("2024 Q4", 36.2, 32.4, 24.3)
                            ),
                            financialHealth = FinancialHealthData(
                                liquidity = 0.85,
                                leverage = 0.42,
                                interestCoverage = 4.5,
                                cashPosition = 84.2,
                                currentRatio = 1.25,
                                quickRatio = 1.05
                            ),
                            valuationModules = listOf(
                                ScoreCardData("DCF Fair Value", "355.20 TL", 0.85, "Undervalued"),
                                ScoreCardData("Graham Value", "312.40 TL", 0.70, "Near Fair"),
                                ScoreCardData("Intrinsic Value", "338.00 TL", 0.80, "Undervalued"),
                                ScoreCardData("Margin of Safety", "%21.4", 0.90, "Excellent")
                            ),
                            qualityModules = listOf(
                                ScoreCardData("Piotroski F", "8/9", 0.88, "High Quality"),
                                ScoreCardData("ROE Performance", "%55.2", 0.95, "Excellent"),
                                ScoreCardData("Economic Moat", "Wide", 0.90, "Strong"),
                                ScoreCardData("ROIC", "%18.4", 0.85, "Good")
                            ),
                            riskModules = listOf(
                                ScoreCardData("Altman Z", "2.85", 0.65, "Safe Zone"),
                                ScoreCardData("Beneish M", "-2.80", 0.90, "No Manipulation"),
                                ScoreCardData("Debt Score", "Low", 0.80, "Safe"),
                                ScoreCardData("FCF Quality", "High", 0.85, "Cash Rich")
                            ),
                            aiScenarios = listOf(
                                AiScenarioData("Bull", "Turizm patlaması ve kargo büyümesi", 385.00, 0.30),
                                AiScenarioData("Base", "Mevcut büyüme projeksiyonu", 345.50, 0.50),
                                AiScenarioData("Bear", "Resesyon ve yüksek petrol fiyatı", 280.00, 0.20)
                            ),
                            analystConsensus = 0.88,
                            aiConfidenceScore = 0.92,
                            boardMembers = listOf(
                                BoardMember("Ahmet Bolat", "Yönetim Kurulu Başkanı"),
                                BoardMember("Bilal Ekşi", "Genel Müdür / CEO"),
                                BoardMember("Murat Şeker", "Genel Müdür Yardımcısı (Finans)")
                            ),
                            ownershipStructure = listOf(
                                OwnerData("Türkiye Varlık Fonu", 49.12),
                                OwnerData("Halka Açık", 50.88)
                            ),
                            corporateTimeline = listOf(
                                TimelineEvent("2024 Q4", "Rekor Kar Açıklaması", "Tarihinin en yüksek çeyreklik karı."),
                                TimelineEvent("2024 Q3", "Yeni Uçak Siparişi", "250 adet Airbus siparişi onaylandı."),
                                TimelineEvent("2024 Q2", "Sürdürülebilirlik Ödülü", "Karbon ayak izi azaltım başarısı.")
                            ),
                            isLoading = false
                        )
                    }
                }
            }
        }
    }
}
