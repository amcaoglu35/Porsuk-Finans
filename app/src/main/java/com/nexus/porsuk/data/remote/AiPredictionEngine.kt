package com.nexus.porsuk.data.remote

import com.nexus.porsuk.data.model.IndicatorCalculator
import java.util.Locale
import kotlin.math.sqrt

/**
 * AI Prediction Engine for Porsuk Finans.
 * Calculates 9 quantitative signals in Kotlin (RSI, MACD, EMA Trend, Bollinger, Volume, Volatility, 20-Day Trend, News Sentiment, Macro Outlook)
 * and generates a single-paragraph summary for Gemini to produce probabilistic forecasts without guessing exact prices.
 */
data class PredictionSignals(
    val bullishProbPct: Int,
    val bearishProbPct: Int,
    val neutralProbPct: Int,
    val singleParagraphSummary: String
)

object AiPredictionEngine {

    fun analyze(
        symbol: String,
        prices: List<Double>,
        volumes: List<Double> = emptyList(),
        newsTitles: List<String> = emptyList(),
        macroParagraph: String = ""
    ): PredictionSignals {
        if (prices.size < 5) {
            return PredictionSignals(
                bullishProbPct = 33,
                bearishProbPct = 33,
                neutralProbPct = 34,
                singleParagraphSummary = "[$symbol SİNYAL ÖZETİ]: Fiyat verisi kısıtlı. 9 Sinyal (RSI: Nötr, MACD: Nötr, EMA: Nötr, Bollinger: Nötr, Hacim: Nötr, Volatilite: Düşük, 20G Trend: Yatay, Haber: Nötr, Makro: Dengeli) eşit olasılık dağılımı gösteriyor."
            )
        }

        // 1. RSI
        val rsiVal = IndicatorCalculator.calculateRsi(prices, 14) ?: 50.0
        val rsiSignal = when {
            rsiVal >= 70.0 -> "Aşırı Alım (Düzeltme Riski)"
            rsiVal <= 30.0 -> "Aşırı Satım (Tepki Potansiyeli)"
            rsiVal >= 55.0 -> "Pozitif İvme"
            rsiVal <= 45.0 -> "Negatif Baskı"
            else -> "Dengeli Nötr"
        }

        // 2. MACD
        val macdData = IndicatorCalculator.calculateMacd(prices)
        val macdSignal = when {
            macdData == null -> "Nötr"
            macdData.histogram > 0 -> "Pozitif Momentum (AL)"
            macdData.histogram < 0 -> "Negatif İvme (SAT)"
            else -> "Nötr Kesişim"
        }

        // 3. EMA Trend (20 vs 50)
        val ema20 = calculateEma(prices, 20)
        val ema50 = calculateEma(prices, 50)
        val emaSignal = when {
            ema20 != null && ema50 != null -> if (ema20 > ema50) "Pozitif Altın Kesişim" else "Negatif Ölüm Kesişimi"
            else -> "Trend Belirsiz"
        }

        // 4. Bollinger
        val bollinger = IndicatorCalculator.calculateBollinger(prices, 20)
        val lastPrice = prices.last()
        val bollingerSignal = when {
            bollinger == null -> "Nötr"
            lastPrice >= bollinger.upper -> "Üst Bant Sıkışması"
            lastPrice <= bollinger.lower -> "Alt Bant Sıkışması"
            lastPrice > bollinger.middle -> "Pozitif Kanal"
            else -> "Zayıf Kanal"
        }

        // 5. Volume
        val volumeSignal = if (volumes.size >= 5 && volumes.last() > volumes.take(volumes.size - 1).average() * 1.2) "Yüksek Hacim Desteği" else "Normal Hacim"

        // 6. Volatility
        val returns = prices.zipWithNext { a, b -> if (a > 0) (b - a) / a else 0.0 }
        val variance = returns.map { it * it }.average()
        val annualizedVol = sqrt(variance) * sqrt(252.0) * 100.0
        val volSignal = when {
            annualizedVol >= 40.0 -> "Yüksek Oynaklık (%${String.format(Locale.US, "%.1f", annualizedVol)})"
            annualizedVol >= 20.0 -> "Orta Volatilite"
            else -> "Kararlı Düşük Oynaklık"
        }

        // 7. 20-Day Trend
        val last20 = prices.takeLast(20)
        val first20 = last20.first()
        val trend20Pct = if (first20 > 0) (last20.last() - first20) / first20 * 100.0 else 0.0
        val trend20Signal = when {
            trend20Pct >= 5.0 -> "Yükseliş Trendi (%${String.format(Locale.US, "%.1f", trend20Pct)})"
            trend20Pct <= -5.0 -> "Düşüş Trendi (%${String.format(Locale.US, "%.1f", trend20Pct)})"
            else -> "Yatay Seyir (%${String.format(Locale.US, "%.1f", trend20Pct)})"
        }

        // 8. News Sentiment
        val newsSignal = if (newsTitles.any { it.contains("rekor", true) || it.contains("kar", true) }) "Pozitif Haber Akışı" else "Dengeli Haber Akışı"

        // 9. Macro Outlook
        val macroSignal = if (macroParagraph.contains("pozitif", true) || macroParagraph.contains("yüksel", true)) "Pozitif Makro Rüzgar" else "Nötr Makro Dengesi"

        // Calculate Probabilities
        var bullScore = 40
        var bearScore = 30
        var neutralScore = 30

        if (rsiVal in 45.0..65.0) bullScore += 10
        if (macdData?.histogram ?: 0.0 > 0) bullScore += 15 else bearScore += 15
        if (trend20Pct > 3.0) bullScore += 15 else if (trend20Pct < -3.0) bearScore += 15

        val totalScore = (bullScore + bearScore + neutralScore).toDouble()
        val bullPct = ((bullScore / totalScore) * 100).toInt()
        val bearPct = ((bearScore / totalScore) * 100).toInt()
        val neutralPct = 100 - bullPct - bearPct

        // Single Paragraph Summary
        val summary = "[$symbol OLASILIK SİNYAL ÖZETİ]: 9 Metrik (RSI: $rsiSignal, MACD: $macdSignal, EMA: $emaSignal, Bollinger: $bollingerSignal, Hacim: $volumeSignal, Volatilite: $volSignal, 20G Trend: $trend20Signal, Haber: $newsSignal, Makro: $macroSignal) ışığında tahmini eğilim %$bullPct Yükseliş, %$bearPct Düşüş ve %$neutralPct Yatay olarak hesaplanmıştır."

        return PredictionSignals(
            bullishProbPct = bullPct,
            bearishProbPct = bearPct,
            neutralProbPct = neutralPct,
            singleParagraphSummary = summary
        )
    }

    private fun calculateEma(prices: List<Double>, period: Int): Double? {
        if (prices.size < period) return null
        val k = 2.0 / (period + 1)
        var ema = prices.take(period).average()
        for (i in period until prices.size) {
            ema = (prices[i] * k) + (ema * (1 - k))
        }
        return ema
    }
}
