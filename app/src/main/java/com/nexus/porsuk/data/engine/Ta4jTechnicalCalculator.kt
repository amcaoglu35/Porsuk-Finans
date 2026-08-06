package com.nexus.porsuk.data.engine

import org.ta4j.core.BarSeries
import org.ta4j.core.BaseBarSeriesBuilder
import org.ta4j.core.indicators.ATRIndicator
import org.ta4j.core.indicators.MACDIndicator
import org.ta4j.core.indicators.RSIIndicator
import org.ta4j.core.indicators.SMAIndicator
import org.ta4j.core.indicators.EMAIndicator
import org.ta4j.core.indicators.helpers.ClosePriceIndicator
import org.ta4j.core.indicators.bollinger.BollingerBandsLowerIndicator
import org.ta4j.core.indicators.bollinger.BollingerBandsMiddleIndicator
import org.ta4j.core.indicators.bollinger.BollingerBandsUpperIndicator
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator
import org.ta4j.core.num.DecimalNum
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

data class Ta4jIndicatorResults(
    val rsi: Double?,
    val macdValue: Double?,
    val macdSignal: Double?,
    val macdHist: Double?,
    val bollingerUpper: Double?,
    val bollingerMiddle: Double?,
    val bollingerLower: Double?,
    val atr: Double?,
    val sma50: Double?,
    val ema20: Double?,
    val isSufficientData: Boolean
)

object Ta4jTechnicalCalculator {

    /**
     * Map a list of price/OHLCV data into a ta4j BarSeries.
     * Minimum required bars for reliable indicator calculations is 14.
     */
    fun createBarSeries(
        name: String,
        prices: List<Double>,
        timestamps: List<Long> = emptyList(),
        volumes: List<Double> = emptyList()
    ): BarSeries {
        val series = BaseBarSeriesBuilder().withName(name).build()
        if (prices.isEmpty()) return series

        val now = System.currentTimeMillis()
        val duration = Duration.ofDays(1)

        prices.forEachIndexed { index, closePrice ->
            val ts = if (index < timestamps.size) timestamps[index] else now - ((prices.size - 1 - index) * 86400000L)
            val zdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(ts), ZoneId.systemDefault())
            
            val openPrice = if (index > 0) prices[index - 1] else closePrice
            val highPrice = maxOf(openPrice, closePrice) * 1.002
            val lowPrice = minOf(openPrice, closePrice) * 0.998
            val volume = if (index < volumes.size) volumes[index] else 1000.0

            try {
                series.addBar(
                    duration,
                    zdt,
                    DecimalNum.valueOf(openPrice),
                    DecimalNum.valueOf(highPrice),
                    DecimalNum.valueOf(lowPrice),
                    DecimalNum.valueOf(closePrice),
                    DecimalNum.valueOf(volume)
                )
            } catch (_: Exception) {
                // Ignore bar addition errors if timestamp order clashes
            }
        }
        return series
    }

    /**
     * Calculate RSI, MACD, Bollinger Bands, ATR, SMA and EMA using ta4j core indicators.
     */
    fun calculateIndicators(series: BarSeries): Ta4jIndicatorResults {
        if (series.barCount < 14) {
            return Ta4jIndicatorResults(
                rsi = null,
                macdValue = null,
                macdSignal = null,
                macdHist = null,
                bollingerUpper = null,
                bollingerMiddle = null,
                bollingerLower = null,
                atr = null,
                sma50 = null,
                ema20 = null,
                isSufficientData = false
            )
        }

        val endIndex = series.endIndex
        val closePrice = ClosePriceIndicator(series)

        // 1. RSI (14)
        val rsiIndicator = RSIIndicator(closePrice, 14)
        val rsiVal = rsiIndicator.getValue(endIndex).doubleValue()

        // 2. MACD (12, 26, 9)
        val macdIndicator = MACDIndicator(closePrice, 12, 26)
        val macdSignalIndicator = EMAIndicator(macdIndicator, 9)
        val macdVal = macdIndicator.getValue(endIndex).doubleValue()
        val macdSignalVal = macdSignalIndicator.getValue(endIndex).doubleValue()
        val macdHistVal = macdVal - macdSignalVal

        // 3. Bollinger Bands (20, 2 stddev)
        val bbMiddle = BollingerBandsMiddleIndicator(closePrice)
        val stdDev = StandardDeviationIndicator(closePrice, 20)
        val bbUpper = BollingerBandsUpperIndicator(bbMiddle, stdDev, DecimalNum.valueOf(2))
        val bbLower = BollingerBandsLowerIndicator(bbMiddle, stdDev, DecimalNum.valueOf(2))

        val bbMidVal = bbMiddle.getValue(endIndex).doubleValue()
        val bbUpVal = bbUpper.getValue(endIndex).doubleValue()
        val bbLowVal = bbLower.getValue(endIndex).doubleValue()

        // 4. ATR (14)
        val atrIndicator = ATRIndicator(series, 14)
        val atrVal = atrIndicator.getValue(endIndex).doubleValue()

        // 5. SMA 50 & EMA 20
        val sma50Indicator = SMAIndicator(closePrice, 50.coerceAtMost(series.barCount))
        val ema20Indicator = EMAIndicator(closePrice, 20.coerceAtMost(series.barCount))
        val sma50Val = sma50Indicator.getValue(endIndex).doubleValue()
        val ema20Val = ema20Indicator.getValue(endIndex).doubleValue()

        return Ta4jIndicatorResults(
            rsi = rsiVal,
            macdValue = macdVal,
            macdSignal = macdSignalVal,
            macdHist = macdHistVal,
            bollingerUpper = bbUpVal,
            bollingerMiddle = bbMidVal,
            bollingerLower = bbLowVal,
            atr = atrVal,
            sma50 = sma50Val,
            ema20 = ema20Val,
            isSufficientData = true
        )
    }

    /**
     * RSI (14 period) serisi hesapla (NullPointerException korumalı)
     */
    fun calculateRSI(barSeries: BarSeries, period: Int = 14): List<Double> {
        if (barSeries.isEmpty || barSeries.barCount < period) return emptyList()
        val rsiIndicator = RSIIndicator(ClosePriceIndicator(barSeries), period)
        return (0 until barSeries.barCount).map { i ->
            try {
                rsiIndicator.getValue(i).doubleValue()
            } catch (_: Exception) {
                0.0
            }
        }
    }

    /**
     * MACD serisi hesapla (Fast, Slow, Signal)
     */
    fun calculateMACD(
        barSeries: BarSeries,
        fastPeriod: Int = 12,
        slowPeriod: Int = 26,
        signalPeriod: Int = 9
    ): Triple<List<Double>, List<Double>, List<Double>> {
        if (barSeries.isEmpty || barSeries.barCount < slowPeriod) {
            return Triple(emptyList(), emptyList(), emptyList())
        }
        val closePrices = ClosePriceIndicator(barSeries)
        val macdIndicator = MACDIndicator(closePrices, fastPeriod, slowPeriod)
        val signalLine = EMAIndicator(macdIndicator, signalPeriod)

        val macdValues = (0 until barSeries.barCount).map { macdIndicator.getValue(it).doubleValue() }
        val signalValues = (0 until barSeries.barCount).map { signalLine.getValue(it).doubleValue() }
        val histogramValues = macdValues.zip(signalValues) { m, s -> m - s }

        return Triple(macdValues, signalValues, histogramValues)
    }
}
