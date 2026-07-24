package com.nexus.porsuk.domain.model

/**
 * 1. Piyasa Riski Veri Modeli
 */
data class MarketRiskData(
    val beta: Double = 1.12,
    val historicalVolatilityPct: Double = 24.5,
    val impliedVolatilityPct: Double = 28.0,
    val downsideVolatilityPct: Double = 18.2,
    val upsideVolatilityPct: Double = 21.4
)

/**
 * 2. Likidite Riski Veri Modeli
 */
data class LiquidityRiskData(
    val avgDailyVolumeUsd: Double = 125000000.0, // 125M USD
    val bidAskSpreadPct: Double = 0.08,
    val liquidityScore: Int = 88, // 0-100
    val freeFloatPct: Double = 50.4
)

/**
 * 3. Finansal Risk Veri Modeli
 */
data class FinancialRiskData(
    val debtToEquity: Double = 0.71,
    val altmanZScore: Double = 3.85, // > 2.9 Güvenli Bölge (Safe Zone)
    val interestCoverageRatio: Double = 8.5,
    val bankruptcyRiskCategory: String = "Düşük İflas Riski (Güvenli)"
)

/**
 * 4. İş & Sektör Riski Veri Modeli
 */
data class BusinessRiskData(
    val sectorRiskLevel: RiskLevel = RiskLevel.MODERATE,
    val countryRiskLevel: RiskLevel = RiskLevel.LOW,
    val currencyRiskLevel: RiskLevel = RiskLevel.MODERATE,
    val regulatoryRiskSummary: String = "Düzenleyici Kurum Kısıtlamaları Düşük"
)

/**
 * 5. Fiyat & Volatilite Riski Veri Modeli
 */
data class PriceRiskData(
    val maxDrawdownPct: Double = -18.2,
    val valueAtRiskVaR95Pct: Double = -3.4, // %95 Güvenle 1 Günlük Maks Düşüş
    val expectedShortfallPct: Double = -4.8,
    val dailyPriceSwingPct: Double = 2.8
)

/**
 * 6. Portföy Riski Veri Modeli
 */
data class PortfolioRiskData(
    val portfolioBeta: Double = 1.05,
    val diversificationScore: Int = 82, // 0-100
    val concentrationRiskPct: Double = 18.5, // En büyük varlığın oranı
    val topSectorExposureName: String = "Havacılık (%35.0)"
)

/**
 * Standart Risk Raporu Modeli (RiskIntelligenceReport)
 */
data class RiskIntelligenceReport(
    val symbol: String,
    val overallRiskLevel: RiskLevel,
    val overallRiskScore: Int, // 0 - 100
    val marketRisk: MarketRiskData,
    val liquidityRisk: LiquidityRiskData,
    val financialRisk: FinancialRiskData,
    val businessRisk: BusinessRiskData,
    val priceRisk: PriceRiskData,
    val portfolioRisk: PortfolioRiskData,
    val strongPoints: List<String>,
    val riskFactors: List<String>,
    val protectionRecommendations: List<String>,
    val generatedAt: Long = System.currentTimeMillis()
)
