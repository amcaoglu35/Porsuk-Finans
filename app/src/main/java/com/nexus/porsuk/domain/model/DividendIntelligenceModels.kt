package com.nexus.porsuk.domain.model

/**
 * Temettü Frekansı (Dividend Frequency)
 */
enum class DividendFrequency(val displayName: String) {
    ANNUAL("Yıllık (1x)"),
    SEMI_ANNUAL("Altı Aylık (2x)"),
    QUARTERLY("Üç Aylık (4x)"),
    MONTHLY("Aylık (12x)");
}

/**
 * 5 Temettü Kalite Skoru (Dividend Quality Scores)
 */
data class DividendQualityScores(
    val safetyScore: Int = 92, // 0-100 Dividend Safety
    val growthScore: Int = 88, // 0-100 Dividend Growth
    val consistencyScore: Int = 95, // 0-100 Dividend Consistency
    val sustainabilityScore: Int = 90, // 0-100 Sustainability
    val reliabilityScore: Int = 94 // 0-100 Reliability
)

/**
 * Temettü Hissesi Modeli (DividendStockItem)
 */
data class DividendStockItem(
    val symbol: String,
    val companyName: String,
    val marketType: ScanMarketType,
    val lastPrice: Double,
    val dividendYieldPct: Double,
    val annualDividendUsd: Double,
    val payoutRatioPct: Double,
    val exDividendDateText: String,
    val paymentDateText: String,
    val frequency: DividendFrequency,
    val scores: DividendQualityScores
)

/**
 * Pasif Gelir Tahmin Modeli (IncomeProjection)
 */
data class IncomeProjection(
    val estimatedMonthlyIncomeUsd: Double = 450.0,
    val estimatedAnnualIncomeUsd: Double = 5400.0,
    val averageYieldPct: Double = 5.2,
    val portfolioDividendCount: Int = 12
)

/**
 * Geleceğe Hazır AI Dividend Forecast & Passive Income Planner Stub Modeli
 */
data class AiDividendForecastStub(
    val symbol: String = "FROTO.IS",
    val predictedYieldPct: Double = 6.8,
    val predictedPayoutDateText: String = "15 Nisan 2027",
    val aiPassiveIncomeTip: String = "Orakul AI: Temettü gelirlerinizi otomatik DRIP ile yeniden yatırıma yönlendirerek 5 yılda bileşik büyümenizi %42 artırabilirsiniz."
)
