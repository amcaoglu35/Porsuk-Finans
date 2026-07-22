package com.nexus.porsuk.data.remote

import com.nexus.porsuk.data.local.entity.NewsItemEntity

/**
 * Trust & Reliability Info DTO for News Publishers.
 */
data class SourceReliabilityInfo(
    val sourceName: String,
    val trustScore: Int, // 0 to 100
    val weightFactor: Double, // 0.1 to 1.0
    val trustCategory: String // HIGH, MEDIUM, LOW
)

/**
 * Source Reliability Engine for Porsuk Finans.
 * Evaluates the trustworthiness of news sources (KAP: %100, Bloomberg: %95, Investing: %75, Social Media: %40).
 * Ensures low-reliability rumor news has lower weighting, while official high-trust sources drive sentiment.
 */
object SourceReliabilityEngine {

    private val defaultSourceTrustMap = mapOf(
        "KAP" to SourceReliabilityInfo("KAP (Kamuyu Aydınlatma Platformu)", 100, 1.0, "HIGH"),
        "BLOOMBERG" to SourceReliabilityInfo("Bloomberg HT / International", 95, 0.95, "HIGH"),
        "REUTERS" to SourceReliabilityInfo("Reuters Finans", 95, 0.95, "HIGH"),
        "ANADOLU" to SourceReliabilityInfo("Anadolu Ajansı Finans", 90, 0.90, "HIGH"),
        "MATRIKS" to SourceReliabilityInfo("Matriks Veri Yayın", 90, 0.90, "HIGH"),
        "CNBC" to SourceReliabilityInfo("CNBC Finans", 85, 0.85, "HIGH"),
        "INVESTING" to SourceReliabilityInfo("Investing.com", 75, 0.75, "MEDIUM"),
        "BIGPARA" to SourceReliabilityInfo("Bigpara Ekonomi", 70, 0.70, "MEDIUM"),
        "MYNET" to SourceReliabilityInfo("Mynet Finans", 65, 0.65, "MEDIUM"),
        "TWITTER" to SourceReliabilityInfo("Sosyal Medya Duyumu", 40, 0.40, "LOW"),
        "FORUM" to SourceReliabilityInfo("Borsa Forum Şayiası", 30, 0.30, "LOW")
    )

    /**
     * Resolves reliability info for a given news publisher string.
     */
    fun getReliabilityInfo(sourceOrPublisher: String?): SourceReliabilityInfo {
        if (sourceOrPublisher.isNullOrBlank()) {
            return SourceReliabilityInfo("Genel Haber Kaynağı", 70, 0.70, "MEDIUM")
        }

        val upper = sourceOrPublisher.uppercase()
        for ((key, info) in defaultSourceTrustMap) {
            if (upper.contains(key)) return info
        }

        return SourceReliabilityInfo(sourceOrPublisher, 70, 0.70, "MEDIUM")
    }

    /**
     * Formats headlines with explicit publisher trust scores and weightings for Gemini prompt injection.
     */
    fun formatHeadlinesWithTrust(newsList: List<NewsItemEntity>): String {
        if (newsList.isEmpty()) return "Kritik yeni haber akışı bulunmuyor."

        return newsList.take(5).joinToString("\n") { news ->
            val info = getReliabilityInfo(news.source)
            "• [Kaynak: ${info.sourceName} | Güven Skoru: %${info.trustScore} | Ağırlık: ${info.weightFactor}x]: \"${news.title}\" (Duyarlılık: ${news.sentiment ?: "NEUTRAL"})"
        }
    }
}
