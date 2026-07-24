package com.nexus.porsuk.data.local.dao

import androidx.room.*
import com.nexus.porsuk.data.local.entity.*
import kotlinx.coroutines.flow.Flow

/**
 * Porsuk Finans — Fiyat Alarmları DAO
 */
@Dao
interface AlarmDao {
    @Query("SELECT * FROM db_alarms ORDER BY created_date DESC")
    fun getAllAlarms(): Flow<List<AlarmEntity>>

    @Query("SELECT * FROM db_alarms WHERE is_enabled = 1")
    fun getActiveAlarms(): Flow<List<AlarmEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarm: AlarmEntity)

    @Update
    suspend fun updateAlarm(alarm: AlarmEntity)

    @Query("UPDATE db_alarms SET is_enabled = :isEnabled WHERE id = :alarmId")
    suspend fun setAlarmEnabled(alarmId: Long, isEnabled: Boolean)

    @Delete
    suspend fun deleteAlarm(alarm: AlarmEntity)
}

/**
 * Porsuk Finans — Finansal Haberler DAO
 */
@Dao
interface NewsDao {
    @Query("SELECT * FROM db_news ORDER BY published_at DESC LIMIT 100")
    fun getLatestNews(): Flow<List<NewsEntity>>

    @Query("SELECT * FROM db_news WHERE symbol = :symbol ORDER BY published_at DESC")
    fun getNewsForSymbol(symbol: String): Flow<List<NewsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(articles: List<NewsEntity>)

    @Query("DELETE FROM db_news")
    suspend fun deleteAllNews()
}

/**
 * Porsuk Finans — Temettü Takvimi DAO
 */
@Dao
interface DividendDao {
    @Query("SELECT * FROM db_dividends WHERE ex_date >= :fromDate ORDER BY ex_date ASC")
    fun getUpcomingDividends(fromDate: Long = System.currentTimeMillis()): Flow<List<DividendEntity>>

    @Query("SELECT * FROM db_dividends WHERE symbol = :symbol ORDER BY ex_date DESC")
    fun getDividendsForSymbol(symbol: String): Flow<List<DividendEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDividends(dividends: List<DividendEntity>)
}

/**
 * Porsuk Finans — Bilanço Raporları DAO
 */
@Dao
interface EarningsDao {
    @Query("SELECT * FROM db_earnings WHERE report_date >= :fromDate ORDER BY report_date ASC")
    fun getUpcomingEarnings(fromDate: Long = System.currentTimeMillis()): Flow<List<EarningsEntity>>

    @Query("SELECT * FROM db_earnings WHERE symbol = :symbol ORDER BY report_date DESC")
    fun getEarningsForSymbol(symbol: String): Flow<List<EarningsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEarnings(earningsList: List<EarningsEntity>)
}

/**
 * Porsuk Finans — Orakul AI Analiz Geçmişi DAO
 */
@Dao
interface AIHistoryDao {
    @Query("SELECT * FROM db_ai_history WHERE symbol = :symbol ORDER BY analysis_date DESC LIMIT 1")
    fun getLatestAiAnalysis(symbol: String): Flow<AIHistoryEntity?>

    @Query("SELECT * FROM db_ai_history ORDER BY analysis_date DESC LIMIT 50")
    fun getRecentAiAnalyses(): Flow<List<AIHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAiAnalysis(analysis: AIHistoryEntity)
}

/**
 * Porsuk Finans — Uygulama Tercihleri ve Ayarları DAO
 */
@Dao
interface AppSettingsDao {
    @Query("SELECT * FROM db_app_settings WHERE id = 1 LIMIT 1")
    fun getAppSettings(): Flow<AppSettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateAppSettings(settings: AppSettingsEntity)
}
