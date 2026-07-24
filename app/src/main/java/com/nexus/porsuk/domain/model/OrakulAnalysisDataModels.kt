package com.nexus.porsuk.domain.model

/**
 * 1. Şirket Analizi Veri Modeli
 */
data class CompanyAnalysisData(
    val symbol: String,
    val sector: String,
    val industry: String,
    val marketCapCategory: String, // Büyük Ölçekli (Large Cap), Orta Ölçekli (Mid Cap)
    val mainBusinessField: String
)

/**
 * 2. Finansal Analiz Veri Modeli (Infrastructure Stub)
 */
data class FinancialAnalysisData(
    val symbol: String,
    val revenueGrowthPct: Double = 0.0,
    val netProfitMarginPct: Double = 0.0,
    val ebitdaMarginPct: Double = 0.0,
    val debtToEquityRatio: Double = 0.0,
    val freeCashFlowStatus: String = "Pozitif Nakit Akışı"
)

/**
 * 3. Teknik Analiz Veri Modeli (Infrastructure Stub)
 */
data class TechnicalIndicatorResult(
    val indicatorType: TechnicalIndicatorType,
    val valueText: String,
    val signalText: String // AL, SAT, NÖTR
)

data class TechnicalAnalysisData(
    val symbol: String,
    val indicators: List<TechnicalIndicatorResult> = emptyList(),
    val overallTechnicalSignal: String = "Nötr / Olumlu"
)

/**
 * 4. Temettü Analiz Veri Modeli
 */
data class DividendAnalysisData(
    val symbol: String,
    val averageYieldPct: Double = 0.0,
    val payoutRatioPct: Double = 0.0,
    val sustainabilityScore: String = "Yüksek Süreklilik",
    val consecutiveYearsPaid: Int = 5
)

/**
 * 5. Risk Analiz Veri Modeli
 */
data class RiskAnalysisData(
    val symbol: String,
    val beta: Double = 1.0,
    val annualVolatilityPct: Double = 0.0,
    val maxDrawdownPct: Double = 0.0,
    val riskLevelCategory: String = "Orta Risk"
)

/**
 * 6. Kazançlar & Bilanço Analiz Veri Modeli
 */
data class EarningsAnalysisData(
    val symbol: String,
    val latestEps: Double = 0.0,
    val epsSurprisePct: Double = 0.0,
    val revenueSurprisePct: Double = 0.0
)

/**
 * 7. Piyasa & Makro Ortam Analiz Veri Modeli
 */
data class MarketAnalysisData(
    val symbol: String,
    val indexStatus: String = "BIST 100 Pozitif Trendde",
    val sectorPerformance: String = "Sektör Ortalaması Üstünde",
    val fxEffect: String = "Döviz Kuru Etkisi Sınırlı"
)
