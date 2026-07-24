package com.nexus.porsuk.data.repository

import com.nexus.porsuk.data.engine.DividendIncomeProjectionEngine
import com.nexus.porsuk.data.engine.DividendScoringEngine
import com.nexus.porsuk.data.local.dao.DividendIntelligenceDao
import com.nexus.porsuk.data.local.entity.DividendIntelligenceEntity
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DividendRepositoryImpl @Inject constructor(
    private val scoringEngine: DividendScoringEngine,
    private val dao: DividendIntelligenceDao
) : DividendRepository {

    override fun getDividendStocks(): Flow<List<DividendStockItem>> = flow {
        emit(scoringEngine.getSampleDividendStocks())
    }

    override fun getFavoriteDividendStocks(): Flow<List<DividendStockItem>> {
        return dao.getAllFavoriteDividendStocks().map { list ->
            list.map { entity ->
                DividendStockItem(
                    symbol = entity.symbol,
                    companyName = entity.companyName,
                    marketType = ScanMarketType.BIST,
                    lastPrice = 100.0,
                    dividendYieldPct = entity.dividendYieldPct,
                    annualDividendUsd = entity.annualDividendUsd,
                    payoutRatioPct = 40.0,
                    exDividendDateText = "15 Nisan 2026",
                    paymentDateText = "22 Nisan 2026",
                    frequency = DividendFrequency.ANNUAL,
                    scores = DividendQualityScores(safetyScore = entity.safetyScore)
                )
            }
        }
    }

    override suspend fun saveFavoriteStock(item: DividendStockItem) {
        val entity = DividendIntelligenceEntity(
            symbol = item.symbol,
            companyName = item.companyName,
            dividendYieldPct = item.dividendYieldPct,
            annualDividendUsd = item.annualDividendUsd,
            safetyScore = item.scores.safetyScore
        )
        dao.insertDividendStock(entity)
    }
}

@Singleton
class DividendIntelligenceCalendarRepositoryImpl @Inject constructor(
    private val scoringEngine: DividendScoringEngine
) : DividendIntelligenceCalendarRepository {
    override fun getUpcomingDividends(): Flow<List<DividendStockItem>> = flow {
        emit(scoringEngine.getSampleDividendStocks())
    }
}

@Singleton
class DividendAnalyticsRepositoryImpl @Inject constructor(
    private val scoringEngine: DividendScoringEngine
) : DividendAnalyticsRepository {
    override fun getDividendQualityScores(symbol: String): Flow<DividendQualityScores> = flow {
        emit(scoringEngine.calculateScores(45.0, 12.5))
    }
}

@Singleton
class DividendPortfolioRepositoryImpl @Inject constructor(
    private val projectionEngine: DividendIncomeProjectionEngine
) : DividendPortfolioRepository {
    override fun getIncomeProjection(): Flow<IncomeProjection> = flow {
        emit(projectionEngine.calculateIncomeProjection())
    }
}
