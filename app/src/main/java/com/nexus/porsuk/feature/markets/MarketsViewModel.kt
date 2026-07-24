package com.nexus.porsuk.feature.markets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.domain.model.AssetCategory
import com.nexus.porsuk.domain.model.MarketQuote
import com.nexus.porsuk.domain.repository.MarketRepository
import com.nexus.porsuk.domain.repository.WatchlistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Porsuk Markets Module — ViewModel (MarketsViewModel)
 *
 * 10 piyasa sekmesindeki verileri, favorileri, aramaları, sıralamaları ve Liste/Grid görünümlerini yönetir.
 */
@HiltViewModel
class MarketsViewModel @Inject constructor(
    private val marketRepository: MarketRepository,
    private val watchlistRepository: WatchlistRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MarketsUiState())
    val uiState: StateFlow<MarketsUiState> = _uiState.asStateFlow()

    init {
        observeFavorites()
        loadDataForSelectedTab(_uiState.value.selectedTab)
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            watchlistRepository.getAllWatchlistItems().collect { items ->
                val favSet = items.map { it.symbol }.toSet()
                _uiState.update { it.copy(favoriteSymbols = favSet) }
                applyFiltersAndSorting()
            }
        }
    }

    fun selectTab(tab: MarketTab) {
        _uiState.update { it.copy(selectedTab = tab, isLoading = true) }
        loadDataForSelectedTab(tab)
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFiltersAndSorting()
    }

    fun updateSortOption(option: MarketSortOption) {
        _uiState.update { it.copy(sortOption = option) }
        applyFiltersAndSorting()
    }

    fun toggleViewMode() {
        _uiState.update { it.copy(isGridView = !it.isGridView) }
    }

    fun toggleFavorite(symbol: String) {
        viewModelScope.launch {
            val isFav = _uiState.value.favoriteSymbols.contains(symbol)
            if (isFav) {
                watchlistRepository.removeWatchlistItem(symbol)
            } else {
                watchlistRepository.addWatchlistItem(symbol)
            }
        }
    }

    private fun loadDataForSelectedTab(tab: MarketTab) {
        viewModelScope.launch {
            if (tab == MarketTab.FAVORITES) {
                marketRepository.getQuotesByCategory(AssetCategory.BIST_STOCK).collect { allQuotes ->
                    val favQuotes = allQuotes.filter { _uiState.value.favoriteSymbols.contains(it.symbol) }
                    _uiState.update { it.copy(quotesList = favQuotes, isLoading = false) }
                    applyFiltersAndSorting()
                }
            } else {
                val category = tab.category ?: AssetCategory.BIST_STOCK
                marketRepository.getQuotesByCategory(category).collect { quotes ->
                    _uiState.update { it.copy(quotesList = quotes, isLoading = false) }
                    applyFiltersAndSorting()
                }
            }
        }
    }

    private fun applyFiltersAndSorting() {
        val currentState = _uiState.value
        var list = currentState.quotesList

        // 1. Arama Filtresi
        if (currentState.searchQuery.isNotBlank()) {
            val query = currentState.searchQuery.trim().lowercase()
            list = list.filter {
                it.symbol.lowercase().contains(query) || it.name.lowercase().contains(query)
            }
        }

        // 2. Sıralama
        list = when (currentState.sortOption) {
            MarketSortOption.CHANGE_PERCENT_DESC -> list.sortedByDescending { it.dailyChangePct }
            MarketSortOption.CHANGE_PERCENT_ASC -> list.sortedBy { it.dailyChangePct }
            MarketSortOption.PRICE_DESC -> list.sortedByDescending { it.lastPrice }
            MarketSortOption.PRICE_ASC -> list.sortedBy { it.lastPrice }
            MarketSortOption.VOLUME_DESC -> list.sortedByDescending { it.volume }
            MarketSortOption.SYMBOL_ASC -> list.sortedBy { it.symbol }
            MarketSortOption.DEFAULT -> list
        }

        _uiState.update { it.copy(filteredQuotesList = list) }
    }
}
