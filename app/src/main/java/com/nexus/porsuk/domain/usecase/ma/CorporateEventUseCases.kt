package com.nexus.porsuk.domain.usecase.ma

import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * 1. Şirket Olayları & Zaman Çizelgesi UseCase
 */
class GetCorporateEventsUseCase @Inject constructor(
    private val corporateEventRepository: CorporateEventRepository
) {
    fun getEvents(symbol: String): Flow<List<CorporateEvent>> {
        return corporateEventRepository.getCorporateEvents(symbol)
    }

    fun getUpcoming(): Flow<List<CorporateEvent>> {
        return corporateEventRepository.getUpcomingEvents()
    }
}

/**
 * 2. Birleşme & Satın Alım (M&A) İşlemleri UseCase
 */
class GetMergersAndAcquisitionsUseCase @Inject constructor(
    private val mergerRepository: MergerRepository,
    private val acquisitionRepository: AcquisitionRepository
) {
    fun getMergers(): Flow<List<DealAnalyticsItem>> {
        return mergerRepository.getActiveMergers()
    }

    fun getAcquisitions(): Flow<List<DealAnalyticsItem>> {
        return acquisitionRepository.getActiveAcquisitions()
    }
}

/**
 * 3. M&A İşlem Analitiği & Görseller UseCase
 */
class GetDealAnalyticsUseCase @Inject constructor(
    private val dealAnalyticsRepository: DealAnalyticsRepository
) {
    suspend fun getVisuals(dealId: String): DealVisuals {
        return dealAnalyticsRepository.getDealVisuals(dealId)
    }
}

/**
 * 4. Etki & Sinerji Analizi UseCase
 */
class AnalyzeDealImpactUseCase @Inject constructor(
    private val dealAnalyticsRepository: DealAnalyticsRepository
) {
    suspend fun execute(dealId: String): DealImpactAnalysis {
        return dealAnalyticsRepository.getDealImpactAnalysis(dealId)
    }
}

/**
 * 5. AI M&A Zekası UseCase
 */
class GenerateDealAiIntelligenceUseCase @Inject constructor(
    private val dealAnalyticsRepository: DealAnalyticsRepository
) {
    suspend fun execute(dealId: String): DealAiIntelligence {
        return dealAnalyticsRepository.getDealAiIntelligence(dealId)
    }
}
