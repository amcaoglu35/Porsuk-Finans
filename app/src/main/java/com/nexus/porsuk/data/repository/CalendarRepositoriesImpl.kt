package com.nexus.porsuk.data.repository

import com.nexus.porsuk.data.local.dao.CalendarDao
import com.nexus.porsuk.data.local.entity.DividendCalendarProEntity
import com.nexus.porsuk.data.local.entity.EarningsCalendarEntity
import com.nexus.porsuk.data.local.entity.EconomicEventEntity
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalendarRepositoryImpl @Inject constructor(
    private val dao: CalendarDao
) : CalendarRepository {

    override fun getAllEvents(): Flow<List<EconomicEvent>> {
        return dao.getAllEconomicEvents().map { list -> list.map { it.toDomainModel() } }
    }

    override fun getEventsByCategory(category: CalendarEventCategory): Flow<List<EconomicEvent>> {
        return if (category == CalendarEventCategory.ALL) {
            getAllEvents()
        } else {
            dao.getEventsByCategory(category.name).map { list -> list.map { it.toDomainModel() } }
        }
    }

    override fun getEventsByCountry(country: String): Flow<List<EconomicEvent>> {
        return dao.getEventsByCountry(country).map { list -> list.map { it.toDomainModel() } }
    }

    override fun getEventsByImpactLevel(impactLevel: CalendarImpactLevel): Flow<List<EconomicEvent>> {
        return dao.getEventsByImpactLevel(impactLevel.name).map { list -> list.map { it.toDomainModel() } }
    }
}

@Singleton
class EconomicRepositoryImpl @Inject constructor(
    private val dao: CalendarDao
) : EconomicRepository {

    override fun getCentralBankDecisions(): Flow<List<EconomicEvent>> {
        return dao.getEventsByCategory(CalendarEventCategory.MACRO.name).map { list -> list.map { it.toDomainModel() } }
    }

    override fun getMacroEconomicData(): Flow<List<EconomicEvent>> {
        return dao.getEventsByCategory(CalendarEventCategory.ECONOMIC_DATA.name).map { list -> list.map { it.toDomainModel() } }
    }
}

@Singleton
class EarningsCalendarRepositoryImpl @Inject constructor(
    private val dao: CalendarDao
) : EarningsCalendarRepository {

    override fun getAllEarningsEvents(): Flow<List<EarningsEvent>> {
        return dao.getAllEarningsEvents().map { list -> list.map { it.toDomainModel() } }
    }
}

@Singleton
class DividendCalendarRepositoryImpl @Inject constructor(
    private val dao: CalendarDao
) : DividendCalendarRepository {

    override fun getAllDividendEvents(): Flow<List<DividendEvent>> {
        return dao.getAllDividendEvents().map { list -> list.map { it.toDomainModel() } }
    }
}

// Mappers
private fun EconomicEventEntity.toDomainModel() = EconomicEvent(
    eventId = eventId,
    title = title,
    country = country,
    category = CalendarEventCategory.fromString(category),
    impactLevel = try { CalendarImpactLevel.valueOf(impactLevel) } catch (e: Exception) { CalendarImpactLevel.MEDIUM },
    actualValue = actualValue,
    forecastValue = forecastValue,
    previousValue = previousValue,
    eventTime = eventTime,
    symbol = symbol
)

private fun EarningsCalendarEntity.toDomainModel() = EarningsEvent(
    earningsId = earningsId,
    symbol = symbol,
    companyName = companyName,
    reportDate = reportDate,
    epsForecast = epsForecast,
    epsActual = epsActual,
    revenueForecast = revenueForecast,
    revenueActual = revenueActual
)

private fun DividendCalendarProEntity.toDomainModel() = DividendEvent(
    dividendId = dividendId,
    symbol = symbol,
    companyName = companyName,
    exDate = exDate,
    paymentDate = paymentDate,
    amount = amount,
    currency = currency
)
