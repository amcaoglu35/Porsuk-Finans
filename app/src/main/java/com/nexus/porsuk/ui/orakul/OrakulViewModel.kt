package com.nexus.porsuk.ui.orakul

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.data.local.SettingsManager
import com.nexus.porsuk.data.local.entity.Basket
import com.nexus.porsuk.data.local.entity.BasketItem
import com.nexus.porsuk.data.local.entity.CompanyRatioEntity
import com.nexus.porsuk.data.local.entity.IncomeStatementEntity
import com.nexus.porsuk.data.repository.FinanceRepository
import com.nexus.porsuk.ui.orakul.engine.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class OrakulMode(val label: String, val emoji: String, val description: String) {
    KAZI("Derin Kazı", "⛏️", "Piyasadaki tüm verileri tarar, en gizli fırsatları bulur."),
    BASKET("Model Sepet", "🧺", "Bütçenize ve risk profilinize özel model portföy oluşturur."),
    ASK("Simsar Sor", "💬", "Borsa üstadına piyasa veya belirli bir hisse hakkında her şeyi sor.")
}

data class OrakulDecision(
    val symbol: String,
    val decision: String,
    val reason: String,
    val confidence: Int,
    val formulaLayer: String = "",
    val weight: Double = 0.0,
    val rsi: Double = 50.0,
    val sma20: Double = 0.0,
    val sma50: Double = 0.0,
    val crossSignal: String = "NÖTR"
)

data class RebalanceTrade(
    val symbol: String,
    val currentQty: Double,
    val currentPrice: Double,
    val targetWeight: Double,
    val targetQty: Double,
    val tradeQty: Double,
    val valueDiff: Double,
    val decision: String
)

data class OrakulHistoryEntry(
    val timestamp: String,
    val mode: String,
    val decisionCount: Int,
    val topDecision: String
)

data class OrakulStressScenario(
    val scenario: String,
    val impact: String,
    val advice: String
)

data class OracleHisseReport(
    val aiScore: Int,
    val riskScore: Int,
    val growthPotential: Int,
    val dividendScore: Int,
    val financialHealth: Int,
    val momentum: Int,
    val volatility: Int,
    val liquidity: Int,
    val qualityScore: Int,
    val confidence: Int,
    val recommendation: String,
    val fairValue: Double,
    val strengths: List<String>,
    val weaknesses: List<String>,
    val opportunities: List<String>,
    val risks: List<String>,
    val shortTermOutlook: String,
    val longTermOutlook: String,
    val investmentThesis: String
)

data class OraclePortfolioReport(
    val title: String,
    val summary: String,
    val plan: String,
    val scenario: String,
    val impact: String,
    val recommendation: String
)

data class OrakulUiState(
    val isLoading: Boolean = false,
    val selectedSymbol: String = "",
    val hisseReport: OracleHisseReport? = null,
    val selectedMode: OrakulMode = OrakulMode.ASK,
    val streamingText: String = "",
    val rawResponse: String? = null,
    val decisions: List<OrakulDecision> = emptyList(),
    val lastAnalysisTime: String? = null,
    val hasGeminiKey: Boolean = false,
    val error: String? = null,
    val customQuestion: String = "",
    val investmentAmount: String = "100.000",
    val selectedTerm: String = "Orta Vade",
    val selectedMarket: String = "BIST",
    val history: List<OrakulHistoryEntry> = emptyList(),
    val marketSentimentScore: Int = 68,
    val rebalanceTrades: List<RebalanceTrade> = emptyList(),
    val rebalanceBaskets: List<Basket> = emptyList(),
    val selectedRebalanceBasketId: Int? = null,
    val stressScenarios: List<OrakulStressScenario> = emptyList(),
    val basketRiskProfile: String = "BALANCED",
    val basketStrategyFocus: String = "GROWTH",
    val basketStockCount: Int = 5,
    val basketCashPct: Double = 10.0,
    val basketReport: OraclePortfolioReport? = null,
    val sourceEngine: String = "ORAKUL-2.0-QUANT",
    val consensusWeights: Map<String, Int> = mapOf(
        "Fundamental" to 35,
        "Technical" to 25,
        "Sentiment" to 20,
        "Macro" to 20
    ),
    val bullCase: String? = null,
    val bearCase: String? = null
)

@HiltViewModel
class OrakulViewModel @Inject constructor(
    private val repository: FinanceRepository,
    private val settingsManager: SettingsManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrakulUiState())
    val uiState: StateFlow<OrakulUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val key = settingsManager.getGeminiApiKey()
            _uiState.update { it.copy(hasGeminiKey = !key.isNullOrBlank()) }
            
            repository.allBaskets.collect { baskets ->
                _uiState.update { it.copy(rebalanceBaskets = baskets) }
            }
        }
    }

    fun selectMode(mode: OrakulMode) {
        _uiState.update { it.copy(selectedMode = mode, error = null, streamingText = "", rawResponse = null, decisions = emptyList()) }
    }

    fun setCustomQuestion(q: String) { _uiState.update { it.copy(customQuestion = q) } }
    fun setInvestmentAmount(a: String) { _uiState.update { it.copy(investmentAmount = a) } }
    fun setSelectedTerm(t: String) { _uiState.update { it.copy(selectedTerm = t) } }
    fun setSelectedMarket(m: String) { _uiState.update { it.copy(selectedMarket = m) } }
    fun setBasketRiskProfile(p: String) { _uiState.update { it.copy(basketRiskProfile = p) } }
    fun setBasketStrategyFocus(f: String) { _uiState.update { it.copy(basketStrategyFocus = f) } }
    fun setBasketStockCount(c: Int) { _uiState.update { it.copy(basketStockCount = c) } }
    fun setBasketCashPct(p: Double) { _uiState.update { it.copy(basketCashPct = p) } }

    fun analyzeSymbol(symbol: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, selectedSymbol = symbol, error = null) }
            try {
                val data = repository.getAiOracleData(symbol)
                val report = repository.getAiOracleReport(
                    symbol,
                    data["price"] as Double,
                    data["income"] as List<IncomeStatementEntity>,
                    data["ratios"] as List<CompanyRatioEntity>
                )
                _uiState.update { it.copy(hisseReport = report, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.localizedMessage, isLoading = false) }
            }
        }
    }

    fun analyze() {
        val mode = _uiState.value.selectedMode
        val apiKey = settingsManager.getGeminiApiKey()
        if (apiKey.isNullOrBlank()) {
            _uiState.update { it.copy(error = "Gemini API anahtarı bulunamadı. Lütfen Ayarlar sayfasından anahtarınızı girin.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, streamingText = "", rawResponse = null) }
            
            try {
                val companies = repository.allCompanies.first()
                val marketFilter = _uiState.value.selectedMarket
                val filteredCompanies = if (marketFilter == "Tümü") companies else companies.filter { it.market == marketFilter }
                
                val companyLines = filteredCompanies.take(15).joinToString("\n") { comp ->
                    "${comp.symbol}: ${comp.name}, Fiyat: ${comp.currentPrice}, Değişim: %${comp.changePercent}, Sektör: ${comp.sector}"
                }

                val portfolioItems = repository.getAllBasketItemsDirect()
                val portfolioLines = portfolioItems.joinToString("\n") { "${it.symbol}: ${it.quantity} adet, Maliyet: ${it.buyPrice}" }

                val prompt = OrakulPromptBuilder.buildPrompt(
                    mode = mode,
                    uiState = _uiState.value,
                    companyLines = companyLines,
                    portfolioLines = portfolioLines,
                    customQuestion = _uiState.value.customQuestion,
                    exchangeRates = repository.exchangeRates.value
                )

                val flow = repository.getOrakulStream(prompt)
                var fullResponse = ""
                flow.collect { chunk ->
                    fullResponse += chunk
                    _uiState.update { it.copy(streamingText = fullResponse) }
                }

                val decisions = OrakulParser.parseDecisions(fullResponse)
                val stressScenarios = OrakulParser.parseStressScenarios(fullResponse)
                val (bull, bear) = OrakulParser.parseBullBearCases(fullResponse)

                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        rawResponse = fullResponse,
                        decisions = decisions,
                        stressScenarios = stressScenarios,
                        bullCase = bull,
                        bearCase = bear,
                        lastAnalysisTime = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.localizedMessage) }
            }
        }
    }

    fun saveGeneratedBasket(basketName: String, onComplete: () -> Unit) {
        val decisions = _uiState.value.decisions
        if (decisions.isEmpty()) return

        viewModelScope.launch {
            val market = _uiState.value.selectedMarket
            val basketId = repository.addBasket(Basket(name = basketName, market = market, category = "AI Model"))
            
            val totalBudgetStr = _uiState.value.investmentAmount.replace(Regex("[^0-9]"), "")
            val totalBudget = totalBudgetStr.toDoubleOrNull() ?: 100000.0
            
            val pricesMap = repository.prices.value
            val companies = repository.allCompanies.first().associateBy { it.symbol }

            decisions.filter { it.decision == "AL" }.forEach { decision ->
                val currentPrice = pricesMap[decision.symbol]?.price ?: companies[decision.symbol]?.currentPrice ?: 100.0
                val weight = decision.weight / 100.0
                val qty = (totalBudget * weight) / currentPrice
                
                if (qty > 0) {
                    repository.addBasketItem(
                        BasketItem(
                            basketId = basketId.toInt(),
                            symbol = decision.symbol,
                            quantity = qty,
                            buyPrice = currentPrice,
                            buyDate = System.currentTimeMillis()
                        )
                    )
                }
            }
            onComplete()
        }
    }

    fun executeRebalance(onComplete: () -> Unit) {
        val trades = _uiState.value.rebalanceTrades
        val basketId = _uiState.value.selectedRebalanceBasketId ?: return
        
        viewModelScope.launch {
            trades.forEach { trade ->
                if (trade.tradeQty != 0.0) {
                    repository.executeTransaction(
                        basketId = basketId,
                        symbol = trade.symbol,
                        quantity = kotlin.math.abs(trade.tradeQty),
                        price = trade.currentPrice,
                        isBuy = trade.tradeQty > 0
                    )
                }
            }
            onComplete()
        }
    }

    fun initRebalanceWizard() {
        viewModelScope.launch {
            val baskets = repository.allBaskets.first()
            if (baskets.isNotEmpty()) {
                _uiState.update { it.copy(rebalanceBaskets = baskets, selectedRebalanceBasketId = baskets.first().id) }
                calculateRebalanceTrades(baskets.first().id)
            }
        }
    }

    fun selectRebalanceBasket(id: Int) {
        _uiState.update { it.copy(selectedRebalanceBasketId = id) }
        calculateRebalanceTrades(id)
    }

    fun calculateRebalanceTrades(basketId: Int) {
        val decisions = _uiState.value.decisions
        if (decisions.isEmpty()) return

        viewModelScope.launch {
            val companies = repository.allCompanies.first()
            val allItems = repository.getAllBasketItemsDirect().filter { it.basketId == basketId }
            val rates = repository.exchangeRates.value
            val pricesMap = repository.prices.value
            
            val trades = OrakulTradeEngine.calculateRebalanceTrades(
                basketId = basketId,
                decisions = decisions,
                companies = companies,
                allItems = allItems,
                exchangeRates = rates,
                pricesMap = pricesMap,
                investmentAmount = _uiState.value.investmentAmount
            )

            _uiState.update { it.copy(rebalanceTrades = trades) }
        }
    }

    fun executeRebalanceTrades(basketId: Int, trades: List<RebalanceTrade>, onComplete: () -> Unit) {
        viewModelScope.launch {
            trades.forEach { trade ->
                if (trade.tradeQty != 0.0) {
                    repository.executeTransaction(
                        basketId = basketId,
                        symbol = trade.symbol,
                        quantity = kotlin.math.abs(trade.tradeQty),
                        price = trade.currentPrice,
                        isBuy = trade.tradeQty > 0
                    )
                }
            }
            onComplete()
        }
    }

    fun clearError() { _uiState.update { it.copy(error = null) } }
}
