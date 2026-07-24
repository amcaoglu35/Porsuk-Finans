package com.nexus.porsuk.domain.repository

import com.nexus.porsuk.data.local.entity.*
import kotlinx.coroutines.flow.Flow

/**
 * Porsuk Finans — Portföy Varlıkları Repository Sözleşmesi
 */
interface PortfolioHoldingRepository {
    fun getAllHoldings(): Flow<List<PortfolioHoldingEntity>>
    fun getTotalPortfolioValue(): Flow<Double?>
    fun getTotalProfitLoss(): Flow<Double?>
    suspend fun insertHolding(holding: PortfolioHoldingEntity)
    suspend fun deleteHolding(holding: PortfolioHoldingEntity)
}

/**
 * Porsuk Finans — Takip Listesi Repository Sözleşmesi
 */
interface WatchlistRepository {
    fun getAllWatchlistItems(): Flow<List<WatchlistItemEntity>>
    fun isInWatchlist(symbol: String): Flow<Boolean>
    suspend fun addWatchlistItem(symbol: String, notes: String? = null)
    suspend fun removeWatchlistItem(symbol: String)
}

/**
 * Porsuk Finans — Fiyat Alarmları Repository Sözleşmesi
 */
interface AlarmRepository {
    fun getAllAlarms(): Flow<List<AlarmEntity>>
    fun getActiveAlarms(): Flow<List<AlarmEntity>>
    suspend fun insertAlarm(alarm: AlarmEntity)
    suspend fun setAlarmEnabled(alarmId: Long, isEnabled: Boolean)
    suspend fun deleteAlarm(alarm: AlarmEntity)
}

/**
 * Porsuk Finans — Finansal Haberler Repository Sözleşmesi
 */
interface NewsRepository {
    fun getLatestNews(): Flow<List<NewsEntity>>
    fun getNewsForSymbol(symbol: String): Flow<List<NewsEntity>>
    suspend fun insertNews(articles: List<NewsEntity>)
}

/**
 * Porsuk Finans — Bilanço Raporları Repository Sözleşmesi
 */
interface EarningsRepository {
    fun getUpcomingEarnings(): Flow<List<EarningsEntity>>
    fun getEarningsForSymbol(symbol: String): Flow<List<EarningsEntity>>
    suspend fun insertEarnings(earningsList: List<EarningsEntity>)
}

/**
 * Porsuk Finans — Orakul AI Analiz Geçmişi Repository Sözleşmesi
 */
interface AIHistoryRepository {
    fun getLatestAiAnalysis(symbol: String): Flow<AIHistoryEntity?>
    fun getRecentAiAnalyses(): Flow<List<AIHistoryEntity>>
    suspend fun insertAiAnalysis(analysis: AIHistoryEntity)
}

/**
 * Porsuk Finans — Uygulama Tercihleri ve Ayarlar Repository Sözleşmesi
 */
interface AppSettingsRepository {
    fun getAppSettings(): Flow<AppSettingsEntity?>
    suspend fun updateAppSettings(settings: AppSettingsEntity)
}
