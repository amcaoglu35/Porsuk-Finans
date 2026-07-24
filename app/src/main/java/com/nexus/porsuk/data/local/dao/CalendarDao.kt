package com.nexus.porsuk.data.local.dao

import androidx.room.*
import com.nexus.porsuk.data.local.entity.DividendCalendarProEntity
import com.nexus.porsuk.data.local.entity.EarningsCalendarEntity
import com.nexus.porsuk.data.local.entity.EconomicEventEntity
import kotlinx.coroutines.flow.Flow

/**
 * Porsuk Economic Calendar & Events Engine — Room DAO Sorguları
 */
@Dao
interface CalendarDao {

    // Ekonomik Etkinlik Sorguları
    @Query("SELECT * FROM engine_economic_events ORDER BY event_time ASC")
    fun getAllEconomicEvents(): Flow<List<EconomicEventEntity>>

    @Query("SELECT * FROM engine_economic_events WHERE category = :category ORDER BY event_time ASC")
    fun getEventsByCategory(category: String): Flow<List<EconomicEventEntity>>

    @Query("SELECT * FROM engine_economic_events WHERE country = :country ORDER BY event_time ASC")
    fun getEventsByCountry(country: String): Flow<List<EconomicEventEntity>>

    @Query("SELECT * FROM engine_economic_events WHERE impact_level = :impactLevel ORDER BY event_time ASC")
    fun getEventsByImpactLevel(impactLevel: String): Flow<List<EconomicEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEconomicEvent(event: EconomicEventEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEconomicEvents(events: List<EconomicEventEntity>)

    // Bilanço Takvimi Sorguları
    @Query("SELECT * FROM engine_earnings_calendar ORDER BY report_date ASC")
    fun getAllEarningsEvents(): Flow<List<EarningsCalendarEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEarningsEvents(events: List<EarningsCalendarEntity>)

    // Temettü Takvimi Sorguları
    @Query("SELECT * FROM engine_dividend_calendar_pro ORDER BY ex_date ASC")
    fun getAllDividendEvents(): Flow<List<DividendCalendarProEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDividendEvents(events: List<DividendCalendarProEntity>)
}
