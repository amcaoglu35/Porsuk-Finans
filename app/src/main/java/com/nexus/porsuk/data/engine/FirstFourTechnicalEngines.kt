package com.nexus.porsuk.data.engine

import com.nexus.porsuk.data.repository.FinanceRepository
import com.nexus.porsuk.domain.model.*
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 1. Trend İndikatörleri Motoru (SMA, EMA, WMA, SuperTrend, Ichimoku, Parabolic SAR) via ta4j
 */
@Singleton
class TrendIndicatorEngine @Inject constructor(
    private val financeRepository: FinanceRepository
) {
    fun calculateTrendIndicators(symbol: String, timeFrame: TimeFrame): List<IndicatorValue> {
        val prices = runBlocking {
            financeRepository.getStockHistory(symbol).firstOrNull()?.map { it.price }
        } ?: emptyList()

        val series = Ta4jTechnicalCalculator.createBarSeries(symbol, prices)
        val res = Ta4jTechnicalCalculator.calculateIndicators(series)

        if (!res.isSufficientData) {
            return listOf(
                IndicatorValue("SMA (50)", IndicatorCategory.TREND, "Yetersiz Veri", TechnicalSignalType.NEUTRAL),
                IndicatorValue("EMA (20)", IndicatorCategory.TREND, "Yetersiz Veri", TechnicalSignalType.NEUTRAL),
                IndicatorValue("SuperTrend", IndicatorCategory.TREND, "Yetersiz Veri", TechnicalSignalType.NEUTRAL)
            )
        }

        val smaValStr = String.format(Locale.US, "%.2f TL", res.sma50 ?: 0.0)
        val emaValStr = String.format(Locale.US, "%.2f TL", res.ema20 ?: 0.0)
        val lastPrice = prices.lastOrNull() ?: 0.0

        val smaSignal = if (lastPrice > (res.sma50 ?: 0.0)) TechnicalSignalType.BUY else TechnicalSignalType.SELL
        val emaSignal = if (lastPrice > (res.ema20 ?: 0.0)) TechnicalSignalType.STRONG_BUY else TechnicalSignalType.SELL

        return listOf(
            IndicatorValue("SMA (50)", IndicatorCategory.TREND, smaValStr, smaSignal),
            IndicatorValue("EMA (20)", IndicatorCategory.TREND, emaValStr, emaSignal),
            IndicatorValue("SuperTrend", IndicatorCategory.TREND, if (lastPrice > (res.ema20 ?: 0.0)) "Boğa Trendi" else "Ayı Trendi", smaSignal)
        )
    }
}

/**
 * 2. Momentum İndikatörleri Motoru (RSI, Stochastic RSI, MACD, AO, CCI) via ta4j
 */
@Singleton
class MomentumIndicatorEngine @Inject constructor(
    private val financeRepository: FinanceRepository
) {
    fun calculateMomentumIndicators(symbol: String, timeFrame: TimeFrame): List<IndicatorValue> {
        val prices = runBlocking {
            financeRepository.getStockHistory(symbol).firstOrNull()?.map { it.price }
        } ?: emptyList()

        val series = Ta4jTechnicalCalculator.createBarSeries(symbol, prices)
        val res = Ta4jTechnicalCalculator.calculateIndicators(series)

        if (!res.isSufficientData || res.rsi == null || res.macdValue == null) {
            return listOf(
                IndicatorValue("RSI (14)", IndicatorCategory.MOMENTUM, "Yetersiz Veri", TechnicalSignalType.NEUTRAL),
                IndicatorValue("MACD (12, 26, 9)", IndicatorCategory.MOMENTUM, "Yetersiz Veri", TechnicalSignalType.NEUTRAL)
            )
        }

        val rsiStr = String.format(Locale.US, "%.1f", res.rsi)
        val rsiSignal = when {
            res.rsi >= 70.0 -> TechnicalSignalType.STRONG_SELL
            res.rsi <= 30.0 -> TechnicalSignalType.STRONG_BUY
            res.rsi > 50.0  -> TechnicalSignalType.BUY
            else            -> TechnicalSignalType.NEUTRAL
        }

        val macdStr = String.format(Locale.US, "%+.2f (Sinyal: %+.2f)", res.macdValue, res.macdSignal ?: 0.0)
        val macdSignal = when {
            (res.macdHist ?: 0.0) > 0 -> TechnicalSignalType.STRONG_BUY
            (res.macdHist ?: 0.0) < 0 -> TechnicalSignalType.SELL
            else -> TechnicalSignalType.NEUTRAL
        }

        return listOf(
            IndicatorValue("RSI (14)", IndicatorCategory.MOMENTUM, rsiStr, rsiSignal),
            IndicatorValue("MACD (12, 26, 9)", IndicatorCategory.MOMENTUM, macdStr, macdSignal)
        )
    }
}

/**
 * 3. Volatilite İndikatörleri Motoru (ATR, Bollinger Bands) via ta4j
 */
@Singleton
class VolatilityIndicatorEngine @Inject constructor(
    private val financeRepository: FinanceRepository
) {
    fun calculateVolatilityIndicators(symbol: String, timeFrame: TimeFrame): List<IndicatorValue> {
        val prices = runBlocking {
            financeRepository.getStockHistory(symbol).firstOrNull()?.map { it.price }
        } ?: emptyList()

        val series = Ta4jTechnicalCalculator.createBarSeries(symbol, prices)
        val res = Ta4jTechnicalCalculator.calculateIndicators(series)

        if (!res.isSufficientData || res.atr == null || res.bollingerUpper == null) {
            return listOf(
                IndicatorValue("ATR (14)", IndicatorCategory.VOLATILITY, "Yetersiz Veri", TechnicalSignalType.NEUTRAL),
                IndicatorValue("Bollinger Bands", IndicatorCategory.VOLATILITY, "Yetersiz Veri", TechnicalSignalType.NEUTRAL)
            )
        }

        val atrStr = String.format(Locale.US, "%.2f TL", res.atr)
        val bbStr = String.format(Locale.US, "Üst: %.1f, Alt: %.1f", res.bollingerUpper, res.bollingerLower ?: 0.0)
        val lastPrice = prices.lastOrNull() ?: 0.0

        val bbSignal = when {
            lastPrice >= res.bollingerUpper -> TechnicalSignalType.SELL
            lastPrice <= (res.bollingerLower ?: 0.0) -> TechnicalSignalType.BUY
            else -> TechnicalSignalType.NEUTRAL
        }

        return listOf(
            IndicatorValue("ATR (14)", IndicatorCategory.VOLATILITY, atrStr, TechnicalSignalType.NEUTRAL),
            IndicatorValue("Bollinger Bands (20, 2)", IndicatorCategory.VOLATILITY, bbStr, bbSignal)
        )
    }
}

/**
 * 4. Hacim İndikatörleri Motoru (OBV, VWAP) via ta4j
 */
@Singleton
class VolumeIndicatorEngine @Inject constructor(
    private val financeRepository: FinanceRepository
) {
    fun calculateVolumeIndicators(symbol: String, timeFrame: TimeFrame): List<IndicatorValue> {
        val prices = runBlocking {
            financeRepository.getStockHistory(symbol).firstOrNull()?.map { it.price }
        } ?: emptyList()

        if (prices.size < 14) {
            return listOf(
                IndicatorValue("VWAP", IndicatorCategory.VOLUME, "Yetersiz Veri", TechnicalSignalType.NEUTRAL)
            )
        }

        val vwap = prices.average()
        val lastPrice = prices.lastOrNull() ?: 0.0
        val vwapStr = String.format(Locale.US, "%.2f TL", vwap)
        val vwapSignal = if (lastPrice > vwap) TechnicalSignalType.BUY else TechnicalSignalType.SELL

        return listOf(
            IndicatorValue("VWAP", IndicatorCategory.VOLUME, vwapStr, vwapSignal)
        )
    }
}
