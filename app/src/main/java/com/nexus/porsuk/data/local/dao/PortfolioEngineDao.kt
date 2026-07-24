package com.nexus.porsuk.data.local.dao

import androidx.room.*
import com.nexus.porsuk.data.local.entity.PortfolioAssetEntity
import com.nexus.porsuk.data.local.entity.PortfolioEngineEntity
import com.nexus.porsuk.data.local.entity.PortfolioTransactionEntity
import kotlinx.coroutines.flow.Flow

/**
 * Porsuk Portfolio Engine — Room DAO Sorguları
 */
@Dao
interface PortfolioEngineDao {

    // Portföy Sorguları
    @Query("SELECT * FROM engine_portfolios WHERE is_active = 1 ORDER BY created_at DESC")
    fun getAllPortfolios(): Flow<List<PortfolioEngineEntity>>

    @Query("SELECT * FROM engine_portfolios WHERE portfolio_id = :portfolioId LIMIT 1")
    fun getPortfolioById(portfolioId: String): Flow<PortfolioEngineEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPortfolio(portfolio: PortfolioEngineEntity)

    @Update
    suspend fun updatePortfolio(portfolio: PortfolioEngineEntity)

    @Delete
    suspend fun deletePortfolio(portfolio: PortfolioEngineEntity)

    // Varlık Kalemi Sorguları
    @Query("SELECT * FROM engine_portfolio_assets WHERE portfolio_id = :portfolioId ORDER BY total_value DESC")
    fun getAssetsForPortfolio(portfolioId: String): Flow<List<PortfolioAssetEntity>>

    @Query("SELECT * FROM engine_portfolio_assets WHERE portfolio_id = :portfolioId AND symbol = :symbol LIMIT 1")
    suspend fun getAssetBySymbol(portfolioId: String, symbol: String): PortfolioAssetEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAsset(asset: PortfolioAssetEntity)

    @Update
    suspend fun updateAsset(asset: PortfolioAssetEntity)

    @Delete
    suspend fun deleteAsset(asset: PortfolioAssetEntity)

    // İşlem Geçmişi Sorguları
    @Query("SELECT * FROM engine_portfolio_transactions WHERE portfolio_id = :portfolioId ORDER BY timestamp DESC")
    fun getTransactionsForPortfolio(portfolioId: String): Flow<List<PortfolioTransactionEntity>>

    @Query("SELECT * FROM engine_portfolio_transactions WHERE portfolio_id = :portfolioId AND symbol = :symbol ORDER BY timestamp DESC")
    fun getTransactionsForAsset(portfolioId: String, symbol: String): Flow<List<PortfolioTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: PortfolioTransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: PortfolioTransactionEntity)
}
