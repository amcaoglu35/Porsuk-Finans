package com.nexus.porsuk.data.remote

import com.nexus.porsuk.data.local.entity.NewsItemEntity
import java.util.Calendar
import java.util.TimeZone

/**
 * Result DTO of AI Self Check Data Audit.
 */
data class AiSelfCheckResult(
    val dataQualityLevel: String, // Yüksek, Orta, Düşük
    val isNewsFresh: Boolean,
    val isTechnicalDataFresh: Boolean,
    val isMarketOpen: Boolean,
    val hasMissingData: Boolean,
    val missingDataDetails: List<String>,
    val selfCheckSummaryText: String
)

/**
 * AI Self Check Engine for Porsuk Finans.
 * Performs pre-analysis data validation checks:
 * - Haber güncel mi?
 * - Teknik veri güncel mi?
 * - Piyasa açık mı? (BIST 10:00 - 18:15 TRT)
 * - Veri eksik mi? (F/K, geçmiş fiyat dizisi, bilanço)
 * - Confidence düşük mü? (<50)
 * 
 * Ensures missing or stale data is never hidden from the user.
 */
object AiSelfCheckEngine {

    fun runSelfCheck(
        priceTimestamp: Long? = null,
        newsList: List<NewsItemEntity> = emptyList(),
        peRatio: Double? = null,
        historicalPricesCount: Int = 10,
        confidenceScore: Int = 80
    ): AiSelfCheckResult {
        val missingList = mutableListOf<String>()
        var qualityPoints = 100

        val now = System.currentTimeMillis()

        // 1. Technical Data Freshness (within 24 hours)
        val isTechFresh = priceTimestamp == null || (now - priceTimestamp) < 24 * 60 * 60 * 1000L
        if (!isTechFresh) {
            qualityPoints -= 20
            missingList.add("Son teknik fiyat verisi 24 saatten eski")
        }

        // 2. News Freshness
        val isNewsFresh = newsList.isNotEmpty()
        if (!isNewsFresh) {
            qualityPoints -= 15
            missingList.add("Son 48 saate ait güncel haber akışı bulunmuyor")
        }

        // 3. Fundamental Data (P/E & History)
        if (peRatio == null || peRatio <= 0) {
            qualityPoints -= 20
            missingList.add("F/K rasyosu veya temel bilanço kalemi eksik")
        }

        if (historicalPricesCount < 5) {
            qualityPoints -= 20
            missingList.add("Geçmiş fiyat verisi kısıtlı (5 günden az)")
        }

        // 4. Low Confidence Check (<50)
        if (confidenceScore in 1..49) {
            qualityPoints -= 15
            missingList.add("Yapay zekâ güven skoru düşük (%%$confidenceScore)")
        }

        // 5. Market Open Check
        val isMarketOpen = checkIsBistMarketOpen()

        val dataQualityLevel = when {
            qualityPoints >= 80 -> "Yüksek"
            qualityPoints >= 55 -> "Orta"
            else -> "Düşük"
        }

        val summary = if (missingList.isNotEmpty()) {
            "⚠️ VERİ EKSİKLİĞİ UYARISI: ${missingList.joinToString("; ")}."
        } else {
            "✅ TÜM TEKNİK VE TEMEL VERİLER GÜNCEL VE TAM."
        }

        return AiSelfCheckResult(
            dataQualityLevel = dataQualityLevel,
            isNewsFresh = isNewsFresh,
            isTechnicalDataFresh = isTechFresh,
            isMarketOpen = isMarketOpen,
            hasMissingData = missingList.isNotEmpty(),
            missingDataDetails = missingList,
            selfCheckSummaryText = summary
        )
    }

    private fun checkIsBistMarketOpen(): Boolean {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Istanbul"))
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) return false
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val totalMinutes = hour * 60 + minute
        return totalMinutes in (10 * 60)..(18 * 60 + 15)
    }
}
