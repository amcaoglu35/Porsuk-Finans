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
        .map { list -> list.map { it.price } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadCompanyDetailData()
        observeWatchlistStatus()
        fetchHistory()
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
            _uiState.update { it.copy(isLoading = true) }

            // 1. Refresh real data from API and Save to Room
            launch {
                financeRepository.refreshFullCompanyDetail(symbol)
                financeRepository.refreshPrice(symbol, market)
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
                                liquidity = last.roe, // Mapping ROE to liquidity field for UI reuse
                                leverage = last.roa,
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

            _uiState.update { it.copy(isLoading = false) }
        }
    }
}
