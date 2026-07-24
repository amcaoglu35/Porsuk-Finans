package com.nexus.porsuk.domain.repository

import com.nexus.porsuk.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * 1. Bildirim Yöneticisi Deposu Sözleşmesi (FilingRepository)
 */
interface FilingRepository {
    fun getLatestFilings(): Flow<List<RegulatoryFiling>>
    fun getFilingsByCompany(symbol: String): Flow<List<RegulatoryFiling>>
    suspend fun markAsDownloaded(filingId: String)
}

/**
 * 2. Özel Durum Açıklamaları Deposu Sözleşmesi (DisclosureRepository)
 */
interface DisclosureRepository {
    fun getMaterialEventDisclosures(): Flow<List<RegulatoryFiling>>
    suspend fun getFilingAiSummary(filingId: String): FilingAiSummary
}

/**
 * 3. Belge Önbellek ve İndirme Deposu Sözleşmesi (DocumentRepository)
 */
interface DocumentRepository {
    fun getCachedDocuments(): Flow<List<RegulatoryFiling>>
    suspend fun downloadFilingPdf(filingId: String): Boolean
}

/**
 * 4. Bildirim Sınıflandırma Deposu Sözleşmesi (ClassificationRepository)
 */
interface ClassificationRepository {
    fun getFilingsByCategory(category: FilingCategory): Flow<List<RegulatoryFiling>>
    fun getAvailableCategories(): List<FilingCategory>
}

/**
 * 5. Şirket Zaman Çizelgesi Deposu Sözleşmesi (TimelineRepository)
 */
interface TimelineRepository {
    fun getCompanyTimeline(symbol: String): Flow<List<CompanyTimelineEvent>>
}
