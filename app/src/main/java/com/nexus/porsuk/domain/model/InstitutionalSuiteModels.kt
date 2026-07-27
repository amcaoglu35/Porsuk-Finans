package com.nexus.porsuk.domain.model

/**
 * Kurumsal Piyasa Özeti (Market Overview)
 */
data class InstitutionalMarketOverview(
    val totalMarketCap: Double,
    val totalVolume24h: Double,
    val topGainers: List<AssetMetric>,
    val topLosers: List<AssetMetric>,
    val mostActive: List<AssetMetric>,
    val marketSentimentScore: Int, // 0-100
    val volatilityIndex: Double, // VIX equivalent
    val advanceDeclineRatio: Double
)

data class AssetMetric(
    val symbol: String,
    val name: String,
    val price: Double,
    val changePct: Double,
    val volume: Double? = null
)

/**
 * Sektör Analitiği (Sector Analytics)
 */
data class SectorAnalytics(
    val sectorName: String,
    val dailyPerf: Double,
    val weeklyPerf: Double,
    val monthlyPerf: Double,
    val avgPeRatio: Double,
    val avgPbRatio: Double,
    val aiStrengthScore: Int,
    val riskScore: Int,
    val momentumScore: Int
)

/**
 * Şirket Analitiği (Institutional Company Analysis)
 */
data class InstitutionalCompanyAnalysis(
    val symbol: String,
    val financialSummary: String,
    val profitabilityScore: Int,
    val growthRate: Double,
    val debtToEquity: Double,
    val freeCashFlow: Double,
    val dividendHistory: List<Double>,
    val valuationMultiples: Map<String, Double>,
    val aiCompanyScore: Int
)

/**
 * Portföy Analitiği (Portfolio Analytics)
 */
data class InstitutionalPortfolioAnalytics(
    val totalReturn: Double,
    val dailyChange: Double,
    val sharpeRatio: Double,
    val sortinoRatio: Double,
    val beta: Double,
    val alpha: Double,
    val maxDrawdown: Double,
    val annualVolatility: Double,
    val diversificationScore: Int
)

/**
 * AI Insights (Kurumsal Yapay Zeka Analizleri)
 */
data class InstitutionalAiInsight(
    val title: String,
    val type: InsightType,
    val description: String,
    val impactedSectors: List<String>,
    val opportunityScore: Int,
    val riskLevel: String,
    val actionSuggestion: String
)

enum class InsightType {
    RISK_DETECTION, OPPORTUNITY, SECTOR_ROTATION, PORTFOLIO_OPTIMIZATION, MACRO_SCENARIO
}
