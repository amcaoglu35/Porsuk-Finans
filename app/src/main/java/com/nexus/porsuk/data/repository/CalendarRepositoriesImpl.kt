package com.nexus.porsuk.data.repository

import com.nexus.porsuk.core.common.NetworkResult
import com.nexus.porsuk.data.local.dao.CalendarDao
import com.nexus.porsuk.data.local.entity.EconomicEventEntity
import com.nexus.porsuk.data.remote.datasource.FinnhubRemoteDataSource
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalendarRepositoryImpl @Inject constructor(
    private val dao: CalendarDao,
    private val remoteDataSource: FinnhubRemoteDataSource
) : CalendarRepository {

    override fun getAllEvents(): Flow<List<EconomicEvent>> {
        return dao.getAllEconomicEvents().map { list -> list.map { it.toDomainModel() } }
    }

    override fun getEventsByCategory(category: CalendarEventCategory): Flow<List<EconomicEvent>> {
        return dao.getEventsByCategory(category.name).map { list -> list.map { it.toDomainModel() } }
    }

    override fun getEventsByCountry(country: String): Flow<List<EconomicEvent>> {
        return dao.getEventsByCountry(country).map { list -> list.map { it.toDomainModel() } }
    }

    override fun getEventsByImpactLevel(impactLevel: CalendarImpactLevel): Flow<List<EconomicEvent>> {
        return dao.getEventsByImpactLevel(impactLevel.name).map { list -> list.map { it.toDomainModel() } }
    }

    override suspend fun refreshEvents() {
        val result = remoteDataSource.fetchEconomicCalendar()
        if (result is NetworkResult.Success) {
            val entities = result.data.economicCalendar.map { dto ->
                EconomicEventEntity(
                    eventId = UUID.randomUUID().toString(),
                    title = dto.event,
                    country = dto.country,
                    category = "ECONOMIC_DATA",
                    impactLevel = dto.impact.uppercase(),
                    actualValue = dto.actual?.toString(),
                    forecastValue = dto.estimate?.toString(),
                    previousValue = dto.prev?.toString(),
                    eventTime = parseTime(dto.time)
                )
            }
            dao.insertEconomicEvents(entities)
        }
    }

    override suspend fun getAiImpactAnalysis(eventId: String): AiEventImpact? {
        // Implementation with Gemini
        return null
    }

    private fun parseTime(timeStr: String): Long {
        return try {
            java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.US).parse(timeStr)?.time ?: System.currentTimeMillis()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }
}

@Singleton
class EconomicRepositoryImpl @Inject constructor(
    private val dao: CalendarDao
) : EconomicRepository {
    override fun getCentralBankDecisions(): Flow<List<EconomicEvent>> {
        return dao.getEventsByCategory("CENTRAL_BANK").map { list -> list.map { it.toDomainModel() } }
    }

    override fun getMacroEconomicData(): Flow<List<EconomicEvent>> {
        return dao.getEventsByCategory("MACRO").map { list -> list.map { it.toDomainModel() } }
    }
}

@Singleton
class EarningsCalendarRepositoryImpl @Inject constructor(
    private val dao: CalendarDao
) : EarningsCalendarRepository {
    override fun getAllEarningsEvents(): Flow<List<EarningsEvent>> {
        return dao.getAllEarningsEvents().map { list -> 
            list.map { EarningsEvent(it.earningsId, it.symbol, it.companyName, it.reportDate, it.epsForecast, it.epsActual, it.revenueForecast, it.revenueActual) }
        }
    }
}

@Singleton
class DividendCalendarRepositoryImpl @Inject constructor(
    private val dao: CalendarDao
) : DividendCalendarRepository {
    override fun getAllDividendEvents(): Flow<List<DividendEvent>> {
        return dao.getAllDividendEvents().map { list ->
            list.map { DividendEvent(it.dividendId, it.symbol, it.companyName, it.exDate, it.paymentDate, it.amount, it.currency) }
        }
    }
}

// Mappers
private fun EconomicEventEntity.toDomainModel() = EconomicEvent(
    eventId = eventId,
    title = title,
    country = country,
    category = try { CalendarEventCategory.valueOf(category) } catch (e: Exception) { CalendarEventCategory.ECONOMIC_DATA },
    impactLevel = try { CalendarImpactLevel.valueOf(impactLevel) } catch (e: Exception) { CalendarImpactLevel.LOW },
    actualValue = actualValue,
    forecastValue = forecastValue,
    previousValue = previousValue,
    eventTime = eventTime
)
