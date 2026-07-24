package com.nexus.porsuk.data.repository

import com.nexus.porsuk.data.local.dao.*
import com.nexus.porsuk.data.local.entity.*
import com.nexus.porsuk.domain.repository.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PortfolioHoldingRepositoryImpl @Inject constructor(
    private val portfolioHoldingDao: PortfolioHoldingDao
) : PortfolioHoldingRepository {
    override fun getAllHoldings(): Flow<List<PortfolioHoldingEntity>> = portfolioHoldingDao.getAllHoldings()
    override fun getTotalPortfolioValue(): Flow<Double?> = portfolioHoldingDao.getTotalPortfolioValue()
    override fun getTotalProfitLoss(): Flow<Double?> = portfolioHoldingDao.getTotalProfitLoss()
    override suspend fun insertHolding(holding: PortfolioHoldingEntity) = portfolioHoldingDao.insertHolding(holding)
    override suspend fun deleteHolding(holding: PortfolioHoldingEntity) = portfolioHoldingDao.deleteHolding(holding)
}

@Singleton
class WatchlistRepositoryImpl @Inject constructor(
    private val watchlistDao: WatchlistItemDao
) : WatchlistRepository {
    override fun getAllWatchlistItems(): Flow<List<WatchlistItemEntity>> = watchlistDao.getAllWatchlistItems()
    override fun isInWatchlist(symbol: String): Flow<Boolean> = watchlistDao.isInWatchlist(symbol)
    override suspend fun addWatchlistItem(symbol: String, notes: String?) {
        watchlistDao.insertWatchlistItem(WatchlistItemEntity(symbol = symbol, notes = notes))
    }
    override suspend fun removeWatchlistItem(symbol: String) = watchlistDao.deleteBySymbol(symbol)
}

@Singleton
class AlarmRepositoryImpl @Inject constructor(
    private val alarmDao: AlarmDao
) : AlarmRepository {
    override fun getAllAlarms(): Flow<List<AlarmEntity>> = alarmDao.getAllAlarms()
    override fun getActiveAlarms(): Flow<List<AlarmEntity>> = alarmDao.getActiveAlarms()
    override suspend fun insertAlarm(alarm: AlarmEntity) = alarmDao.insertAlarm(alarm)
    override suspend fun setAlarmEnabled(alarmId: Long, isEnabled: Boolean) = alarmDao.setAlarmEnabled(alarmId, isEnabled)
    override suspend fun deleteAlarm(alarm: AlarmEntity) = alarmDao.deleteAlarm(alarm)
}

@Singleton
class NewsRepositoryImpl @Inject constructor(
    private val newsDao: NewsDao
) : NewsRepository {
    override fun getLatestNews(): Flow<List<NewsEntity>> = newsDao.getLatestNews()
    override fun getNewsForSymbol(symbol: String): Flow<List<NewsEntity>> = newsDao.getNewsForSymbol(symbol)
    override suspend fun insertNews(articles: List<NewsEntity>) = newsDao.insertNews(articles)
}

@Singleton
class EarningsRepositoryImpl @Inject constructor(
    private val earningsDao: EarningsDao
) : EarningsRepository {
    override fun getUpcomingEarnings(): Flow<List<EarningsEntity>> = earningsDao.getUpcomingEarnings()
    override fun getEarningsForSymbol(symbol: String): Flow<List<EarningsEntity>> = earningsDao.getEarningsForSymbol(symbol)
    override suspend fun insertEarnings(earningsList: List<EarningsEntity>) = earningsDao.insertEarnings(earningsList)
}

@Singleton
class AIHistoryRepositoryImpl @Inject constructor(
    private val aiHistoryDao: AIHistoryDao
) : AIHistoryRepository {
    override fun getLatestAiAnalysis(symbol: String): Flow<AIHistoryEntity?> = aiHistoryDao.getLatestAiAnalysis(symbol)
    override fun getRecentAiAnalyses(): Flow<List<AIHistoryEntity>> = aiHistoryDao.getRecentAiAnalyses()
    override suspend fun insertAiAnalysis(analysis: AIHistoryEntity) = aiHistoryDao.insertAiAnalysis(analysis)
}

@Singleton
class AppSettingsRepositoryImpl @Inject constructor(
    private val appSettingsDao: AppSettingsDao
) : AppSettingsRepository {
    override fun getAppSettings(): Flow<AppSettingsEntity?> = appSettingsDao.getAppSettings()
    override suspend fun updateAppSettings(settings: AppSettingsEntity) = appSettingsDao.updateAppSettings(settings)
}
