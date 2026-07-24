package com.nexus.porsuk.data.local.dao

import androidx.room.*
import com.nexus.porsuk.data.local.entity.PortfolioHoldingEntity
import com.nexus.porsuk.data.local.entity.WatchlistItemEntity
import kotlinx.coroutines.flow.Flow

/**
 * Porsuk Finans — Portföy Varlıkları DAO
 */
@Dao
interface PortfolioHoldingDao {

    @Query("SELECT * FROM db_portfolio_holdings ORDER BY total_value DESC")
    fun getAllHoldings(): Flow<List<PortfolioHoldingEntity>>

    @Query("SELECT * FROM db_portfolio_holdings WHERE symbol = :symbol LIMIT 1")
    suspend fun getHoldingBySymbol(symbol: String): PortfolioHoldingEntity?

    @Query("SELECT SUM(total_value) FROM db_portfolio_holdings")
    fun getTotalPortfolioValue(): Flow<Double?>

    @Query("SELECT SUM(profit_loss) FROM db_portfolio_holdings")
    fun getTotalProfitLoss(): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHolding(holding: PortfolioHoldingEntity)

    @Update
    suspend fun updateHolding(holding: PortfolioHoldingEntity)

    @Delete
    suspend fun deleteHolding(holding: PortfolioHoldingEntity)

    @Query("DELETE FROM db_portfolio_holdings")
    suspend fun deleteAllHoldings()
}

/**
 * Porsuk Finans — Takip Listesi DAO
 */
@Dao
interface WatchlistItemDao {

    @Query("SELECT * FROM db_watchlist_items ORDER BY added_date DESC")
    fun getAllWatchlistItems(): Flow<List<WatchlistItemEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM db_watchlist_items WHERE symbol = :symbol)")
    fun isInWatchlist(symbol: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchlistItem(item: WatchlistItemEntity)

    @Query("DELETE FROM db_watchlist_items WHERE symbol = :symbol")
    suspend fun deleteBySymbol(symbol: String)

    @Delete
    suspend fun deleteWatchlistItem(item: WatchlistItemEntity)
}
