package com.nexus.porsuk.domain.model

/**
 * Porsuk Master Score Engine — 7 Dereceli Skor Seviyeleri (Score Levels)
 */
enum class ScoreLevel(
    val rangeMin: Int,
    val rangeMax: Int,
    val displayName: String,
    val colorHex: Long
) {
    EXCEPTIONAL(90, 100, "Exceptional (Mükemmel Üstü)", 0xFF00C853),
    EXCELLENT(80, 89, "Excellent (Mükemmel)", 0xFF2E7D32),
    STRONG(70, 79, "Strong (Güçlü)", 0xFF00B0FF),
    NEUTRAL(60, 69, "Neutral (Dengeli / Nötr)", 0xFFFFB300),
    WEAK(40, 59, "Weak (Zayıf)", 0xFFFF6D00),
    RISKY(20, 39, "Risky (Riskli)", 0xFFDD2C00),
    CRITICAL(0, 19, "Critical (Kritik / Tehlikeli)", 0xFFD50000);

    companion object {
        fun fromScore(score: Int): ScoreLevel {
            return entries.firstOrNull { score in it.rangeMin..it.rangeMax } ?: NEUTRAL
        }
    }
}

/**
 * Porsuk Master Score Engine — 8 Alt Skor Bileşeni (Sub-Score Components)
 */
enum class ScoreComponentType(val displayName: String, val defaultWeightPct: Double) {
    FINANCIAL("Finansal Skor", 20.0),
    TECHNICAL("Teknik Skor", 15.0),
    DIVIDEND("Temettü Skoru", 10.0),
    RISK("Risk Skoru", 15.0),
    MARKET("Piyasa Skoru", 10.0),
    GROWTH("Büyüme Skoru", 10.0),
    VALUATION("Değerleme Skoru", 10.0),
    MOMENTUM("Momentum Skoru", 10.0);
}
