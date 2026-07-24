package com.nexus.porsuk.domain.model

/**
 * 8 Küresel Bölge ve Varlık Sınıfı (Market Regions)
 */
enum class MarketRegion(val displayName: String, val iconEmoji: String) {
    TURKEY("Türkiye Piyasaları", "🇹🇷"),
    USA("ABD Piyasaları", "🇺🇸"),
    EUROPE("Avrupa Piyasaları", "🇪🇺"),
    ASIA("Asya Piyasaları", "🌏"),
    CRYPTO("Kripto Paralar", "🪙"),
    COMMODITIES("Emtialar", "🛢️"),
    BONDS("Tahvil & Bono", "🏛️"),
    INDICES("Küresel Endeksler", "🌐");
}

/**
 * 5 Borsa Çalışma Durumu (Market State)
 */
enum class MarketState(val displayName: String, val colorHex: Long) {
    OPEN("Piyasa Açık 🟢", 0xFF00C853),
    CLOSED("Piyasa Kapalı 🔴", 0xFFD50000),
    HOLIDAY("Resmi Tatil 🏖️", 0xFF00B0FF),
    PRE_MARKET("Ön Açılış (Pre-Market) 🟡", 0xFFFFB300),
    AFTER_HOURS("Kapanış Sonrası (After-Hours) 🟣", 0xFFAB47BC);
}

/**
 * 10 Sektör (Sector Center)
 */
enum class SectorType(val displayName: String) {
    TECHNOLOGY("Teknoloji"),
    BANKING("Bankacılık"),
    DEFENSE("Savunma Sanayii"),
    ENERGY("Enerji"),
    HEALTHCARE("Sağlık"),
    INDUSTRY("Sanayi"),
    TRANSPORT("Ulaştırma / Havacılık"),
    TELECOM("Telekomünikasyon"),
    REIT("GYO (Gayrimenkul)"),
    INSURANCE("Sigortacılık");
}
