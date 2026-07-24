package com.nexus.porsuk.domain.model

/**
 * 11 Hazır Tarama Stratejisi Kategorisi (Preset Scan Categories)
 */
enum class ScanPresetCategory(val displayName: String, val iconEmoji: String) {
    TOP_GAINERS("Günün Kazananları", "🚀"),
    TOP_LOSERS("Günün Kaybedenleri", "📉"),
    HIGH_VOLUME("Olağanüstü Hacim", "🔥"),
    NEW_HIGH_52W("Yeni Zirve (52W High)", "🏔️"),
    NEW_LOW_52W("Yeni Dip (52W Low)", "⚓"),
    STRONG_GROWTH("Güçlü Büyüme Hisseleri", "📈"),
    VALUE_STOCKS("Uygun Değer Hisseleri", "💎"),
    DIVIDEND_STOCKS("Yüksek Temettü Hisseleri", "💰"),
    LOW_RISK("Düşük Riskli Limanlar", "🛡️"),
    HIGH_MOMENTUM("Yüksek Momentum", "⚡"),
    AI_READY_LIST("AI İçin Hazır Liste", "🤖");
}

/**
 * 10 Varlık Piyasası (Scan Market Types)
 */
enum class ScanMarketType(val displayName: String) {
    ALL("Tüm Piyasalar"),
    BIST("BIST Hisseleri"),
    NASDAQ("NASDAQ (ABD)"),
    NYSE("NYSE (ABD)"),
    EUROPE("Avrupa Piyasaları"),
    ETF("ETF Fonları"),
    TEFAS("TEFAS Yatırım Fonları"),
    INDEX("Endeksler"),
    FOREX("Döviz & Kurlar"),
    CRYPTO("Kripto Paralar"),
    COMMODITY("Emtialar");
}
