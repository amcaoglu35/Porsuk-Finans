package com.nexus.porsuk.data.local.dao

import androidx.room.*
import com.nexus.porsuk.data.local.entity.MarketQuoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MarketQuoteDao {
    @Query("SELECT * FROM market_quotes")
    fun getAllQuotes(): Flow<List<MarketQuoteEntity>>

    @Query("SELECT * FROM market_quotes WHERE symbol = :symbol LIMIT 1")
    suspend fun getQuoteBySymbol(symbol: String): MarketQuoteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateQuotes(quotes: List<MarketQuoteEntity>)
}
