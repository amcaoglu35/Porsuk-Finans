package com.nexus.porsuk.feature.quant

import com.nexus.porsuk.domain.model.*

/**
 * Porsuk Quant Platform — Ekran Sekmeleri
 */
enum class QuantPlatformTab(val title: String, val iconEmoji: String) {
    ALPHA_FACTORY("Alpha Factory", "🧪"),
    ACADEMIC_MODELS("Academic Models", "🎓"),
    VALIDATION_ANALYTICS("Validation & Analytics", "🔬"),
    FEATURE_ML_STORE("Feature Store & ML", "⚡"),
    EXPERIMENTS_NOTEBOOK("Experiments & Notebook", "📝");
}

/**
 * Porsuk Quant Research Studio — UI Ekran Durumu (QuantResearchUiState)
 */
data class QuantResearchUiState(
    val activeTab: QuantPlatformTab = QuantPlatformTab.ALPHA_FACTORY,
    val activeWorkspace: ResearchWorkspace = ResearchWorkspace(),
    val factorMetrics: List<FactorMetric> = emptyList(),
    val selectedFactorCategory: FactorCategory? = null,
    val alphaFactorDefs: List<AlphaFactorDefinition> = emptyList(),
    val selectedCombinationStrategy: FactorCombinationStrategy = FactorCombinationStrategy.IC_WEIGHTED,
    val combinationResults: List<FactorCombinationResult> = emptyList(),

    // Academic Models
    val selectedAcademicModel: AcademicModelType = AcademicModelType.FAMA_FRENCH_5,
    val academicModelResult: AcademicModelResult? = null,

    // Validation & Analytics
    val walkForwardResult: WalkForwardResult? = null,
    val rollingWindowResult: RollingWindowResult? = null,
    val bootstrapResult: BootstrapResult? = null,
    val factorDecay: FactorDecayMetrics? = null,
    val factorPersistence: FactorPersistenceMetrics? = null,
    val correlationMatrix: FactorCorrelationMatrix? = null,
    val performanceAttribution: PerformanceAttributionResult? = null,

    // Feature Store & ML
    val featureDefinitions: List<FeatureDefinition> = emptyList(),
    val mlModelConfigs: List<MlModelConfig> = emptyList(),
    val activeMlEvaluation: MlEvaluationResult? = null,

    // Experiments & Workspaces
    val savedExperiments: List<QuantExperiment> = emptyList(),
    val statisticalResult: StatisticalAnalysisResult = StatisticalAnalysisResult(),
    val portfolioResearch: PortfolioResearchMetrics = PortfolioResearchMetrics(),
    val datasets: List<DatasetItem> = emptyList(),
    val savedWorkspaces: List<ResearchWorkspace> = emptyList(),
    val futureSuite: FutureReadyQuantSuite = FutureReadyQuantSuite(),

    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
