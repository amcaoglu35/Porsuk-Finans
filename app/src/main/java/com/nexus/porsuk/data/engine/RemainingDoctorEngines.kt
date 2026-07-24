package com.nexus.porsuk.data.engine

import com.nexus.porsuk.domain.model.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 5. Portföy Büyüme Motoru (DoctorGrowthEngine)
 */
@Singleton
class DoctorGrowthEngine @Inject constructor() {
    fun calculateGrowth(): DoctorGrowthData {
        return DoctorGrowthData(
            growthPotentialScore = 85,
            valuePotentialScore = 78,
            qualityScore = 88
        )
    }
}

/**
 * 6. Portföy Yeniden Dengeleme Motoru (DoctorRebalancingEngine Stubs)
 */
@Singleton
class DoctorRebalancingEngine @Inject constructor() {
    fun calculateRebalancing(): List<DoctorRebalancingItem> {
        return listOf(
            DoctorRebalancingItem(
                assetSymbol = "THYAO.IS",
                currentWeightPct = 35.0,
                targetWeightPct = 20.0,
                rebalancingSignal = RebalancingSignal.OVERWEIGHT,
                adviceText = "Havacılık ağırlığı hedef %20'nin üstünde. Kar realizasyonu ile oran düşürülebilir."
            ),
            DoctorRebalancingItem(
                assetSymbol = "YAY (TEFAS Fonu)",
                currentWeightPct = 10.0,
                targetWeightPct = 20.0,
                rebalancingSignal = RebalancingSignal.UNDERWEIGHT,
                adviceText = "Teknoloji fonu payı hedef seviyenin altında. Kademeli alım yapılabilir."
            )
        )
    }
}

/**
 * Standart Portföy Doktoru Rapor Oluşturucu Motoru (PortfolioDoctorReportGeneratorEngine)
 */
@Singleton
class PortfolioDoctorReportGeneratorEngine @Inject constructor(
    private val performanceEngine: DoctorPerformanceEngine,
    private val diversificationEngine: DoctorDiversificationEngine,
    private val riskEngine: DoctorRiskEngine,
    private val incomeEngine: DoctorIncomeEngine,
    private val growthEngine: DoctorGrowthEngine,
    private val rebalancingEngine: DoctorRebalancingEngine
) {
    fun generateDoctorReport(): PortfolioDoctorReport {
        val perf = performanceEngine.calculatePerformance()
        val div = diversificationEngine.calculateDiversification()
        val rsk = riskEngine.calculateRisk()
        val inc = incomeEngine.calculateIncome()
        val grw = growthEngine.calculateGrowth()
        val rebal = rebalancingEngine.calculateRebalancing()

        val healthScore = 82 // 0-100 Sağlık Skoru
        val healthLevel = HealthScoreLevel.fromScore(healthScore)

        return PortfolioDoctorReport(
            portfolioName = "Ana Yatırım Portföyüm",
            healthScore = healthScore,
            healthLevel = healthLevel,
            performance = perf,
            diversification = div,
            risk = rsk,
            income = inc,
            growth = grw,
            rebalancingItems = rebal,
            strengths = listOf(
                "Yıllık %${perf.yearlyReturnPct} getiri ile güçlü performans",
                "Düzenli pasif temettü akışı ($${inc.totalAnnualDividendUsd} Yıllık / %${inc.dividendYieldPct} Verim)",
                "Yüksek şirket kalitesi (${grw.qualityScore}/100 Kalite Skoru)"
            ),
            weaknesses = listOf(
                "Havacılık sektöründe %35 yoğunlaşma riski",
                "Gelişmekte olan piyasa ağırlığı yüksek"
            ),
            risks = listOf(
                "Dolar/TL döviz kuru dalgalanması",
                "Portföy Beta değeri 1.08 ile piyasa hareketlerine duyarlı"
            ),
            doctorExecutiveSummary = "Portföyünüz $healthScore/100 Sağlık Skoru ile (${healthLevel.displayName}) konumundadır. Kar marjı ve temettü geliri oldukça tatmin edicidir. Sektörel yoğunlaşmayı azaltmak için THYAO kar satışı ve TEFAS fon ilavesi önerilir."
        )
    }
}
