package com.nexus.porsuk.data.remote

import com.nexus.porsuk.data.model.BollingerData
import com.nexus.porsuk.data.model.IndicatorCalculator
import com.nexus.porsuk.data.model.MacdData
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Local Decision Engine for Porsuk Finans.
 * Performs local Kotlin math calculations for 8 quantitative metrics (RSI, MACD, EMA, Bollinger, Trend, Volume, News Sentiment, Volatility).
 * Injects a pre-computed decision summary into Gemini prompts to minimize token usage and avoid AI calculation overhead.
 */
data class DecisionResult(
    val rsi: Double?,
    val rsiStatus: String,
    val macd: MacdData?,
    val macdStatus: String,
    val ema20: Double?,
    val ema50: Double?,
    val emaStatus: String,
    val bollinger: BollingerData?,
    val bollingerStatus: String,
    val trend: String,
    val volumeStatus: String,
    val newsSentiment: String,
    val volatilityPercent: Double,
    val volatilityStatus: String,
    val preComputedSummary: String
)

object DecisionEngine {

    /**
     * Executes local indicator & quantitative analysis on raw price/volume/news data.
     */
    fun analyze(
        prices: List<Double>,
        volumes: List<Double> = emptyList(),
        newsTitles: List<String> = emptyList()
    ): DecisionResult {
        if (prices.isEmpty()) {
            return DecisionResult(
                rsi = null, rsiStatus = "Yetersiz Veri",
                macd = null, macdStatus = "Yetersiz Veri",
                ema20 = null, ema50 = null, emaStatus = "Yetersiz Veri",
                bollinger = null, bollingerStatus = "Yetersiz Veri",
                trend = "Yetersiz Veri",
                volumeStatus = "Veri Yok",
                newsSentiment = "Veri Yok",
                volatilityPercent = 0.0, volatilityStatus = "Veri Yok",
                preComputedSummary = "Lokal karar motoru: Yetersiz fiyat verisi."
            )
        }

        // 1. RSI (14)
        val rsiVal = IndicatorCalculator.calculateRsi(prices, 14)
        val rsiStatus = when {
            rsiVal == null -> "Yetersiz Veri"
            rsiVal >= 70.0 -> "Aşırı Alım Bölgesi (RSI: ${String.format("%.1f", rsiVal)})"
            rsiVal <= 30.0 -> "Aşırı Satım Bölgesi (RSI: ${String.format("%.1f", rsiVal)})"
            rsiVal >= 55.0 -> "Nötr-Pozitif İvme (RSI: ${String.format("%.1f", rsiVal)})"
            rsiVal <= 45.0 -> "Nötr-Negatif Baskı (RSI: ${String.format("%.1f", rsiVal)})"
            else -> "Dengeli Nötr Bölge (RSI: ${String.format("%.1f", rsiVal)})"
        }

        // 2. MACD
        val macdData = IndicatorCalculator.calculateMacd(prices)
        val macdStatus = when {
            macdData == null -> "Yetersiz Veri"
            macdData.histogram > 0 -> "Pozitif Momentum (AL Sinyali - Macd: ${String.format("%.2f", macdData.macd)})"
            macdData.histogram < 0 -> "Negatif Momentum (SAT Sinyali - Macd: ${String.format("%.2f", macdData.macd)})"
            else -> "Nötr Kesişim (Macd: ${String.format("%.2f", macdData.macd)})"
        }

        // 3. EMA (20 & 50)
        val ema20Val = calculateEma(prices, 20)
        val ema50Val = calculateEma(prices, 50)
        val emaStatus = when {
            ema20Val != null && ema50Val != null -> {
                if (ema20Val > ema50Val) "Pozitif Altın Kesişim (EMA20: ${String.format("%.2f", ema20Val)} > EMA50: ${String.format("%.2f", ema50Val)})"
                else "Negatif Ölüm Kesişimi (EMA20: ${String.format("%.2f", ema20Val)} < EMA50: ${String.format("%.2f", ema50Val)})"
            }
            ema20Val != null -> "EMA20: ${String.format("%.2f", ema20Val)}"
            else -> "Yetersiz Veri"
        }

        // 4. Bollinger Bands
        val bollingerData = IndicatorCalculator.calculateBollinger(prices, 20)
        val lastPrice = prices.last()
        val bollingerStatus = when {
            bollingerData == null -> "Yetersiz Veri"
            lastPrice >= bollingerData.upper -> "Üst Bant Üzerinde (Aşırı Yükseliş Baskısı)"
            lastPrice <= bollingerData.lower -> "Alt Bant Altında (Aşırı Düşüş Baskısı)"
            lastPrice > bollingerData.middle -> "Orta Bant Üzerinde (Pozitif Kanal)"
            else -> "Orta Bant Altında (Zayıf Kanal)"
        }

        // 5. Trend Analysis
        val trend = calculateTrend(prices)

        // 6. Volume Analysis
        val volumeStatus = calculateVolumeStatus(volumes)

        // 7. News Sentiment Analysis
        val newsSentiment = calculateNewsSentiment(newsTitles)

        // 8. Volatility Analysis
        val (volatilityPercent, volatilityStatus) = calculateVolatility(prices)

        // Build Pre-computed Summary for Gemini
        val summaryBuilder = StringBuilder()
        summaryBuilder.append("=== LOKAL KARAR MOTORU (PRE-COMPUTED DECISION SUMMARY) ===\n")
        summaryBuilder.append("• RSI (14): ").append(rsiStatus).append("\n")
        summaryBuilder.append("• MACD: ").append(macdStatus).append("\n")
        summaryBuilder.append("• EMA Trendi: ").append(emaStatus).append("\n")
        summaryBuilder.append("• Bollinger Görünümü: ").append(bollingerStatus).append("\n")
        summaryBuilder.append("• Yön ve Trend Gücü: ").append(trend).append("\n")
        summaryBuilder.append("• Hacim Görünümü: ").append(volumeStatus).append("\n")
        summaryBuilder.append("• Haber Duyarlılık Algısı: ").append(newsSentiment).append("\n")
        summaryBuilder.append("• Tarihsel Volatilite: %").append(String.format("%.1f", volatilityPercent)).append(" (").append(volatilityStatus).append(")\n")
        summaryBuilder.append("=========================================================\n")
        summaryBuilder.append("NOT: İndikatör hesaplamaları cihaz tarafında tamamlanmıştır. Lütfen hesaplama yapmadan doğrudan bu hazır özet üzerinden profesyonel yorumlamanı gerçekleştir.")

        return DecisionResult(
            rsi = rsiVal,
            rsiStatus = rsiStatus,
            macd = macdData,
            macdStatus = macdStatus,
            ema20 = ema20Val,
            ema50 = ema50Val,
            emaStatus = emaStatus,
            bollinger = bollingerData,
            bollingerStatus = bollingerStatus,
            trend = trend,
            volumeStatus = volumeStatus,
            newsSentiment = newsSentiment,
            volatilityPercent = volatilityPercent,
            volatilityStatus = volatilityStatus,
            preComputedSummary = summaryBuilder.toString()
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

    private fun calculateTrend(prices: List<Double>): String {
        if (prices.size < 5) return "Nötr (Yetersiz Veri)"
        val firstPrice = prices.first()
        val lastPrice = prices.last()
        val changePct = if (firstPrice > 0) (lastPrice - firstPrice) / firstPrice * 100 else 0.0

        return when {
            changePct >= 10.0 -> "Güçlü Yükseliş Trendi (%${String.format("%.1f", changePct)})"
            changePct >= 2.0 -> "Ilımlı Yükseliş Trendi (%${String.format("%.1f", changePct)})"
            changePct <= -10.0 -> "Güçlü Düşüş Trendi (%${String.format("%.1f", changePct)})"
            changePct <= -2.0 -> "Ilımlı Düşüş Trendi (%${String.format("%.1f", changePct)})"
            else -> "Yatay / Nötr Seyir (%${String.format("%.1f", changePct)})"
        }
    }

    private fun calculateVolumeStatus(volumes: List<Double>): String {
        if (volumes.size < 5) return "Normal Hacim (Veri Kısıtlı)"
        val lastVolume = volumes.last()
        val avgVolume = volumes.take(volumes.size - 1).average()
        if (avgVolume <= 0) return "Normal Hacim"

        val ratio = (lastVolume / avgVolume) * 100
        return when {
            ratio >= 200.0 -> "Çok Yüksek İşlem Hacmi (Ortalamanın %${String.format("%.0f", ratio)} Üzerinde)"
            ratio >= 120.0 -> "Yüksek İşlem Hacmi (Ortalamanın %${String.format("%.0f", ratio)} Üzerinde)"
            ratio <= 60.0 -> "Düşük İşlem Hacmi (Hacimsiz Sıkışma)"
            else -> "Normal Ortalama Hacim"
        }
    }

    private fun calculateNewsSentiment(newsTitles: List<String>): String {
        if (newsTitles.isEmpty()) return "Haber Akışı Nötr / Veri Yok"
        val positiveKeywords = listOf("büyüme", "rekor", "kar", "kâr", "anlaşma", "ihale", "artış", "temettü", "satın alım", "yükseliş", "kazanç")
        val negativeKeywords = listOf("düşüş", "zarar", "iptal", "dava", "ceza", "kriz", "baskı", "azalış", "tehlike", "risk")

        var posCount = 0
        var negCount = 0

        newsTitles.forEach { title ->
            val lower = title.lowercase()
            if (positiveKeywords.any { lower.contains(it) }) posCount++
            if (negativeKeywords.any { lower.contains(it) }) negCount++
        }

        return when {
            posCount > negCount -> "Ağırlıklı POZİTİF ($posCount Olumlu Haber)"
            negCount > posCount -> "Ağırlıklı NEGATİF ($negCount Olumsuz Haber)"
            else -> "Dengeli NÖTR Haber Akışı"
        }
    }

    private fun calculateVolatility(prices: List<Double>): Pair<Double, String> {
        if (prices.size < 3) return Pair(0.0, "Düşük")
        val returns = prices.zipWithNext { a, b -> if (a > 0) (b - a) / a else 0.0 }
        val avg = returns.average()
        val variance = returns.map { (it - avg) * (it - avg) }.average()
        val dailyVol = sqrt(variance)
        val annualizedVol = dailyVol * sqrt(252.0) * 100.0

        val status = when {
            annualizedVol >= 45.0 -> "Yüksek Riskli Volatilite"
            annualizedVol >= 25.0 -> "Orta Volatilite"
            else -> "Düşük Volatilite (Kararlı)"
        }
        return Pair(annualizedVol, status)
    }
}
