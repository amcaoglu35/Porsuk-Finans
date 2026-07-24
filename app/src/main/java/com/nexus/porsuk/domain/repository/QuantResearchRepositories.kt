package com.nexus.porsuk.domain.repository

import com.nexus.porsuk.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * 1. Niceliksel Araştırma Deposu Sözleşmesi (ResearchRepository)
 */
interface ResearchRepository {
    fun getActiveResearchWorkspace(): Flow<ResearchWorkspace>
    suspend fun saveWorkspaceNotes(notes: String)
}

/**
 * 2. Faktör Analizi Deposu Sözleşmesi (FactorRepository)
 */
interface FactorRepository {
    fun getFactorMetrics(): Flow<List<FactorMetric>>
    suspend fun calculateCompositeFactorScore(symbol: String): Double
}

/**
 * 3. İstatistiksel Analiz Deposu Sözleşmesi (StatisticsRepository)
 */
interface StatisticsRepository {
    fun getStatisticalAnalysis(assetPair: String): Flow<StatisticalAnalysisResult>
    fun getPortfolioResearchMetrics(): Flow<PortfolioResearchMetrics>
}

/**
 * 4. Veri Seti Yöneticisi Deposu Sözleşmesi (DatasetRepository)
 */
interface DatasetRepository {
    fun getAvailableDatasets(): Flow<List<DatasetItem>>
    suspend fun loadDataset(datasetId: String): Boolean
}

/**
 * 5. Çalışma Alanı Deposu Sözleşmesi (QuantWorkspaceRepository)
 */
interface QuantWorkspaceRepository {
    fun getSavedWorkspaces(): Flow<List<ResearchWorkspace>>
    suspend fun switchWorkspace(workspaceId: String)
}
