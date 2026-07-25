package com.nexus.porsuk.domain.repository

import com.nexus.porsuk.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * 1. Şirket Olayları Deposu Sözleşmesi (CorporateEventRepository)
 */
interface CorporateEventRepository {
    fun getCorporateEvents(symbol: String): Flow<List<CorporateEvent>>
    fun getEventsByType(eventType: CorporateEventType): Flow<List<CorporateEvent>>
    fun getUpcomingEvents(): Flow<List<CorporateEvent>>
}

/**
 * 2. Birleşme Deposu Sözleşmesi (MergerRepository)
 */
interface MergerRepository {
    fun getActiveMergers(): Flow<List<DealAnalyticsItem>>
    suspend fun getMergerDetail(dealId: String): DealAnalyticsItem?
}

/**
 * 3. Satın Alım Deposu Sözleşmesi (AcquisitionRepository)
 */
interface AcquisitionRepository {
    fun getActiveAcquisitions(): Flow<List<DealAnalyticsItem>>
    suspend fun getAcquisitionDetail(dealId: String): DealAnalyticsItem?
}

/**
 * 4. M&A İşlem Analitiği Deposu Sözleşmesi (DealAnalyticsRepository)
 */
interface DealAnalyticsRepository {
    suspend fun getDealImpactAnalysis(dealId: String): DealImpactAnalysis
    suspend fun getDealAiIntelligence(dealId: String): DealAiIntelligence
    suspend fun getDealVisuals(dealId: String): DealVisuals
    fun getFutureStubs(): Flow<CorporateEventFutureStubs>
}
