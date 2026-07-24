package com.nexus.porsuk.domain.repository

import com.nexus.porsuk.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * 1. Genel Takvim Deposu Sözleşmesi (CalendarRepository)
 */
interface CalendarRepository {
    fun getAllEvents(): Flow<List<EconomicEvent>>
    fun getEventsByCategory(category: CalendarEventCategory): Flow<List<EconomicEvent>>
    fun getEventsByCountry(country: String): Flow<List<EconomicEvent>>
    fun getEventsByImpactLevel(impactLevel: CalendarImpactLevel): Flow<List<EconomicEvent>>
}

/**
 * 2. Makro Ekonomi ve Merkez Bankaları Deposu Sözleşmesi (EconomicRepository)
 */
interface EconomicRepository {
    fun getCentralBankDecisions(): Flow<List<EconomicEvent>>
    fun getMacroEconomicData(): Flow<List<EconomicEvent>>
}

/**
 * 3. Şirket Bilanço Takvimi Deposu Sözleşmesi (EarningsCalendarRepository)
 */
interface EarningsCalendarRepository {
    fun getAllEarningsEvents(): Flow<List<EarningsEvent>>
}

/**
 * 4. Temettü Takvimi Deposu Sözleşmesi (DividendCalendarRepository)
 */
interface DividendCalendarRepository {
    fun getAllDividendEvents(): Flow<List<DividendEvent>>
}
