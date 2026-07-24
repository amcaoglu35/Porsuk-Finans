package com.nexus.porsuk.domain.model

/**
 * Alt Skor Bileşen Detayı
 */
data class SubScoreDetail(
    val type: ScoreComponentType,
    val score: Int, // 0 - 100
    val weightPct: Double,
    val summaryText: String
)

/**
 * Porsuk Master Score Engine — Final Skor Sonucu Domain Modeli
 */
data class MasterScoreResult(
    val symbol: String,
    val masterScore: Int, // 0 - 100
    val level: ScoreLevel,
    val subScores: List<SubScoreDetail>,
    val calculatedAt: Long = System.currentTimeMillis()
)

/**
 * Skor Geçmişi ve Trend Detayı
 */
data class MasterScoreHistoryItem(
    val scoreId: Long = 0,
    val symbol: String,
    val masterScore: Int,
    val level: ScoreLevel,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Geleceğe Hazır AI Master Score & Confidence Stub Modeli
 */
data class AiScoreRecommendationStub(
    val symbol: String,
    val masterScore: Int,
    val aiConfidencePct: Double = 94.5, // AI Güven Oranı
    val sectorRelativeRank: String = "#3 / 48 Şirket (Sektör Liderleri Arasında)",
    val aiRecommendationSummary: String = "Orakul AI: Yüksek Master Score ve güçlü bilanço rasyoları nedeniyle uzun vadeli birikime uygun görülmektedir."
)
