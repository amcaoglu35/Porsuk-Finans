package com.nexus.porsuk.domain.repository

import com.nexus.porsuk.domain.model.SmartCategory
import com.nexus.porsuk.domain.model.WatchlistGroup
import com.nexus.porsuk.domain.model.WatchlistItemPro
import kotlinx.coroutines.flow.Flow

/**
 * 1. Takip Listesi Grupları Deposu Sözleşmesi (WatchlistProRepository)
 */
interface WatchlistProRepository {
    fun getAllWatchlistGroups(): Flow<List<WatchlistGroup>>
    fun getFavoriteGroups(): Flow<List<WatchlistGroup>>
    suspend fun createWatchlistGroup(title: String, smartCategory: SmartCategory? = null): String
    suspend fun updateWatchlistGroup(group: WatchlistGroup)
    suspend fun deleteWatchlistGroup(groupId: String)
    suspend fun setFavoriteGroup(groupId: String, isFavorite: Boolean)
}

/**
 * 2. Takip Listesi Kalemleri ve Toplu İşlemler Sözleşmesi (WatchlistItemProRepository)
 */
interface WatchlistItemProRepository {
    fun getItemsForGroup(groupId: String): Flow<List<WatchlistItemPro>>
    fun isSymbolInGroup(groupId: String, symbol: String): Flow<Boolean>
    suspend fun addItemToGroup(groupId: String, symbol: String, notes: String? = null, tags: List<String> = emptyList())
    suspend fun addBulkItemsToGroup(groupId: String, symbols: List<String>)
    suspend fun removeItemsFromGroup(groupId: String, symbols: List<String>)
    suspend fun updateItemNotesAndTags(groupId: String, symbol: String, notes: String?, tags: List<String>)
}

/**
 * 3. Favoriler Deposu Sözleşmesi (FavoriteRepository)
 */
interface FavoriteRepository {
    fun getFavoriteSymbols(): Flow<Set<String>>
    suspend fun toggleFavoriteSymbol(symbol: String)
}
