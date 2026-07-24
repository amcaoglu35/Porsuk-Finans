package com.nexus.porsuk.domain.repository

import com.nexus.porsuk.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * 1. Portföy Yönetim Deposu Sözleşmesi (PortfolioRepository)
 */
interface PortfolioEngineRepository {
    fun getAllPortfolios(): Flow<List<PortfolioSummary>>
    fun getPortfolioById(portfolioId: String): Flow<PortfolioSummary?>
    suspend fun createPortfolio(name: String, description: String, type: PortfolioType, currency: String): String
    suspend fun updatePortfolio(portfolio: PortfolioSummary)
    suspend fun deletePortfolio(portfolioId: String)
}

/**
 * 2. İşlem Geçmişi Deposu Sözleşmesi (TransactionRepository)
 */
interface PortfolioTransactionRepository {
    fun getTransactionsForPortfolio(portfolioId: String): Flow<List<PortfolioTransaction>>
    fun getTransactionsForAsset(portfolioId: String, symbol: String): Flow<List<PortfolioTransaction>>
    suspend fun addTransaction(transaction: PortfolioTransaction)
    suspend fun deleteTransaction(transactionId: Long)
}

/**
 * 3. Portföy Varlık Kalemleri Deposu Sözleşmesi (AssetRepository)
 */
interface PortfolioAssetRepository {
    fun getAssetsForPortfolio(portfolioId: String): Flow<List<PortfolioAsset>>
    suspend fun updateAssetPrice(portfolioId: String, symbol: String, currentPrice: Double)
    suspend fun deleteAsset(portfolioId: String, symbol: String)
}

/**
 * 4. Performans ve Dağılım Analiz Deposu Sözleşmesi (PerformanceRepository)
 */
interface PortfolioPerformanceRepository {
    fun getPortfolioBreakdown(portfolioId: String): Flow<PortfolioBreakdown>
    suspend fun getRealizedProfitLoss(portfolioId: String): Double
}
