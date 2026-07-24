package com.nexus.porsuk.domain.model

/**
 * Porsuk Market Engine — Ana Piyasa Fiyatı ve Enstrüman Domain Modeli (MarketQuote)
 *
 * Tüm hisse senetleri, fonlar, döviz, emtia, kripto ve endekslerin fiyat verilerini temsil eder.
 */
data class MarketQuote(
    val symbol: String,
    val name: String,
    val market: String,
    val category: AssetCategory,
    val currency: String,
    val lastPrice: Double,
    val dailyChange: Double,
    val dailyChangePct: Double,
    val open: Double = 0.0,
    val high: Double = 0.0,
    val low: Double = 0.0,
    val previousClose: Double = 0.0,
    val volume: Long = 0,
    val marketCap: Double? = null,
    val lastUpdateTime: Long = System.currentTimeMillis()
)
