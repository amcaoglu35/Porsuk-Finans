package com.nexus.porsuk.domain.model

/**
 * Porsuk Market Engine — Finansal Varlık Kategorileri
 */
enum class AssetCategory(val displayName: String) {
    BIST_STOCK("BIST Hisseleri"),
    NASDAQ_STOCK("NASDAQ Hisseleri"),
    NYSE_STOCK("NYSE Hisseleri"),
    EUROPE_STOCK("Avrupa Hisseleri"),
    ETF("Borsa Yatırım Fonları (ETF)"),
    MUTUAL_FUND("TEFAS Fonları"),
    CURRENCY("Döviz Kurları"),
    COMMODITY("Emtialar (Altın, Gümüş, Petrol vb.)"),
    CRYPTO("Kripto Paralar"),
    INDEX("Borsa Endeksleri");

    companion object {
        fun fromSymbol(symbol: String): AssetCategory {
            val upper = symbol.uppercase()
            return when {
                upper.endsWith(".IS") -> BIST_STOCK
                upper.contains("BTC") || upper.contains("ETH") || upper.contains("USDT") -> CRYPTO
                upper.startsWith("USD") || upper.startsWith("EUR") || upper.startsWith("GBP") -> CURRENCY
                upper == "GAU" || upper == "XAU" || upper == "XAG" || upper == "BRENT" -> COMMODITY
                upper.startsWith("XU") || upper.startsWith("^") -> INDEX
                else -> NASDAQ_STOCK
            }
        }
    }
}

/**
 * Porsuk Market Engine — Veri Sağlayıcı Tipleri (Multi-Provider Architecture)
 */
enum class ProviderType(val providerName: String) {
    FINNHUB("Finnhub API"),
    ALPHA_VANTAGE("Alpha Vantage API"),
    POLYGON("Polygon.io API"),
    TWELVE_DATA("Twelve Data API"),
    PUBLIC_FALLBACK("Public Backup Provider")
}
