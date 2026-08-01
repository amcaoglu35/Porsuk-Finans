package com.nexus.porsuk.core.domain.entity

enum class TechnicalSignal(val label: String) {
    STRONG_BUY("GÜÇLÜ AL"),
    BUY("AL"),
    NEUTRAL("NÖTR"),
    SELL("SAT"),
    STRONG_SELL("GÜÇLÜ SAT")
}

data class CompanyStock(
    val id: String,
    val symbol: String,
    val name: String,
    val price: Double,
    val changePercentage: Double,
    val volume: Double, // Milyon TL
    val peRatio: Double, // F/K
    val pbRatio: Double, // PD/DD
    val rsi: Double, // RSI 14
    val technicalSignal: TechnicalSignal,
    val sector: String,
    val marketCap: String,
    val high52w: Double,
    val low52w: Double,
    val supportPrice: Double,
    val resistancePrice: Double,
    val aiRatingScore: Int, // 0 - 100
    val aiSummary: String,
    val roe: Double, // Özsermaye Kârlılığı (%)
    val macdStatus: String = "Pozitif Kesişim",
    val ma20: Double = price * 0.98,
    val ma50: Double = price * 0.94,
    val ma200: Double = price * 0.88,
    val sectorPeAverage: Double = 8.5,
    val sectorPbAverage: Double = 2.1
)
