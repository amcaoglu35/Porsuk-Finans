package com.nexus.porsuk.data.local.dao

import androidx.room.*
import com.nexus.porsuk.data.local.entity.WatchlistAlertStubEntity
import com.nexus.porsuk.data.local.entity.WatchlistGroupEntity
import com.nexus.porsuk.data.local.entity.WatchlistItemProEntity
import kotlinx.coroutines.flow.Flow

/**
 * Porsuk Watchlist Pro — Room DAO Sorguları
 */
@Dao
interface WatchlistProDao {

    // Takip Listesi Grubu Sorguları
    @Query("SELECT * FROM pro_watchlist_groups ORDER BY created_at ASC")
    fun getAllWatchlistGroups(): Flow<List<WatchlistGroupEntity>>

    @Query("SELECT * FROM pro_watchlist_groups WHERE is_favorite = 1")
    fun getFavoriteWatchlistGroups(): Flow<List<WatchlistGroupEntity>>

    @Query("SELECT * FROM pro_watchlist_groups WHERE group_id = :groupId LIMIT 1")
    fun getWatchlistGroupById(groupId: String): Flow<WatchlistGroupEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchlistGroup(group: WatchlistGroupEntity)

    @Update
    suspend fun updateWatchlistGroup(group: WatchlistGroupEntity)

    @Delete
    suspend fun deleteWatchlistGroup(group: WatchlistGroupEntity)

    // Takip Listesi Kalemi Sorguları
    @Query("SELECT * FROM pro_watchlist_items WHERE group_id = :groupId ORDER BY added_at DESC")
    fun getItemsForGroup(groupId: String): Flow<List<WatchlistItemProEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM pro_watchlist_items WHERE group_id = :groupId AND symbol = :symbol)")
    fun isSymbolInGroup(groupId: String, symbol: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchlistItem(item: WatchlistItemProEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchlistItems(items: List<WatchlistItemProEntity>)

    @Query("DELETE FROM pro_watchlist_items WHERE group_id = :groupId AND symbol IN (:symbols)")
    suspend fun deleteItemsFromGroup(groupId: String, symbols: List<String>)

    @Delete
    suspend fun deleteWatchlistItem(item: WatchlistItemProEntity)

    // Geleceğe Hazır Alarm Sorguları
    @Query("SELECT * FROM pro_watchlist_alerts WHERE is_enabled = 1")
    fun getActiveAlertStubs(): Flow<List<WatchlistAlertStubEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlertStub(alert: WatchlistAlertStubEntity)
}
