package com.nexus.porsuk.domain.model

/**
 * Portföy Sağlık Seviyesi (Health Score Level)
 */
enum class HealthScoreLevel(val minScore: Int, val maxScore: Int, val displayName: String, val colorHex: Long) {
    EXCELLENT(85, 100, "Mükemmel Sağlık 🚀", 0xFF00C853),
    STRONG(70, 84, "Güçlü Sağlık 🟢", 0xFF2E7D32),
    MODERATE(55, 69, "Orta Seviye Sağlık 🟡", 0xFFFFB300),
    WEAK(40, 54, "Zayıf / Düzeltme Gerekli 🟠", 0xFFFF6D00),
    CRITICAL(0, 39, "Kritik Riskli Portföy 🔴", 0xFFD50000);

    companion object {
        fun fromScore(score: Int): HealthScoreLevel {
            return entries.firstOrNull { score in it.minScore..it.maxScore } ?: MODERATE
        }
    }
}

/**
 * Yeniden Dengeleme Sinyali (Rebalancing Signal)
 */
enum class RebalancingSignal(val displayName: String) {
    OVERWEIGHT("Fazla Ağırlık (Kâr Al / Azalt) 🔴"),
    UNDERWEIGHT("Eksik Ağırlık (Ekle / Eşitle) 🟢"),
    BALANCED("Dengeli Konum ⚖️");
}
