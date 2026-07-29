package com.nexus.porsuk.domain.model

/**
 * Porsuk News Intelligence Center — Haber Kategorileri
 */
enum class NewsCategory(val displayName: String) {
    LATEST("Son Haberler"),
    COMPANY("Şirket Haberleri"),
    SECTOR("Sektör Haberleri"),
    ECONOMY("Ekonomi Haberleri"),
    WORLD("Dünya Piyasaları"),
    CRYPTO("Kripto Para"),
    TECH("Teknoloji"),
    AI("Yapay Zeka"),
    ALL("Tüm Haberler");

    companion object {
        fun fromString(name: String?): NewsCategory {
            return entries.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: ALL
        }
    }
}

/**
 * Geleceğe Hazır AI Duygu Analizi Enumu (Sentiment Analysis Stub)
 */
enum class NewsSentiment(val displayName: String, val colorHex: Long) {
    POSITIVE("Pozitif İvme 🚀", 0xFF00C853),
    NEUTRAL("Nötr Etki ⚖️", 0xFF757575),
    NEGATIVE("Riskli / Olumsuz ⚠️", 0xFFD50000)
}
