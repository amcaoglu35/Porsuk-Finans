package com.nexus.porsuk.feature.sample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.core.domain.engine.AiLabEngine
import com.nexus.porsuk.core.domain.engine.AiLabTool
import com.nexus.porsuk.core.domain.engine.PortfolioDoctorEngine
import com.nexus.porsuk.core.domain.engine.PortfolioDoctorReport
import com.nexus.porsuk.core.domain.engine.PortfolioSimulationReport
import com.nexus.porsuk.core.domain.engine.PortfolioSimulatorEngine
import com.nexus.porsuk.core.domain.engine.StockDuelEngine
import com.nexus.porsuk.core.domain.engine.StockDuelResult
import com.nexus.porsuk.core.domain.entity.CompanyStock
import com.nexus.porsuk.core.domain.entity.MacroIndicators
import com.nexus.porsuk.core.domain.entity.MarketSentiment
import com.nexus.porsuk.core.domain.repository.CorporateEvent
import com.nexus.porsuk.core.domain.repository.CorporateEventRepository
import com.nexus.porsuk.core.domain.repository.FundComparisonRepository
import com.nexus.porsuk.core.domain.repository.FundOverlapResult
import com.nexus.porsuk.core.domain.repository.InstitutionalAnalyticsRepository
import com.nexus.porsuk.core.domain.repository.InstitutionalHolding
import com.nexus.porsuk.core.domain.repository.KapNotice
import com.nexus.porsuk.core.domain.repository.KapRadarRepository
import com.nexus.porsuk.core.domain.repository.MarketRepository
import com.nexus.porsuk.core.domain.repository.MutualFund
import com.nexus.porsuk.core.domain.repository.NewsItem
import com.nexus.porsuk.core.domain.repository.NewsRepository
import com.nexus.porsuk.core.domain.repository.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SampleViewModel @Inject constructor(
    private val stockRepository: StockRepository,
    private val marketRepository: MarketRepository,
    private val newsRepository: NewsRepository,
    private val kapRadarRepository: KapRadarRepository,
    private val corporateEventRepository: CorporateEventRepository,
    private val institutionalAnalyticsRepository: InstitutionalAnalyticsRepository,
    private val fundComparisonRepository: FundComparisonRepository,
    private val portfolioDoctorEngine: PortfolioDoctorEngine,
    private val stockDuelEngine: StockDuelEngine,
    private val portfolioSimulatorEngine: PortfolioSimulatorEngine,
    private val aiLabEngine: AiLabEngine
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedSector = MutableStateFlow("Tümü")
    val selectedSector: StateFlow<String> = _selectedSector.asStateFlow()

    val sectors: List<String> = stockRepository.getSectors()

    private val _filteredStocks = MutableStateFlow<List<CompanyStock>>(emptyList())
    val stocks: StateFlow<List<CompanyStock>> = _filteredStocks.asStateFlow()

    private val _selectedStock = MutableStateFlow<CompanyStock?>(null)
    val selectedStock: StateFlow<CompanyStock?> = _selectedStock.asStateFlow()

    val marketSentiment: StateFlow<MarketSentiment> = marketRepository.getMarketSentiment()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MarketSentiment(74, 26, 68, "Hırs", 14.8, 1.65, 87.6))

    val macroIndicators: StateFlow<MacroIndicators> = marketRepository.getMacroIndicators()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MacroIndicators())

    val latestNews: StateFlow<List<NewsItem>> = newsRepository.getLatestNews()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val kapNotices: StateFlow<List<KapNotice>> = kapRadarRepository.getKapNotices()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val corporateEvents: StateFlow<List<CorporateEvent>> = corporateEventRepository.getCorporateEvents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val institutionalData: StateFlow<List<InstitutionalHolding>> = institutionalAnalyticsRepository.getInstitutionalData()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val funds: StateFlow<List<MutualFund>> = fundComparisonRepository.getFunds()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _portfolioDoctorReport = MutableStateFlow<PortfolioDoctorReport?>(null)
    val portfolioDoctorReport: StateFlow<PortfolioDoctorReport?> = _portfolioDoctorReport.asStateFlow()

    private val _backtestReport = MutableStateFlow<PortfolioSimulationReport?>(null)
    val backtestReport: StateFlow<PortfolioSimulationReport?> = _backtestReport.asStateFlow()

    private val _aiLabTools = MutableStateFlow<List<AiLabTool>>(emptyList())
    val aiLabTools: StateFlow<List<AiLabTool>> = _aiLabTools.asStateFlow()

    private val _networkErrorMessage = MutableStateFlow<String?>(null)
    val networkErrorMessage: StateFlow<String?> = _networkErrorMessage.asStateFlow()

    init {
        viewModelScope.launch {
            // Refresh live quotes asynchronously
            val result = stockRepository.refreshLiveQuotes()
            result.onFailure {
                _networkErrorMessage.value = "Veri güncellenemedi: İnternet bağlantınızı kontrol edin."
            }

            // Heavy AI calculations executed ONLY when stock data list updates
            launch {
                stockRepository.getStocks().collect { allStocks ->
                    updateEngineReports(allStocks)
                }
            }

            // Fast UI filtering (search & sector) without re-triggering heavy AI engines
            combine(
                stockRepository.getStocks(),
                _searchQuery,
                _selectedSector
            ) { allStocks, query, sector ->
                allStocks.filter { stock ->
                    val matchesQuery = query.isBlank() ||
                            stock.symbol.contains(query, ignoreCase = true) ||
                            stock.name.contains(query, ignoreCase = true)

                    val matchesSector = sector == "Tümü" ||
                            stock.sector.equals(sector, ignoreCase = true)

                    matchesQuery && matchesSector
                }
            }.collect { filtered ->
                _filteredStocks.value = filtered
            }
        }
    }

    private fun updateEngineReports(allStocks: List<CompanyStock>) {
        val macro = macroIndicators.value
        _portfolioDoctorReport.value = portfolioDoctorEngine.analyzePortfolio(allStocks, macro)
        _backtestReport.value = portfolioSimulatorEngine.runBacktest(allStocks.take(5))
        _aiLabTools.value = aiLabEngine.generateAiLabTools(allStocks, macro)
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onSectorSelected(sector: String) {
        _selectedSector.value = sector
    }

    fun selectStock(symbol: String) {
        _selectedStock.value = stockRepository.getStockBySymbol(symbol)
    }

    fun runDuel(symbolA: String, symbolB: String): StockDuelResult? {
        val stockA = stockRepository.getStockBySymbol(symbolA) ?: return null
        val stockB = stockRepository.getStockBySymbol(symbolB) ?: return null
        return stockDuelEngine.duel(stockA, stockB)
    }

    fun calculateFundOverlap(codeA: String, codeB: String): FundOverlapResult {
        return fundComparisonRepository.calculateOverlap(codeA, codeB)
    }
}
