package com.nexus.porsuk.feature.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.domain.model.SmartCategory
import com.nexus.porsuk.domain.model.WatchlistGroup
import com.nexus.porsuk.domain.repository.WatchlistItemProRepository
import com.nexus.porsuk.domain.repository.WatchlistProRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Porsuk Watchlist Pro — ViewModel (WatchlistViewModel)
 *
 * Çoklu takip listelerini, Akıllı Klasörleri, toplu silme/ekleme ve arama/filtreleme işlemlerini yönetir.
 */
@HiltViewModel
class WatchlistViewModel @Inject constructor(
    private val watchlistProRepository: WatchlistProRepository,
    private val itemRepository: WatchlistItemProRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WatchlistUiState())
    val uiState: StateFlow<WatchlistUiState> = _uiState.asStateFlow()

    init {
        loadWatchlistGroups()
    }

    private fun loadWatchlistGroups() {
        viewModelScope.launch {
            watchlistProRepository.getAllWatchlistGroups().collect { groupsList ->
                if (groupsList.isEmpty()) {
                    // Varsayılan takip listelerini ve Akıllı Klasörleri oluştur
                    createDefaultWatchlistGroups()
                } else {
                    val currentSelected = _uiState.value.selectedGroup ?: groupsList.first()
                    _uiState.update { it.copy(groups = groupsList, selectedGroup = currentSelected, isLoading = false) }
                    loadItemsForGroup(currentSelected.groupId)
                }
            }
        }
    }

    private suspend fun createDefaultWatchlistGroups() {
        val favId = watchlistProRepository.createWatchlistGroup("Favoriler")
        watchlistProRepository.setFavoriteGroup(favId, true)

        watchlistProRepository.createWatchlistGroup("Temettü Klasörü", SmartCategory.DIVIDEND)
        watchlistProRepository.createWatchlistGroup("Teknoloji Devleri", SmartCategory.TECHNOLOGY)
        watchlistProRepository.createWatchlistGroup("Kripto Takip", SmartCategory.CRYPTO)

        itemRepository.addBulkItemsToGroup(favId, listOf("THYAO.IS", "GARAN.IS", "AAPL", "BTC-USD"))
    }

    fun selectGroup(group: WatchlistGroup) {
        _uiState.update { it.copy(selectedGroup = group, isMultiSelectMode = false, selectedSymbolsForBulkDelete = emptySet()) }
        loadItemsForGroup(group.groupId)
    }

    fun selectSmartCategory(category: SmartCategory?) {
        _uiState.update { it.copy(selectedSmartCategory = category) }
        applySearchAndFilters()
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applySearchAndFilters()
    }

    fun createNewGroup(title: String, category: SmartCategory? = null) {
        viewModelScope.launch {
            watchlistProRepository.createWatchlistGroup(title, category)
        }
    }

    fun deleteGroup(groupId: String) {
        viewModelScope.launch {
            watchlistProRepository.deleteWatchlistGroup(groupId)
        }
    }

    fun toggleFavoriteGroup(groupId: String, isFavorite: Boolean) {
        viewModelScope.launch {
            watchlistProRepository.setFavoriteGroup(groupId, isFavorite)
        }
    }

    fun addItemToCurrentGroup(symbol: String) {
        viewModelScope.launch {
            val groupId = _uiState.value.selectedGroup?.groupId ?: return@launch
            itemRepository.addItemToGroup(groupId, symbol)
        }
    }

    fun toggleBulkSelectSymbol(symbol: String) {
        val currentSet = _uiState.value.selectedSymbolsForBulkDelete.toMutableSet()
        if (currentSet.contains(symbol)) {
            currentSet.remove(symbol)
        } else {
            currentSet.add(symbol)
        }
        _uiState.update { it.copy(selectedSymbolsForBulkDelete = currentSet) }
    }

    fun executeBulkDelete() {
        viewModelScope.launch {
            val groupId = _uiState.value.selectedGroup?.groupId ?: return@launch
            val symbols = _uiState.value.selectedSymbolsForBulkDelete.toList()
            if (symbols.isNotEmpty()) {
                itemRepository.removeItemsFromGroup(groupId, symbols)
                _uiState.update { it.copy(isMultiSelectMode = false, selectedSymbolsForBulkDelete = emptySet()) }
            }
        }
    }

    fun toggleMultiSelectMode() {
        _uiState.update { it.copy(isMultiSelectMode = !it.isMultiSelectMode, selectedSymbolsForBulkDelete = emptySet()) }
    }

    private fun loadItemsForGroup(groupId: String) {
        viewModelScope.launch {
            itemRepository.getItemsForGroup(groupId).collect { itemsList ->
                _uiState.update { it.copy(itemsInSelectedGroup = itemsList) }
                applySearchAndFilters()
            }
        }
    }

    private fun applySearchAndFilters() {
        val state = _uiState.value
        var list = state.itemsInSelectedGroup

        if (state.searchQuery.isNotBlank()) {
            val query = state.searchQuery.lowercase().trim()
            list = list.filter { it.symbol.lowercase().contains(query) }
        }

        _uiState.update { it.copy(filteredItems = list) }
    }
}
