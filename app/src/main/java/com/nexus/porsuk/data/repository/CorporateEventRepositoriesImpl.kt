package com.nexus.porsuk.data.repository

import com.nexus.porsuk.data.ma.CorporateEventEngine
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CorporateEventRepositoryImpl @Inject constructor() : CorporateEventRepository {

    override fun getCorporateEvents(symbol: String): Flow<List<CorporateEvent>> = MutableStateFlow<List<CorporateEvent>>(emptyList()).asStateFlow()

    override fun getEventsByType(eventType: CorporateEventType): Flow<List<CorporateEvent>> {
        return MutableStateFlow<List<CorporateEvent>>(emptyList()).asStateFlow()
    }

    override fun getUpcomingEvents(): Flow<List<CorporateEvent>> {
        return MutableStateFlow<List<CorporateEvent>>(emptyList()).asStateFlow()
    }
}

@Singleton
class MergerRepositoryImpl @Inject constructor() : MergerRepository {

    override fun getActiveMergers(): Flow<List<DealAnalyticsItem>> = MutableStateFlow<List<DealAnalyticsItem>>(emptyList()).asStateFlow()

    override suspend fun getMergerDetail(dealId: String): DealAnalyticsItem? {
        return null
    }
}

@Singleton
class AcquisitionRepositoryImpl @Inject constructor() : AcquisitionRepository {

    override fun getActiveAcquisitions(): Flow<List<DealAnalyticsItem>> = MutableStateFlow<List<DealAnalyticsItem>>(emptyList()).asStateFlow()

    override suspend fun getAcquisitionDetail(dealId: String): DealAnalyticsItem? {
        return null
    }
}

@Singleton
class DealAnalyticsRepositoryImpl @Inject constructor(
    private val engine: CorporateEventEngine
) : DealAnalyticsRepository {

    override suspend fun getDealImpactAnalysis(dealId: String): DealImpactAnalysis {
        return engine.computeDealImpact(dealId)
    }

    override suspend fun getDealAiIntelligence(dealId: String): DealAiIntelligence {
        return engine.generateAiIntelligence(dealId)
    }

    override suspend fun getDealVisuals(dealId: String): DealVisuals {
        return engine.computeDealVisuals(dealId)
    }

    override fun getFutureStubs(): Flow<CorporateEventFutureStubs> = MutableStateFlow(CorporateEventFutureStubs()).asStateFlow()
}
