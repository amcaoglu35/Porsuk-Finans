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

    private val sampleEvents = listOf(
        CorporateEvent("e_1", "THYAO.IS", "Türk Hava Yolları", CorporateEventType.ACQUISITION, "22 Haziran 2026", "Doğu Avrupa Lojistik Şirketi Satın Alım Anlaşması", "THY Lojistik A.Ş., Doğu Avrupa pazar payını artırmak amacıyla bölgesel kargo şirketinin %85 hissesini $1.8B bedelle satın alma kararı almıştır.", 88.0, isUpcoming = false),
        CorporateEvent("e_2", "GARAN.IS", "Garanti BBVA", CorporateEventType.STRATEGIC_PARTNERSHIP, "10 Temmuz 2026", "Global FinTech Yapay Zeka Ortaklığı", "Garanti BBVA, Avrupa merkezli FinTech şirketi ile AI algoritmik ticaret ortaklığı imzalamıştır.", 74.0, isUpcoming = false),
        CorporateEvent("e_3", "TUPRS.IS", "Tüpraş", CorporateEventType.ASSET_SALE, "15 Ağustos 2026", "Yenilenebilir Enerji Santral Varlık Satışı", "Yeşil hidrojen yatırımlarına kaynak aktarımı amacıyla atıl güneş santral varlıklarının $120M bedelle satışı planlanmaktadır.", 65.0, isUpcoming = true)
    )

    override fun getCorporateEvents(symbol: String): Flow<List<CorporateEvent>> = MutableStateFlow(sampleEvents).asStateFlow()

    override fun getEventsByType(eventType: CorporateEventType): Flow<List<CorporateEvent>> {
        return MutableStateFlow(sampleEvents.filter { it.eventType == eventType }).asStateFlow()
    }

    override fun getUpcomingEvents(): Flow<List<CorporateEvent>> {
        return MutableStateFlow(sampleEvents.filter { it.isUpcoming }).asStateFlow()
    }
}

@Singleton
class MergerRepositoryImpl @Inject constructor() : MergerRepository {

    private val sampleMergers = listOf(
        DealAnalyticsItem(
            dealId = "deal_m1",
            acquirerSymbol = "KCHOL.IS",
            acquirerName = "Koç Holding",
            targetSymbol = "AYGAZ.IS",
            targetName = "Aygaz A.Ş.",
            dealValueUsd = 2400000000.0,
            premiumPaidPct = 22.5,
            evEbitdaMultiple = 7.8,
            paymentType = DealPaymentType.ALL_STOCK,
            status = DealStatus.APPROVED,
            announcementDate = "12 Nisan 2026",
            expectedClosingDate = "15 Eylül 2026",
            financialAdvisors = listOf("Morgan Stanley", "İş Yatırım"),
            legalAdvisors = listOf("White & Case", "Her时刻 Hukuk")
        )
    )

    override fun getActiveMergers(): Flow<List<DealAnalyticsItem>> = MutableStateFlow(sampleMergers).asStateFlow()

    override suspend fun getMergerDetail(dealId: String): DealAnalyticsItem? {
        return sampleMergers.firstOrNull { it.dealId == dealId }
    }
}

@Singleton
class AcquisitionRepositoryImpl @Inject constructor() : AcquisitionRepository {

    private val sampleAcquisitions = listOf(
        DealAnalyticsItem(
            dealId = "deal_a1",
            acquirerSymbol = "THYAO.IS",
            acquirerName = "Türk Hava Yolları",
            targetSymbol = "LOGI.EU",
            targetName = "EuroLogistics Express",
            dealValueUsd = 1800000000.0,
            premiumPaidPct = 28.5,
            evEbitdaMultiple = 8.5,
            paymentType = DealPaymentType.CASH_AND_STOCK,
            status = DealStatus.PENDING,
            announcementDate = "22 Haziran 2026",
            expectedClosingDate = "30 Eylül 2026",
            financialAdvisors = listOf("Goldman Sachs", "Garanti BBVA Yatırım"),
            legalAdvisors = listOf("Baker McKenzie")
        )
    )

    override fun getActiveAcquisitions(): Flow<List<DealAnalyticsItem>> = MutableStateFlow(sampleAcquisitions).asStateFlow()

    override suspend fun getAcquisitionDetail(dealId: String): DealAnalyticsItem? {
        return sampleAcquisitions.firstOrNull { it.dealId == dealId }
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
