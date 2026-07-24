package com.nexus.porsuk.data.repository

import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FilingRepositoryImpl @Inject constructor() : FilingRepository {

    private val defaultFilings = listOf(
        RegulatoryFiling(
            filingId = "filing_thyao_2026_q2",
            companySymbol = "THYAO.IS",
            companyName = "Türk Hava Yolları A.O.",
            provider = FilingProviderType.KAP_TURKEY,
            documentType = FilingDocumentType.FINANCIAL_STATEMENT,
            category = FilingCategory.EARNINGS,
            title = "2026 2. Çeyrek Konsolide Finansal Raporu ve Faaliyet Notları",
            summaryText = "Şirket 2026/06 döneminde 18.5 Milyar TL net kâr açıklamıştır.",
            isImportant = true
        ),
        RegulatoryFiling(
            filingId = "filing_garan_div",
            companySymbol = "GARAN.IS",
            companyName = "Türkiye Garanti Bankası A.Ş.",
            provider = FilingProviderType.KAP_TURKEY,
            documentType = FilingDocumentType.DIVIDEND_ANNOUNCEMENT,
            category = FilingCategory.DIVIDEND,
            title = "Nakit Temettü Dağıtım Kararı ve Ödeme Takvimi",
            summaryText = "Hisse başına brüt 4.25 TL nakit temettü ödenmesi genel kurulda kabul edilmiştir.",
            isImportant = true
        ),
        RegulatoryFiling(
            filingId = "filing_aapl_10q",
            companySymbol = "AAPL",
            companyName = "Apple Inc.",
            provider = FilingProviderType.SEC_EDGAR_US,
            documentType = FilingDocumentType.QUARTERLY_REPORT,
            category = FilingCategory.EARNINGS,
            title = "SEC Form 10-Q Quarterly Report for Period Ended June 30",
            summaryText = "Quarterly revenue reached $85.8 billion, up 5% year-over-year.",
            isImportant = true
        ),
        RegulatoryFiling(
            filingId = "filing_sasa_buyback",
            companySymbol = "SASA.IS",
            companyName = "SASA Polyester Sanayi A.Ş.",
            provider = FilingProviderType.KAP_TURKEY,
            documentType = FilingDocumentType.SHARE_BUYBACK,
            category = FilingCategory.CORPORATE_ACTION,
            title = "Geri Alınan Paylar Hakkında Özel Durum Açıklaması",
            summaryText = "Şirket sermayesinin %0.5'ine denk gelen paylar borsada geri alınmıştır.",
            isImportant = false
        )
    )

    private val filingsState = MutableStateFlow(defaultFilings)

    override fun getLatestFilings(): Flow<List<RegulatoryFiling>> = filingsState.asStateFlow()

    override fun getFilingsByCompany(symbol: String): Flow<List<RegulatoryFiling>> {
        return filingsState.map { list -> list.filter { it.companySymbol == symbol } }
    }

    override suspend fun markAsDownloaded(filingId: String) {
        filingsState.update { current ->
            current.map { if (it.filingId == filingId) it.copy(isDownloaded = true) else it }
        }
    }
}

@Singleton
class DisclosureRepositoryImpl @Inject constructor() : DisclosureRepository {
    private val disclosuresState = MutableStateFlow(
        listOf(
            RegulatoryFiling(
                filingId = "disc_1",
                companySymbol = "THYAO.IS",
                companyName = "Türk Hava Yolları A.O.",
                title = "Yeni Uçak Siparişi ve Filo Genişleme Anlaşması"
            )
        )
    )

    override fun getMaterialEventDisclosures(): Flow<List<RegulatoryFiling>> = disclosuresState.asStateFlow()

    override suspend fun getFilingAiSummary(filingId: String): FilingAiSummary {
        return FilingAiSummary(filingId = filingId)
    }
}

@Singleton
class DocumentRepositoryImpl @Inject constructor() : DocumentRepository {
    private val cachedState = MutableStateFlow(emptyList<RegulatoryFiling>())

    override fun getCachedDocuments(): Flow<List<RegulatoryFiling>> = cachedState.asStateFlow()

    override suspend fun downloadFilingPdf(filingId: String): Boolean = true
}

@Singleton
class ClassificationRepositoryImpl @Inject constructor() : ClassificationRepository {
    override fun getFilingsByCategory(category: FilingCategory): Flow<List<RegulatoryFiling>> {
        return MutableStateFlow(emptyList<RegulatoryFiling>()).asStateFlow()
    }

    override fun getAvailableCategories(): List<FilingCategory> = FilingCategory.entries.toList()
}

@Singleton
class TimelineRepositoryImpl @Inject constructor() : TimelineRepository {
    private val defaultEvents = listOf(
        CompanyTimelineEvent(
            companySymbol = "THYAO.IS",
            eventDate = "24 Temmuz 2026",
            title = "2026/06 Çeyreklik Bilanço Açıklandı",
            category = FilingCategory.EARNINGS,
            description = "18.5 Milyar TL net kâr elde edildi."
        ),
        CompanyTimelineEvent(
            companySymbol = "THYAO.IS",
            eventDate = "15 Mayıs 2026",
            title = "Olağan Genel Kurul Kararları KAP'a Bildirildi",
            category = FilingCategory.CORPORATE_ACTION,
            description = "Temettü ve yönetim kurulu üyeleri onaylandı."
        )
    )

    private val timelineState = MutableStateFlow(defaultEvents)

    override fun getCompanyTimeline(symbol: String): Flow<List<CompanyTimelineEvent>> = timelineState.asStateFlow()
}
