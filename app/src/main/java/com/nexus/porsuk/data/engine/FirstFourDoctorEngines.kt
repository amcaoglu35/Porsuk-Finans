package com.nexus.porsuk.data.engine

import com.nexus.porsuk.domain.model.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 1. Portföy Performans Motoru (DoctorPerformanceEngine)
 */
@Singleton
class DoctorPerformanceEngine @Inject constructor() {
    fun calculatePerformance(): DoctorPerformanceData {
        return DoctorPerformanceData(
            totalPortfolioValueUsd = 45000.0,
            totalCostUsd = 36000.0,
            totalProfitLossUsd = 9000.0,
            totalReturnPct = 25.0,
            dailyReturnPct = 1.2,
            weeklyReturnPct = 3.4,
            monthlyReturnPct = 8.5,
            yearlyReturnPct = 32.0
        )
    }
}

/**
 * 2. Portföy Çeşitlendirme Motoru (DoctorDiversificationEngine)
 */
@Singleton
class DoctorDiversificationEngine @Inject constructor() {
    fun calculateDiversification(): DoctorDiversificationData {
        return DoctorDiversificationData(
            sectorBreakdown = mapOf("Havacılık" to 35.0, "Teknoloji" to 25.0, "Bankacılık" to 20.0, "Diğer" to 20.0),
            countryBreakdown = mapOf("Türkiye" to 60.0, "ABD" to 40.0),
            currencyBreakdown = mapOf("TRY" to 60.0, "USD" to 40.0),
            assetClassBreakdown = mapOf("Hisse Senedi" to 70.0, "TEFAS Fonu" to 20.0, "Altın / Döviz" to 10.0)
        )
    }
}

/**
 * 3. Portföy Risk Motoru (DoctorRiskEngine)
 */
@Singleton
class DoctorRiskEngine @Inject constructor() {
    fun calculateRisk(): DoctorRiskData {
        return DoctorRiskData(
            portfolioBeta = 1.08,
            annualVolatilityPct = 22.4,
            concentrationRiskPct = 25.0,
            correlationRatingText = "Dengeli Korelasyon (İyi Dağılım)"
        )
    }
}

/**
 * 4. Portföy Gelir & Temettü Motoru (DoctorIncomeEngine)
 */
@Singleton
class DoctorIncomeEngine @Inject constructor() {
    fun calculateIncome(): DoctorIncomeData {
        return DoctorIncomeData(
            totalAnnualDividendUsd = 1450.0,
            expectedNextMonthDividendUsd = 120.0,
            dividendYieldPct = 3.22,
            monthlyPassiveIncomeUsd = 120.8
        )
    }
}
