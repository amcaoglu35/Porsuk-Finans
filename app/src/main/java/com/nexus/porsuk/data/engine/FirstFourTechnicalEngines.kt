package com.nexus.porsuk.data.engine

import com.nexus.porsuk.domain.model.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 1. Trend İndikatörleri Motoru (SMA, EMA, WMA, VWMA, HMA, SuperTrend, Ichimoku, Parabolic SAR)
 */
@Singleton
class TrendIndicatorEngine @Inject constructor() {
    fun calculateTrendIndicators(symbol: String, timeFrame: TimeFrame): List<IndicatorValue> {
        return listOf(
            IndicatorValue("SMA (50)", IndicatorCategory.TREND, "278.50 TL", TechnicalSignalType.BUY),
            IndicatorValue("EMA (20)", IndicatorCategory.TREND, "281.20 TL", TechnicalSignalType.STRONG_BUY),
            IndicatorValue("WMA (14)", IndicatorCategory.TREND, "283.00 TL", TechnicalSignalType.BUY),
            IndicatorValue("SuperTrend", IndicatorCategory.TREND, "272.00 TL Destek", TechnicalSignalType.STRONG_BUY),
            IndicatorValue("Ichimoku Cloud", IndicatorCategory.TREND, "Bulut Üstünde Boğa", TechnicalSignalType.BUY),
            IndicatorValue("Parabolic SAR", IndicatorCategory.TREND, "270.50 TL Boğa", TechnicalSignalType.BUY)
        )
    }
}

/**
 * 2. Momentum İndikatörleri Motoru (RSI, Stoch RSI, MACD, AO, CCI, ROC)
 */
@Singleton
class MomentumIndicatorEngine @Inject constructor() {
    fun calculateMomentumIndicators(symbol: String, timeFrame: TimeFrame): List<IndicatorValue> {
        return listOf(
            IndicatorValue("RSI (14)", IndicatorCategory.MOMENTUM, "58.4", TechnicalSignalType.NEUTRAL),
            IndicatorValue("Stochastic RSI", IndicatorCategory.MOMENTUM, "%78 K / %72 D", TechnicalSignalType.BUY),
            IndicatorValue("MACD (12, 26, 9)", IndicatorCategory.MOMENTUM, "+2.45 Kesişim", TechnicalSignalType.STRONG_BUY),
            IndicatorValue("Awesome Oscillator", IndicatorCategory.MOMENTUM, "+12.8 Yeşil", TechnicalSignalType.BUY),
            IndicatorValue("CCI (20)", IndicatorCategory.MOMENTUM, "+110.5", TechnicalSignalType.BUY)
        )
    }
}

/**
 * 3. Volatilite İndikatörleri Motoru (ATR, Bollinger Bands, Keltner, Donchian)
 */
@Singleton
class VolatilityIndicatorEngine @Inject constructor() {
    fun calculateVolatilityIndicators(symbol: String, timeFrame: TimeFrame): List<IndicatorValue> {
        return listOf(
            IndicatorValue("ATR (14)", IndicatorCategory.VOLATILITY, "8.20 TL", TechnicalSignalType.NEUTRAL),
            IndicatorValue("Bollinger Bands", IndicatorCategory.VOLATILITY, "Üst: 295, Alt: 270", TechnicalSignalType.NEUTRAL),
            IndicatorValue("Keltner Channel", IndicatorCategory.VOLATILITY, "Kanal İçinde", TechnicalSignalType.NEUTRAL),
            IndicatorValue("Donchian Channel", IndicatorCategory.VOLATILITY, "20 Günlük Yüksek", TechnicalSignalType.BUY)
        )
    }
}

/**
 * 4. Hacim İndikatörleri Motoru (OBV, CMF, MFI, VWAP, Volume Osc)
 */
@Singleton
class VolumeIndicatorEngine @Inject constructor() {
    fun calculateVolumeIndicators(symbol: String, timeFrame: TimeFrame): List<IndicatorValue> {
        return listOf(
            IndicatorValue("OBV (On-Balance Volume)", IndicatorCategory.VOLUME, "+14.2M Hacim", TechnicalSignalType.STRONG_BUY),
            IndicatorValue("CMF (Chaikin Money Flow)", IndicatorCategory.VOLUME, "+0.18 Para Girişi", TechnicalSignalType.BUY),
            IndicatorValue("MFI (Money Flow Index)", IndicatorCategory.VOLUME, "64.2 Giriş Güçlü", TechnicalSignalType.BUY),
            IndicatorValue("VWAP", IndicatorCategory.VOLUME, "282.40 TL Üstünde", TechnicalSignalType.STRONG_BUY)
        )
    }
}
