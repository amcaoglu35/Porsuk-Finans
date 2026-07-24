package com.nexus.porsuk.data.local.dao

import androidx.room.*
import com.nexus.porsuk.data.local.entity.DividendIntelligenceEntity
import kotlinx.coroutines.flow.Flow

/**
 * Porsuk Dividend Intelligence Center — Room DAO Sorguları
 */
@Dao
interface DividendIntelligenceDao {

    @Query("SELECT * FROM engine_dividend_intelligence_watchlist ORDER BY dividend_yield_pct DESC")
    fun getAllFavoriteDividendStocks(): Flow<List<DividendIntelligenceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDividendStock(item: DividendIntelligenceEntity)

    @Query("DELETE FROM engine_dividend_intelligence_watchlist WHERE symbol = :symbol")
    suspend fun deleteDividendStock(symbol: String)
}
