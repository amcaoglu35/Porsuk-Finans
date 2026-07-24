package com.nexus.porsuk.data.repository

import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResearchRepositoryImpl @Inject constructor() : ResearchRepository {
    private val workspaceState = MutableStateFlow(ResearchWorkspace())

    override fun getActiveResearchWorkspace(): Flow<ResearchWorkspace> = workspaceState.asStateFlow()

    override suspend fun saveWorkspaceNotes(notes: String) {
        workspaceState.value = workspaceState.value.copy(
            notebookNotes = notes,
            lastModifiedTimestamp = System.currentTimeMillis()
        )
    }
}

@Singleton
class FactorRepositoryImpl @Inject constructor() : FactorRepository {
    private val defaultFactors = listOf(
        FactorMetric(
            factorId = "f_pe_ratio",
            name = "Fiyat/Kazanç (F/K - P/E)",
            category = FactorCategory.VALUE,
            rawValue = 7.4,
            zScore = -1.25,
            percentileRank = 84.0,
            description = "Düşük F/K oranı ucuz fiyatlamayı gösterir."
        ),
        FactorMetric(
            factorId = "f_roe",
            name = "Özsermaye Kârlılığı (ROE)",
            category = FactorCategory.QUALITY,
            rawValue = 38.5,
            zScore = 1.85,
            percentileRank = 92.5,
            description = "Yüksek ROE şirketin sermayesini verimli kullandığını ifade eder."
        ),
        FactorMetric(
            factorId = "f_momentum_12m",
            name = "12 Aylık Fiyat Momentum",
            category = FactorCategory.MOMENTUM,
            rawValue = 142.0,
            zScore = 1.45,
            percentileRank = 88.0,
            description = "12 aylık göreceli getiri ivmesi."
        ),
        FactorMetric(
            factorId = "f_div_yield",
            name = "Temettü Verimi (Yield)",
            category = FactorCategory.DIVIDEND,
            rawValue = 8.2,
            zScore = 1.10,
            percentileRank = 79.5,
            description = "Nakit temettü verimi oranı."
        )
    )

    private val factorState = MutableStateFlow(defaultFactors)

    override fun getFactorMetrics(): Flow<List<FactorMetric>> = factorState.asStateFlow()

    override suspend fun calculateCompositeFactorScore(symbol: String): Double {
        return 88.4
    }
}

@Singleton
class StatisticsRepositoryImpl @Inject constructor() : StatisticsRepository {
    private val statsState = MutableStateFlow(StatisticalAnalysisResult())
    private val portfolioStatsState = MutableStateFlow(PortfolioResearchMetrics())

    override fun getStatisticalAnalysis(assetPair: String): Flow<StatisticalAnalysisResult> = statsState.asStateFlow()

    override fun getPortfolioResearchMetrics(): Flow<PortfolioResearchMetrics> = portfolioStatsState.asStateFlow()
}

@Singleton
class DatasetRepositoryImpl @Inject constructor() : DatasetRepository {
    private val defaultDatasets = listOf(
        DatasetItem(
            datasetId = "ds_bist100_daily",
            title = "BIST 100 Günlük Fiyat & Bilanço Veri Seti (2018-2026)",
            rowCount = 245000L,
            columnsCount = 38,
            isCached = true
        ),
        DatasetItem(
            datasetId = "ds_us_sp500_factors",
            title = "S&P 500 Factor Risk Premiums (2015-2026)",
            rowCount = 180000L,
            columnsCount = 42,
            isCached = true
        )
    )

    private val datasetsState = MutableStateFlow(defaultDatasets)

    override fun getAvailableDatasets(): Flow<List<DatasetItem>> = datasetsState.asStateFlow()

    override suspend fun loadDataset(datasetId: String): Boolean = true
}

@Singleton
class QuantWorkspaceRepositoryImpl @Inject constructor() : QuantWorkspaceRepository {
    private val workspacesState = MutableStateFlow(
        listOf(
            ResearchWorkspace(workspaceId = "ws_1", title = "BIST 100 Multi-Factor Alpha Model"),
            ResearchWorkspace(workspaceId = "ws_2", title = "Global Macro & Commodity Carry Strategy")
        )
    )

    override fun getSavedWorkspaces(): Flow<List<ResearchWorkspace>> = workspacesState.asStateFlow()

    override suspend fun switchWorkspace(workspaceId: String) {}
}
