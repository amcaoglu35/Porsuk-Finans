package com.nexus.porsuk.domain.model

/**
 * 1. Portföy Performans Veri Modeli
 */
data class DoctorPerformanceData(
    val totalPortfolioValueUsd: Double = 45000.0,
    val totalCostUsd: Double = 36000.0,
    val totalProfitLossUsd: Double = 9000.0,
    val totalReturnPct: Double = 25.0,
    val dailyReturnPct: Double = 1.2,
    val weeklyReturnPct: Double = 3.4,
    val monthlyReturnPct: Double = 8.5,
    val yearlyReturnPct: Double = 32.0
)

/**
 * 2. Çeşitlendirme Analizi Veri Modeli
 */
data class DoctorDiversificationData(
    val sectorBreakdown: Map<String, Double> = mapOf("Havacılık" to 35.0, "Teknoloji" to 25.0, "Bankacılık" to 20.0, "Diğer" to 20.0),
    val countryBreakdown: Map<String, Double> = mapOf("Türkiye" to 60.0, "ABD" to 40.0),
    val currencyBreakdown: Map<String, Double> = mapOf("TRY" to 60.0, "USD" to 40.0),
    val assetClassBreakdown: Map<String, Double> = mapOf("Hisse Senedi" to 70.0, "TEFAS Fonu" to 20.0, "Altın / Döviz" to 10.0)
)

/**
 * 3. Risk Analizi Veri Modeli
 */
data class DoctorRiskData(
    val portfolioBeta: Double = 1.08,
    val annualVolatilityPct: Double = 22.4,
    val concentrationRiskPct: Double = 25.0, // En büyük pozisyonun payı
    val correlationRatingText: String = "Orta Düzeyde Korelasyon"
)

/**
 * 4. Gelir & Temettü Analizi Veri Modeli
 */
data class DoctorIncomeData(
    val totalAnnualDividendUsd: Double = 1450.0,
    val expectedNextMonthDividendUsd: Double = 120.0,
    val dividendYieldPct: Double = 3.22,
    val monthlyPassiveIncomeUsd: Double = 120.8
)

/**
 * 5. Büyüme & Değer Potansiyeli Modeli
 */
data class DoctorGrowthData(
    val growthPotentialScore: Int = 85, // 0-100
    val valuePotentialScore: Int = 78,
    val qualityScore: Int = 88
)

/**
 * 6. Yeniden Dengeleme Öğesi (Rebalancing Item Stub)
 */
data class DoctorRebalancingItem(
    val assetSymbol: String,
    val currentWeightPct: Double,
    val targetWeightPct: Double,
    val rebalancingSignal: RebalancingSignal,
    val adviceText: String
)

/**
 * Standart Portföy Doktoru Rapor Modeli (PortfolioDoctorReport)
 */
data class PortfolioDoctorReport(
    val portfolioName: String = "Ana Yatırım Portföyüm",
    val healthScore: Int = 82, // 0-100
    val healthLevel: HealthScoreLevel = HealthScoreLevel.STRONG,
    val performance: DoctorPerformanceData,
    val diversification: DoctorDiversificationData,
    val risk: DoctorRiskData,
    val income: DoctorIncomeData,
    val growth: DoctorGrowthData,
    val rebalancingItems: List<DoctorRebalancingItem>,
    val strengths: List<String>,
    val weaknesses: List<String>,
    val risks: List<String>,
    val doctorExecutiveSummary: String,
    val generatedAt: Long = System.currentTimeMillis()
)
