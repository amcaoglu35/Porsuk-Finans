package com.nexus.porsuk.domain.model

/**
 * Doğrulama Metodu (Validation Methods)
 */
enum class QuantValidationType(val displayName: String) {
    WALK_FORWARD("Walk Forward Analysis"),
    ROLLING_WINDOW("Rolling Window Analysis"),
    OUT_OF_SAMPLE("Out-Of-Sample Split"),
    CROSS_VALIDATION("K-Fold Time Series Cross Validation"),
    BOOTSTRAP("Bootstrap Resampling Simulation");
}

/**
 * Walk Forward Analizi Parametreleri & Sonucu
 */
data class WalkForwardResult(
    val strategyId: String,
    val inSampleMonths: Int,
    val outOfSampleMonths: Int,
    val windowsCount: Int,
    val isStabilityHigh: Boolean,
    val overallSharpeRatio: Double,
    val inSampleSharpeRatio: Double,
    val outOfSampleSharpeRatio: Double,
    val maxDrawdownPct: Double,
    val windowDetails: List<WalkForwardWindowStep>
)

data class WalkForwardWindowStep(
    val windowIndex: Int,
    val trainPeriod: String,
    val testPeriod: String,
    val trainReturnPct: Double,
    val testReturnPct: Double,
    val sharpeRatio: Double
)

/**
 * Rolling Window Analiz Sonucu
 */
data class RollingWindowResult(
    val symbolOrStrategy: String,
    val windowDays: Int,
    val rollingAlphaSeries: List<TimestampValuePair>,
    val rollingSharpeSeries: List<TimestampValuePair>,
    val rollingBetaSeries: List<TimestampValuePair>
)

data class TimestampValuePair(
    val timestamp: Long,
    val dateLabel: String,
    val value: Double
)

/**
 * Bootstrap Resampling Simülasyon Sonucu
 */
data class BootstrapResult(
    val simulationsCount: Int,
    val confidenceInterval95Lower: Double,
    val confidenceInterval95Upper: Double,
    val meanReturnPct: Double,
    val medianReturnPct: Double,
    val probabilityOfLossPct: Double
)

/**
 * Faktör Getirisi & Sönümlenme (Factor Return & Decay) Metrikleri
 */
data class FactorDecayMetrics(
    val factorId: String,
    val halfLifeDays: Double,
    val autocorrelationLag1: Double,
    val autocorrelationLag5: Double,
    val autocorrelationLag21: Double,
    val decayRatePercentPerDay: Double
)

/**
 * Faktör Kararlılığı & Bilgi Katsayısı (IC Persistence) Metrikleri
 */
data class FactorPersistenceMetrics(
    val factorId: String,
    val meanIC: Double,
    val stdIC: Double,
    val icIR: Double, // Information Ratio of IC = Mean IC / Std IC
    val rankIC: Double,
    val positiveICRatioPct: Double,
    val icTimeSeries: List<TimestampValuePair>
)

/**
 * Faktör Korelasyon Matrisi (Correlation Matrix)
 */
data class FactorCorrelationMatrix(
    val factorNames: List<String>,
    val matrixGrid: List<List<Double>>
)

/**
 * Performans Attribüsyonu (Brinson & Factor Performance Attribution)
 */
data class PerformanceAttributionResult(
    val totalReturnPct: Double,
    val benchmarkReturnPct: Double,
    val excessReturnPct: Double,
    val allocationEffectPct: Double, // Sector allocation
    val selectionEffectPct: Double,  // Stock selection
    val interactionEffectPct: Double,
    val factorContributionsMap: Map<String, Double>
)
