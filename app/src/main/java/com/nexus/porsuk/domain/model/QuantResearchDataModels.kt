package com.nexus.porsuk.domain.model

/**
 * 8 Faktör Kategorisi (FactorCategory)
 */
enum class FactorCategory(val displayName: String, val iconEmoji: String) {
    VALUE("Değer Faktörleri (Value)", "💎"),
    GROWTH("Büyüme Faktörleri (Growth)", "🚀"),
    MOMENTUM("Momentum Faktörleri", "⚡"),
    QUALITY("Kalite Faktörleri (Quality)", "🏅"),
    VOLATILITY("Volatite & Risk Faktörleri", "📉"),
    DIVIDEND("Temettü Faktörleri", "💰"),
    LIQUIDITY("Likitide Faktörleri", "💧"),
    CUSTOM("Özel Kullanıcı Faktörleri", "🧪");
}

/**
 * Niceliksel Faktör Metriği (FactorMetric)
 */
data class FactorMetric(
    val factorId: String,
    val name: String,
    val category: FactorCategory = FactorCategory.VALUE,
    val rawValue: Double,
    val zScore: Double,
    val percentileRank: Double,
    val description: String
)

/**
 * İstatistiksel Analiz Sonucu (StatisticalAnalysisResult)
 */
data class StatisticalAnalysisResult(
    val assetPair: String = "THYAO.IS / PGSUS.IS",
    val correlationCoefficient: Double = 0.88,
    val covariance: Double = 0.042,
    val rSquared: Double = 0.77,
    val beta: Double = 1.12,
    val alpha: Double = 0.045,
    val pValue: Double = 0.001
)

/**
 * Portföy Araştırma ve Risk Metrikleri (PortfolioResearchMetrics)
 */
data class PortfolioResearchMetrics(
    val diversificationScore: Int = 88,
    val maxDrawdownPct: Double = 12.4,
    val sharpeRatio: Double = 1.84,
    val sortinoRatio: Double = 2.45,
    val sectorExposures: Map<String, Double> = mapOf(
        "Havacılık" to 35.0,
        "Bankacılık" to 25.0,
        "Teknoloji" to 20.0,
        "Sanayi" to 20.0
    ),
    val riskAttributionMap: Map<String, Double> = mapOf(
        "Piyasa Riski (Beta)" to 62.0,
        "Spesifik Hisse Riski" to 28.0,
        "Kur Riski" to 10.0
    )
)

/**
 * Niceliksel Çalışma Alanı (ResearchWorkspace)
 */
data class ResearchWorkspace(
    val workspaceId: String = "ws_bist_alpha_factor",
    val title: String = "BIST 100 Multi-Factor Alpha Model",
    val author: String = "Quant Research Lab",
    val factorStudiesCount: Int = 14,
    val notebookNotes: String = "Fiyat/Kazanç (F/K) ve Özsermaye Kârlılığı (ROE) faktörlerinin momentum ağırlıklı kompozit skoru BIST 100 üzerinde test edilmiştir.",
    val lastModifiedTimestamp: Long = System.currentTimeMillis()
)

/**
 * Niceliksel Veri Seti (DatasetItem)
 */
data class DatasetItem(
    val datasetId: String = "ds_bist_historical_daily",
    val title: String = "BIST 100 Günlük Fiyat & Bilanço Veri Seti (2018-2026)",
    val rowCount: Long = 245000L,
    val columnsCount: Int = 38,
    val isCached: Boolean = true,
    val version: String = "v2.4.0"
)

/**
 * Geleceğe Hazır Niceliksel Araştırma Stub Modeli (QuantFutureStubs)
 */
data class QuantFutureStubs(
    val isMonteCarloSimulationReady: Boolean = true,
    val isMachineLearningForecastingReady: Boolean = true,
    val isTimeSeriesForecastingReady: Boolean = true,
    val isPythonBridgeInterfaceReady: Boolean = true,
    val isJupyterIntegrationInterfaceReady: Boolean = true,
    val isAiResearchAssistantActive: Boolean = true
)
