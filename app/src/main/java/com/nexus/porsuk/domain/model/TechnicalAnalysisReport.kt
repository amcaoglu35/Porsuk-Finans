package com.nexus.porsuk.domain.model

/**
 * Destek ve Direnç Seviye Modeli
 */
data class SupportResistanceLevel(
    val levelName: String, // Pivot, S1, S2, R1, R2, Fib 61.8%
    val price: Double,
    val isSupport: Boolean // true: Destek, false: Direnç
)

/**
 * İndikatör Sonuç Modeli
 */
data class IndicatorValue(
    val name: String,
    val category: IndicatorCategory,
    val valueText: String,
    val signal: TechnicalSignalType
)

/**
 * Formasyon Tanıma Sonuç Modeli (Pattern Stub)
 */
data class PatternResult(
    val patternName: String,
    val isChartPattern: Boolean, // true: Grafik, false: Mum
    val isBullish: Boolean,
    val reliability: String = "Yüksek"
)

/**
 * Standart Teknik Analiz Raporu Modeli (TechnicalAnalysisReport)
 */
data class TechnicalAnalysisReport(
    val symbol: String,
    val timeFrame: TimeFrame,
    val overallSignal: TechnicalSignalType,
    val trendSummary: String,
    val momentumSummary: String,
    val volatilitySummary: String,
    val volumeSummary: String,
    val indicators: List<IndicatorValue>,
    val supportLevels: List<SupportResistanceLevel>,
    val resistanceLevels: List<SupportResistanceLevel>,
    val detectedPatterns: List<PatternResult>,
    val generatedAt: Long = System.currentTimeMillis()
)
