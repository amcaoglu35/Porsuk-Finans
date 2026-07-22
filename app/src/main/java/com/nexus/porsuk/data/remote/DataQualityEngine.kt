package com.nexus.porsuk.data.remote

import com.nexus.porsuk.data.local.entity.NewsItemEntity
import java.util.Locale

/**
 * Data Quality Evaluation Report.
 */
data class DataQualityReport(
    val totalScore: Int, // 0 to 100
    val priceScore: Int, // Max 20
    val newsScore: Int, // Max 15
    val technicalScore: Int, // Max 20
    val fxScore: Int, // Max 15
    val macroScore: Int, // Max 15
    val financialScore: Int, // Max 15
    val isBelowThreshold: Boolean, // True if totalScore < 60
    val warningMessage: String?,
    val summaryParagraph: String
)

/**
 * Data Quality Engine for Porsuk Finans.
 * Evaluates 6 data components prior to AI analysis:
 * 1. Fiyat verisi (Max 20)
 * 2. Haber tarihi (Max 15)
 * 3. Teknik göstergelerin güncellenme zamanı (Max 20)
 * 4. Döviz kuru (Max 15)
 * 5. Makro veriler (Max 15)
 * 6. Bilanço tarihi (Max 15)
 * 
 * Sums up Total Data Quality Score (0-100).
 * Issues explicit warnings if Data Quality Score < 60 and never hides missing data.
 */
object DataQualityEngine {

    fun evaluate(
        priceTimestamp: Long? = null,
        newsList: List<NewsItemEntity> = emptyList(),
        historicalPricesCount: Int = 20,
        hasFxRates: Boolean = true,
        hasMacroData: Boolean = true,
        peRatio: Double? = null
    ): DataQualityReport {
        val now = System.currentTimeMillis()

        // 1. Fiyat Verisi Güncelliği (Max 20 Puan)
        val priceScore = when {
            priceTimestamp == null -> 10
            (now - priceTimestamp) < 60 * 60 * 1000L -> 20
            (now - priceTimestamp) < 24 * 60 * 60 * 1000L -> 15
            else -> 5
        }

        // 2. Haber Tarihi & Akışı (Max 15 Puan)
        val newsScore = when {
            newsList.isEmpty() -> 0
            else -> 15
        }

        // 3. Teknik Göstergelerin Güncellenme Zamanı (Max 20 Puan)
        val technicalScore = when {
            historicalPricesCount >= 20 -> 20
            historicalPricesCount >= 10 -> 12
            historicalPricesCount >= 5 -> 8
            else -> 0
        }

        // 4. Döviz Kuru (Max 15 Puan)
        val fxScore = if (hasFxRates) 15 else 5

        // 5. Makro Veriler (Max 15 Puan)
        val macroScore = if (hasMacroData) 15 else 5

        // 6. Bilanço Tarihi / Rasyolar (Max 15 Puan)
        val financialScore = if (peRatio != null && peRatio > 0) 15 else 0

        val totalScore = priceScore + newsScore + technicalScore + fxScore + macroScore + financialScore
        val isBelowThreshold = totalScore < 60

        val warnings = mutableListOf<String>()
        if (priceScore < 15) warnings.add("Fiyat verisi güncelliği zayıf")
        if (newsScore == 0) warnings.add("Güncel haber akışı eksik")
        if (technicalScore < 12) warnings.add("Teknik analiz veri dizisi kısıtlı")
        if (financialScore == 0) warnings.add("Bilanço/FK verisi eksik")

        val warningMsg = if (isBelowThreshold) {
            "⚠️ DÜŞÜK VERİ KALİTESİ UYARISI ($totalScore/100): ${warnings.joinToString("; ")}. Üretilen analizlerde temkinli olunuz."
        } else if (warnings.isNotEmpty()) {
            "ℹ️ Veri Kalitesi Bildirimi ($totalScore/100): ${warnings.joinToString("; ")}."
        } else null

        val summary = "VERİ KALİTESİ SKORU: $totalScore/100 (Fiyat: $priceScore/20, Haber: $newsScore/15, Teknik: $technicalScore/20, Döviz: $fxScore/15, Makro: $macroScore/15, Bilanço: $financialScore/15)."

        return DataQualityReport(
            totalScore = totalScore,
            priceScore = priceScore,
            newsScore = newsScore,
            technicalScore = technicalScore,
            fxScore = fxScore,
            macroScore = macroScore,
            financialScore = financialScore,
            isBelowThreshold = isBelowThreshold,
            warningMessage = warningMsg,
            summaryParagraph = summary
        )
    }
}
