package com.nexus.porsuk.data.engine

import com.nexus.porsuk.domain.model.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 5. Fiyat & Volatilite Riski Motoru (PriceRiskEngine - VaR & Max Drawdown)
 */
@Singleton
class PriceRiskEngine @Inject constructor() {
    fun calculatePriceRisk(symbol: String): PriceRiskData {
        return PriceRiskData(
            maxDrawdownPct = -18.2,
            valueAtRiskVaR95Pct = -3.4,
            expectedShortfallPct = -4.8,
            dailyPriceSwingPct = 2.8
        )
    }
}

/**
 * 6. Portföy Riski Motoru (PortfolioRiskEngine - Diversification & Concentration)
 */
@Singleton
class PortfolioRiskEngine @Inject constructor() {
    fun calculatePortfolioRisk(symbol: String): PortfolioRiskData {
        return PortfolioRiskData(
            portfolioBeta = 1.05,
            diversificationScore = 82,
            concentrationRiskPct = 18.5,
            topSectorExposureName = "Havacılık (%35.0)"
        )
    }
}

/**
 * Standart Risk Raporu Birleştirici Motoru (RiskReportGeneratorEngine)
 */
@Singleton
class RiskReportGeneratorEngine @Inject constructor(
    private val marketEngine: MarketRiskEngine,
    private val liquidityEngine: LiquidityRiskEngine,
    private val financialEngine: FinancialRiskEngine,
    private val businessEngine: BusinessRiskEngine,
    private val priceEngine: PriceRiskEngine,
    private val portfolioEngine: PortfolioRiskEngine
) {
    fun generateReport(symbol: String): RiskIntelligenceReport {
        val marketData = marketEngine.calculateMarketRisk(symbol)
        val liquidityData = liquidityEngine.calculateLiquidityRisk(symbol)
        val financialData = financialEngine.calculateFinancialRisk(symbol)
        val businessData = businessEngine.calculateBusinessRisk(symbol)
        val priceData = priceEngine.calculatePriceRisk(symbol)
        val portfolioData = portfolioEngine.calculatePortfolioRisk(symbol)

        val overallLevel = when {
            financialData.altmanZScore > 3.0 && marketData.beta < 1.2 -> RiskLevel.LOW
            financialData.altmanZScore > 2.0 -> RiskLevel.MODERATE
            else -> RiskLevel.HIGH
        }

        return RiskIntelligenceReport(
            symbol = symbol,
            overallRiskLevel = overallLevel,
            overallRiskScore = 32, // 0-100 Düşük Risk Skoru
            marketRisk = marketData,
            liquidityRisk = liquidityData,
            financialRisk = financialData,
            businessRisk = businessData,
            priceRisk = priceData,
            portfolioRisk = portfolioData,
            strongPoints = listOf(
                "Altman Z-Score ${financialData.altmanZScore} ile güçlü bilanço ve düşük iflas riski",
                "Yüksek günlük işlem hacmi (${liquidityData.liquidityScore}/100 Likidite Skoru)",
                "Düzeyli borçluluk oranı (Borç/Özkaynak: ${financialData.debtToEquity})"
            ),
            riskFactors = listOf(
                "Dolar/TL kuru ve döviz dalgalanma hassasiyeti",
                "Maksimum düşüş oranı (Max Drawdown: %${priceData.maxDrawdownPct})"
            ),
            protectionRecommendations = listOf(
                "Vadeli işlem piyasasında (VİOP) döviz kuru riskine karşı hedge korunması",
                "Portföy içi sektör yoğunlaşmasını %25 altına düşürecek çeşitlendirme",
                "%95 VaR seviyesi (%${priceData.valueAtRiskVaR95Pct}) doğrultusunda dinamik Stop-Loss kullanımı"
            )
        )
    }
}
