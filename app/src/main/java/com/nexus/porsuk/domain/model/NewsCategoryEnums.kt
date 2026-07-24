package com.nexus.porsuk.domain.model

/**
 * Porsuk News Intelligence Center — 13 Haber Kategorisi
 */
enum class NewsCategory(val displayName: String) {
    ALL("Tüm Haberler"),
    COMPANY("Şirket Haberleri"),
    KAP("KAP Bildirimleri"),
    TURKEY_ECONOMY("Türkiye Ekonomisi"),
    WORLD_ECONOMY("Dünya Ekonomisi"),
    FED("FED & Merkez Bankaları"),
    TCMB("TCMB Duyuruları"),
    BIST("BIST Piyasaları"),
    USA_MARKETS("ABD Piyasaları"),
    EUROPE_MARKETS("Avrupa Piyasaları"),
    ETF("ETF Haberleri"),
    TEFAS("TEFAS Fon Duyuruları"),
    CRYPTO("Kripto Para Haberleri"),
    COMMODITY("Emtia & Enerji");

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
