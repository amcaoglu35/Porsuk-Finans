package com.nexus.porsuk.data.repository

import com.nexus.porsuk.data.local.dao.CalendarDao
import com.nexus.porsuk.data.local.entity.DividendCalendarProEntity
import com.nexus.porsuk.data.local.entity.EarningsCalendarEntity
import com.nexus.porsuk.data.local.entity.EconomicEventEntity
import com.nexus.porsuk.data.remote.CalendarAiEngine
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalendarRepositoryImpl @Inject constructor(
    private val dao: CalendarDao,
    private val aiEngine: CalendarAiEngine
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

    override suspend fun refreshEvents() {
        // Mocking refresh logic - in real app, fetch from API
    }

    override suspend fun getAiImpactAnalysis(eventId: String): AiEventImpact? {
        val allEvents = getAllEvents().first()
        val event = allEvents.find { it.eventId == eventId } ?: return null
        
        if (event.aiEvaluation != null) return event.aiEvaluation

        // Perform AI analysis if not already cached
        val analysis = aiEngine.analyzeEventImpact(event)
        if (analysis != null) {
            val json = JSONObject().apply {
                put("eventId", analysis.eventId)
                put("expectedImpact", analysis.expectedImpact)
                put("affectedSectors", analysis.affectedSectors)
                put("riskLevel", analysis.riskLevel)
                put("opportunityLevel", analysis.opportunityLevel)
                put("aiCommentary", analysis.aiCommentary)
            }.toString()
            dao.updateEconomicAiImpact(eventId, json)
        }
        return analysis
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
    symbol = symbol,
    aiEvaluation = parseAiImpact(aiImpactJson)
)

private fun EarningsCalendarEntity.toDomainModel() = EarningsEvent(
    earningsId = earningsId,
    symbol = symbol,
    companyName = companyName,
    reportDate = reportDate,
    epsForecast = epsForecast,
    epsActual = epsActual,
    revenueForecast = revenueForecast,
    revenueActual = revenueActual,
    aiEvaluation = parseAiImpact(aiImpactJson)
)

private fun DividendCalendarProEntity.toDomainModel() = DividendEvent(
    dividendId = dividendId,
    symbol = symbol,
    companyName = companyName,
    exDate = exDate,
    paymentDate = paymentDate,
    amount = amount,
    currency = currency,
    aiEvaluation = parseAiImpact(aiImpactJson)
)

private fun parseAiImpact(json: String?): AiEventImpact? {
    if (json == null) return null
    return try {
        val obj = JSONObject(json)
        val sectors = mutableListOf<String>()
        val arr = obj.getJSONArray("affectedSectors")
        for (i in 0 until arr.length()) sectors.add(arr.getString(i))
        
        AiEventImpact(
            eventId = obj.getString("eventId"),
            expectedImpact = obj.getString("expectedImpact"),
            realizedImpact = if (obj.has("realizedImpact")) obj.getString("realizedImpact") else null,
            affectedSectors = sectors,
            riskLevel = obj.getInt("riskLevel"),
            opportunityLevel = obj.getInt("opportunityLevel"),
            aiCommentary = obj.getString("aiCommentary")
        )
    } catch (e: Exception) {
        null
    }
}
