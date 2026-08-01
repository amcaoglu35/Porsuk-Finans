package com.nexus.porsuk.core.domain.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StockDao {
    @Query("SELECT * FROM stocks")
    fun getAllStocks(): Flow<List<StockEntity>>

    @Query("SELECT * FROM stocks WHERE symbol = :symbol LIMIT 1")
    suspend fun getStockBySymbol(symbol: String): StockEntity?

    @Query("SELECT * FROM stocks WHERE isFavorite = 1")
    fun getFavoriteStocks(): Flow<List<StockEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStocks(stocks: List<StockEntity>): Unit

    @Query("UPDATE stocks SET price = :price WHERE symbol = :symbol")
    suspend fun updateStockPrice(symbol: String, price: Double): Int

    @Query("UPDATE stocks SET isFavorite = :isFavorite WHERE symbol = :symbol")
    suspend fun updateFavoriteStatus(symbol: String, isFavorite: Boolean): Int
}

@Dao
interface PortfolioDao {
    @Query("SELECT * FROM portfolio_baskets ORDER BY createdTimestamp DESC")
    fun getAllBaskets(): Flow<List<PortfolioBasketEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBasket(basket: PortfolioBasketEntity): Unit

    @Query("DELETE FROM portfolio_baskets WHERE id = :id")
    suspend fun deleteBasket(id: String): Int
}

@Dao
interface KapNoticeDao {
    @Query("SELECT * FROM kap_notices ORDER BY date DESC")
    fun getAllKapNotices(): Flow<List<KapNoticeEntity>>

    @Query("SELECT * FROM kap_notices WHERE category = :category ORDER BY date DESC")
    fun getKapNoticesByCategory(category: String): Flow<List<KapNoticeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKapNotices(notices: List<KapNoticeEntity>): Unit
}
