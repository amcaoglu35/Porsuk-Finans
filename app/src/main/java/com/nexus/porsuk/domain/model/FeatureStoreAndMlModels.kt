package com.nexus.porsuk.domain.model

/**
 * Eksik Veri Tamamlama Yöntemleri (Imputation Strategies)
 */
enum class MissingDataStrategy(val displayName: String) {
    FORWARD_FILL("Forward Fill (Son Geçerli Değer)"),
    MEAN_IMPUTATION("Ortalama Değer (Mean Imputation)"),
    MEDIAN_IMPUTATION("Medyan Değer (Median Imputation)"),
    LINEAR_INTERPOLATION("Lineer İnterpolasyon");
}

/**
 * Veri Dönüşümü Yöntemleri (Transformations)
 */
enum class FeatureTransformationType(val displayName: String) {
    LOG_TRANSFORM("Logaritmik Dönüşüm (Log)"),
    DIFFERENCING("Fark Alma (1st Difference)"),
    FRACTIONAL_DIFFERENCING("Kesirli Fark Alma (Fractional Diff)"),
    BOX_COX("Box-Cox Dönüşümü");
}

/**
 * Feature Store Özellik Tanımı
 */
data class FeatureDefinition(
    val featureId: String,
    val name: String,
    val dataType: String = "FLOAT64",
    val transformationUsed: FeatureTransformationType = FeatureTransformationType.LOG_TRANSFORM,
    val missingStrategy: MissingDataStrategy = MissingDataStrategy.FORWARD_FILL,
    val version: String = "v1.0"
)

/**
 * Machine Learning Görev Türleri (ML Task Interfaces)
 */
enum class MlTaskType(val code: String, val title: String) {
    REGRESSION("REG", "Quantitative Price/Return Regression"),
    CLASSIFICATION("CLS", "Market Regime & Direction Classification"),
    CLUSTERING("CLU", "Asset Clustering & Risk Bucketing"),
    TIME_SERIES("TS", "ARIMA/GARCH Time Series Forecasting"),
    NEURAL_NETWORK("NN", "Deep Learning Alpha Predictor Interface"),
    TRANSFORMER("TRF", "Financial Time-Series Transformer Interface");
}

/**
 * Machine Learning Model Yapılandırma ve Değerlendirme
 */
data class MlModelConfig(
    val modelId: String,
    val modelName: String,
    val taskType: MlTaskType,
    val hyperparameters: Map<String, String>,
    val trainTestSplitRatio: Double = 0.8,
    val isActivated: Boolean = false
)

data class MlEvaluationResult(
    val modelId: String,
    val taskType: MlTaskType,
    val accuracyOrR2: Double,
    val maeOrLoss: Double,
    val sharpeContribution: Double,
    val featureImportances: Map<String, Double>
)

/**
 * Deney Takip Modelleri (Experiment Tracking)
 */
data class QuantExperiment(
    val experimentId: String,
    val title: String,
    val version: String = "v1.0.0",
    val parameters: Map<String, String>,
    val metrics: Map<String, Double>,
    val notes: String,
    val status: String = "COMPLETED",
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Geleceğe Hazır Platform Arayüz Stubs (Future Ready Interfaces)
 */
data class FutureReadyQuantSuite(
    val isAiAlphaDiscoveryActive: Boolean = false,
    val isAutoFeatureEngineeringActive: Boolean = false,
    val isLlmResearchAssistantActive: Boolean = false,
    val isAutoQuantStrategyActive: Boolean = false,
    val isInstitutionalFactorLibraryActive: Boolean = false
)
