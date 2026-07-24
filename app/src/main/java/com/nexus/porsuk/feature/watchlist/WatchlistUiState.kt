package com.nexus.porsuk.feature.watchlist

import com.nexus.porsuk.domain.model.SmartCategory
import com.nexus.porsuk.domain.model.WatchlistGroup
import com.nexus.porsuk.domain.model.WatchlistItemPro

/**
 * Porsuk Watchlist Pro — UI Ekran Durumu (WatchlistUiState)
 */
data class WatchlistUiState(
    val groups: List<WatchlistGroup> = emptyList(),
    val selectedGroup: WatchlistGroup? = null,
    val itemsInSelectedGroup: List<WatchlistItemPro> = emptyList(),
    val filteredItems: List<WatchlistItemPro> = emptyList(),
    val selectedSmartCategory: SmartCategory? = null,
    val searchQuery: String = "",
    val isMultiSelectMode: Boolean = false,
    val selectedSymbolsForBulkDelete: Set<String> = emptySet(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
