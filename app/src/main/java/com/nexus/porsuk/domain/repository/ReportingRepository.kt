package com.nexus.porsuk.domain.repository

import com.nexus.porsuk.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * Enterprise Reporting Center — Depo Sözleşmesi
 */
interface ReportingRepository {
    fun getSavedReports(): Flow<List<EnterpriseReport>>
    suspend fun generateReport(type: ReportType, format: ReportFormat): EnterpriseReport
    suspend fun deleteReport(reportId: String)
    suspend fun scheduleReport(type: ReportType, schedule: ReportSchedule)
    
    // Rapor Veri Sağlayıcılar
    suspend fun getPortfolioReportData(): PortfolioReportData
    suspend fun getAiReportData(): AiReportData
    suspend fun getRiskReportData(): RiskReportData
    suspend fun getPerformanceReportData(): Map<String, List<Double>> // Benchmark comparisons
    suspend fun getTaxReportData(): TaxReportData
}
