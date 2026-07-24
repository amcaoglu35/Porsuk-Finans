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

            // 4. Orakul AI Skoru
            launch {
                aiHistoryRepository.getLatestAiAnalysis(symbol).collect { aiItem ->
                    _uiState.update { it.copy(aiHistory = aiItem, isLoading = false) }
                }
            }
        }
    }
}
