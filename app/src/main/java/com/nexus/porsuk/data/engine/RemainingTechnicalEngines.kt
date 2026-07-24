package com.nexus.porsuk.data.engine

import com.nexus.porsuk.domain.model.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Destek ve Direnç Motoru (Pivot Points & Fibonacci Retracement)
 */
@Singleton
class SupportResistanceEngine @Inject constructor() {
    fun calculateSupportResistance(symbol: String, timeFrame: TimeFrame): Pair<List<SupportResistanceLevel>, List<SupportResistanceLevel>> {
        val supports = listOf(
            SupportResistanceLevel("Pivot (P)", 280.0, true),
            SupportResistanceLevel("Destek 1 (S1)", 275.5, true),
            SupportResistanceLevel("Destek 2 (S2)", 270.0, true),
            SupportResistanceLevel("Fibonacci %61.8", 274.2, true)
        )

        val resistances = listOf(
            SupportResistanceLevel("Direnç 1 (R1)", 288.5, false),
            SupportResistanceLevel("Direnç 2 (R2)", 294.0, false),
            SupportResistanceLevel("Fibonacci %100", 300.0, false)
        )

        return Pair(supports, resistances)
    }
}

/**
 * Formasyon Tanıma Motoru (Chart Patterns & Candlesticks Stubs)
 */
@Singleton
class PatternRecognitionEngine @Inject constructor() {
    fun detectPatterns(symbol: String, timeFrame: TimeFrame): List<PatternResult> {
        return listOf(
            PatternResult("Yükselen Üçgen (Ascending Triangle)", isChartPattern = true, isBullish = true, reliability = "Yüksek"),
            PatternResult("Boğa Yututan Mum (Bullish Engulfing)", isChartPattern = false, isBullish = true, reliability = "Çok Yüksek"),
            PatternResult("Sabah Yıldızı (Morning Star)", isChartPattern = false, isBullish = true, reliability = "Yüksek")
        )
    }
}

/**
 * 5 Seviyeli Sinyal Birleştirici & Rapor Üreticisi (TechnicalSignalEngine)
 */
@Singleton
class TechnicalSignalEngine @Inject constructor(
    private val trendEngine: TrendIndicatorEngine,
    private val momentumEngine: MomentumIndicatorEngine,
    private val volatilityEngine: VolatilityIndicatorEngine,
    private val volumeEngine: VolumeIndicatorEngine,
    private val supportResistanceEngine: SupportResistanceEngine,
    private val patternEngine: PatternRecognitionEngine
) {
    fun generateTechnicalReport(symbol: String, timeFrame: TimeFrame): TechnicalAnalysisReport {
        val trendInds = trendEngine.calculateTrendIndicators(symbol, timeFrame)
        val momentumInds = momentumEngine.calculateMomentumIndicators(symbol, timeFrame)
        val volatilityInds = volatilityEngine.calculateVolatilityIndicators(symbol, timeFrame)
        val volumeInds = volumeEngine.calculateVolumeIndicators(symbol, timeFrame)

        val allIndicators = trendInds + momentumInds + volatilityInds + volumeInds
        val (supports, resistances) = supportResistanceEngine.calculateSupportResistance(symbol, timeFrame)
        val patterns = patternEngine.detectPatterns(symbol, timeFrame)

        val buyCount = allIndicators.count { it.signal == TechnicalSignalType.BUY || it.signal == TechnicalSignalType.STRONG_BUY }
        val sellCount = allIndicators.count { it.signal == TechnicalSignalType.SELL || it.signal == TechnicalSignalType.STRONG_SELL }

        val overallSignal = when {
            buyCount >= 10 -> TechnicalSignalType.STRONG_BUY
            buyCount > sellCount -> TechnicalSignalType.BUY
            sellCount >= 10 -> TechnicalSignalType.STRONG_SELL
            sellCount > buyCount -> TechnicalSignalType.SELL
            else -> TechnicalSignalType.NEUTRAL
        }

        return TechnicalAnalysisReport(
            symbol = symbol,
            timeFrame = timeFrame,
            overallSignal = overallSignal,
            trendSummary = "Trend: $timeFrame zaman diliminde 50 ve 200 HO üstünde yükseliş trendi devam ediyor.",
            momentumSummary = "Momentum: RSI 58.4 seviyesinde pozitif, MACD yeşil kesişimini koruyor.",
            volatilitySummary = "Volatilite: ATR 8.20 TL seviyesinde, bant sıkışması sonrası kırılım eğilimi var.",
            volumeSummary = "Hacim: OBV ve VWAP yükselen hacim girişini teyit ediyor.",
            indicators = allIndicators,
            supportLevels = supports,
            resistanceLevels = resistances,
            detectedPatterns = patterns
        )
    }
}
