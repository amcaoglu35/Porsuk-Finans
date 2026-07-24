package com.nexus.porsuk.data.engine

import com.nexus.porsuk.domain.model.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 1. Genişletilebilir Temettü Puanlama Motoru (DividendScoringEngine)
 */
@Singleton
class DividendScoringEngine @Inject constructor() {

    fun calculateScores(payoutRatio: Double, growthRatePct: Double): DividendQualityScores {
        val safety = if (payoutRatio < 60.0) 95 else 75
        val growth = if (growthRatePct > 10.0) 90 else 70

        return DividendQualityScores(
            safetyScore = safety,
            growthScore = growth,
            consistencyScore = 95,
            sustainabilityScore = 90,
            reliabilityScore = 92
        )
    }

    fun getSampleDividendStocks(): List<DividendStockItem> {
        return listOf(
            DividendStockItem("FROTO.IS", "Ford Otosan", ScanMarketType.BIST, 980.0, 6.8, 66.6, 42.5, "15 Nisan 2026", "22 Nisan 2026", DividendFrequency.SEMI_ANNUAL, DividendQualityScores(94, 92, 98, 95, 96)),
            DividendStockItem("AKBNK.IS", "Akbank", ScanMarketType.BIST, 58.2, 5.2, 3.02, 32.0, "28 Mart 2026", "04 Nisan 2026", DividendFrequency.ANNUAL, DividendQualityScores(88, 85, 90, 88, 89)),
            DividendStockItem("KO", "Coca-Cola Co.", ScanMarketType.NYSE, 63.5, 3.1, 1.94, 68.0, "12 Haziran 2026", "01 Temmuz 2026", DividendFrequency.QUARTERLY, DividendQualityScores(98, 90, 100, 96, 99)),
            DividendStockItem("O", "Realty Income Corp.", ScanMarketType.NYSE, 52.8, 5.8, 3.07, 78.0, "30 Her Ay", "15 Her Ay", DividendFrequency.MONTHLY, DividendQualityScores(95, 88, 99, 92, 97))
        )
    }
}

/**
 * 2. Pasif Gelir Tahmin Motoru (DividendIncomeProjectionEngine)
 */
@Singleton
class DividendIncomeProjectionEngine @Inject constructor(
    private val scoringEngine: DividendScoringEngine
) {

    fun calculateIncomeProjection(): IncomeProjection {
        val stocks = scoringEngine.getSampleDividendStocks()
        val avgYield = stocks.map { it.dividendYieldPct }.average()

        return IncomeProjection(
            estimatedMonthlyIncomeUsd = 450.0,
            estimatedAnnualIncomeUsd = 5400.0,
            averageYieldPct = avgYield,
            portfolioDividendCount = stocks.size
        )
    }
}
