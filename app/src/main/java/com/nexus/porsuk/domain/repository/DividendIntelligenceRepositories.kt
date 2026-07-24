package com.nexus.porsuk.domain.repository

import com.nexus.porsuk.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * 1. Temettü Deposu Sözleşmesi (DividendRepository)
 */
interface DividendRepository {
    fun getDividendStocks(): Flow<List<DividendStockItem>>
    fun getFavoriteDividendStocks(): Flow<List<DividendStockItem>>
    suspend fun saveFavoriteStock(item: DividendStockItem)
}

/**
 * 2. Temettü İstihbarat Takvim Deposu Sözleşmesi (DividendIntelligenceCalendarRepository)
 */
interface DividendIntelligenceCalendarRepository {
    fun getUpcomingDividends(): Flow<List<DividendStockItem>>
}

/**
 * 3. Temettü Analitik Deposu Sözleşmesi (DividendAnalyticsRepository)
 */
interface DividendAnalyticsRepository {
    fun getDividendQualityScores(symbol: String): Flow<DividendQualityScores>
}

/**
 * 4. Temettü Portföy Deposu Sözleşmesi (DividendPortfolioRepository)
 */
interface DividendPortfolioRepository {
    fun getIncomeProjection(): Flow<IncomeProjection>
}
