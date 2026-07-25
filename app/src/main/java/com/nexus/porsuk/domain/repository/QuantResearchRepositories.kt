package com.nexus.porsuk.domain.repository

import com.nexus.porsuk.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * 1. Niceliksel Araştırma Deposu Sözleşmesi (ResearchRepository)
 */
interface ResearchRepository {
    fun getActiveResearchWorkspace(): Flow<ResearchWorkspace>
    suspend fun saveWorkspaceNotes(notes: String)
    fun getResearchSessions(): Flow<List<ResearchWorkspace>>
    suspend fun createNewSession(title: String, author: String): ResearchWorkspace
}

/**
 * 2. Faktör Analizi Deposu Sözleşmesi (FactorRepository)
 */
interface FactorRepository {
    fun getFactorMetrics(): Flow<List<FactorMetric>>
    suspend fun calculateCompositeFactorScore(symbol: String): Double
    fun getAlphaFactorDefinitions(): Flow<List<AlphaFactorDefinition>>
    suspend fun calculateFactorRanking(factorId: String): FactorRankingResult
    suspend fun calculateFactorExposure(symbol: String): FactorExposureResult
    suspend fun combineFactors(symbols: List<String>, strategy: FactorCombinationStrategy): List<FactorCombinationResult>
    suspend fun saveCustomFactorFormula(title: String, expression: String): CustomFactorFormula
}

/**
 * 3. İstatistiksel Analiz Deposu Sözleşmesi (StatisticsRepository)
 */
interface StatisticsRepository {
    fun getStatisticalAnalysis(assetPair: String): Flow<StatisticalAnalysisResult>
    fun getPortfolioResearchMetrics(): Flow<PortfolioResearchMetrics>
    suspend fun calculateAcademicModel(symbol: String, modelType: AcademicModelType): AcademicModelResult
    suspend fun getFactorDecay(factorId: String): FactorDecayMetrics
    suspend fun getFactorPersistence(factorId: String): FactorPersistenceMetrics
    suspend fun getFactorCorrelationMatrix(): FactorCorrelationMatrix
    suspend fun getPerformanceAttribution(symbolOrPortfolio: String): PerformanceAttributionResult
}

/**
 * 4. Veri Seti Yöneticisi Deposu Sözleşmesi (DatasetRepository)
 */
interface DatasetRepository {
    fun getAvailableDatasets(): Flow<List<DatasetItem>>
    suspend fun loadDataset(datasetId: String): Boolean
    fun getFeatureStoreDefinitions(): Flow<List<FeatureDefinition>>
    suspend fun applyFeatureTransformation(featureId: String, transformation: FeatureTransformationType): List<Double>
}

/**
 * 5. Çalışma Alanı Deposu Sözleşmesi (QuantWorkspaceRepository)
 */
interface QuantWorkspaceRepository {
    fun getSavedWorkspaces(): Flow<List<ResearchWorkspace>>
    suspend fun switchWorkspace(workspaceId: String)
}

/**
 * 6. Ana Niceliksel Platform Deposu (QuantRepository)
 */
interface QuantRepository {
    fun getFutureReadySuite(): Flow<FutureReadyQuantSuite>
    fun getMlModelConfigs(): Flow<List<MlModelConfig>>
    suspend fun runMlModelEvaluation(modelId: String): MlEvaluationResult
}

/**
 * 7. Deney Takibi Deposu Sözleşmesi (ExperimentRepository)
 */
interface ExperimentRepository {
    fun getSavedExperiments(): Flow<List<QuantExperiment>>
    suspend fun saveExperiment(title: String, params: Map<String, String>, metrics: Map<String, Double>, notes: String): QuantExperiment
    suspend fun deleteExperiment(experimentId: String)
}

/**
 * 8. Strateji Doğrulama Deposu Sözleşmesi (ValidationRepository)
 */
interface ValidationRepository {
    suspend fun runWalkForwardAnalysis(strategyId: String, inSampleMonths: Int, outOfSampleMonths: Int): WalkForwardResult
    suspend fun runRollingWindowAnalysis(symbol: String, windowDays: Int): RollingWindowResult
    suspend fun runBootstrapSimulation(strategyId: String, simulationsCount: Int): BootstrapResult
}
