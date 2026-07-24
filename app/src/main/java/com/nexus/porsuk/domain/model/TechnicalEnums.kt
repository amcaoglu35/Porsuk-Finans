package com.nexus.porsuk.domain.model

/**
 * 9 Zaman Dilimi (TimeFrames)
 */
enum class TimeFrame(val code: String, val displayName: String) {
    ONE_MIN("1m", "1 Dakika"),
    FIVE_MIN("5m", "5 Dakika"),
    FIFTEEN_MIN("15m", "15 Dakika"),
    THIRTY_MIN("30m", "30 Dakika"),
    ONE_HOUR("1h", "1 Saat"),
    FOUR_HOUR("4h", "4 Saat"),
    DAILY("1D", "Günlük"),
    WEEKLY("1W", "Haftalık"),
    MONTHLY("1M", "Aylık");
}

/**
 * 5 Seviyeli Sinyal Türü (Technical Signal Type)
 */
enum class TechnicalSignalType(val displayName: String, val colorHex: Long) {
    STRONG_BUY("GÜÇLÜ AL 🚀", 0xFF00C853),
    BUY("AL 🟢", 0xFF2E7D32),
    NEUTRAL("NÖTR ⚖️", 0xFF757575),
    SELL("SAT 🔴", 0xFFD50000),
    STRONG_SELL("GÜÇLÜ SAT ⚠️", 0xFFDD2C00);
}

/**
 * 5 İndikatör Grubu (Indicator Category)
 */
enum class IndicatorCategory(val displayName: String) {
    TREND("Trend İndikatörleri"),
    MOMENTUM("Momentum İndikatörleri"),
    VOLATILITY("Volatilite İndikatörleri"),
    VOLUME("Hacim İndikatörleri"),
    TREND_STRENGTH("Trend Gücü");
}
