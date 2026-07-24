package com.nexus.porsuk.data.engine

import com.nexus.porsuk.domain.model.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Porsuk Calculation Engine — Strategy Pattern Arayüzü (FinancialCalculatorStrategy)
 *
 * Strategy + Factory Pattern: Tüm finansal hesaplayıcılar bu soyut arayüzü uygular.
 * Yeni hesaplayıcılar var olan kodlar değiştirilmeden tek bir strateji sınıfı eklenerek sisteme dahil edilir.
 */
interface FinancialCalculatorStrategy {
    fun getCategory(): CalculatorCategory
    fun getName(): String
    fun calculate(inputs: Map<String, Double>): CalculationResult
}

/**
 * 1. Bileşik Faiz Hesaplayıcı (CompoundInterestStrategy)
 */
@Singleton
class CompoundInterestStrategy @Inject constructor() : FinancialCalculatorStrategy {
    override fun getCategory() = CalculatorCategory.INVESTMENT
    override fun getName() = "Bileşik Faiz Hesaplayıcı"

    override fun calculate(inputs: Map<String, Double>): CalculationResult {
        val principal = inputs["principal"] ?: 10000.0
        val ratePct = inputs["ratePct"] ?: 10.0
        val years = inputs["years"] ?: 5.0

        val total = principal * (1 + (ratePct / 100.0)).pow(years)
        val interest = total - principal

        return CalculationResult(
            calculatorName = getName(),
            category = getCategory(),
            primaryResultValue = total,
            primaryResultText = "$${String.format("%.2f", total)}",
            secondaryDetails = mapOf(
                "Anapara" to "$${String.format("%.2f", principal)}",
                "Toplam Faiz Getirisi" to "$${String.format("%.2f", interest)}"
            )
        )
    }
}

/**
 * 2. CAGR Yıllık Bileşik Büyüme (CagrStrategy)
 */
@Singleton
class CagrStrategy @Inject constructor() : FinancialCalculatorStrategy {
    override fun getCategory() = CalculatorCategory.INVESTMENT
    override fun getName() = "CAGR Yıllık Bileşik Büyüme Oranı"

    override fun calculate(inputs: Map<String, Double>): CalculationResult {
        val startVal = inputs["startVal"] ?: 10000.0
        val endVal = inputs["endVal"] ?: 25000.0
        val years = inputs["years"] ?: 5.0

        val cagr = ((endVal / startVal).pow(1.0 / years) - 1.0) * 100.0

        return CalculationResult(
            calculatorName = getName(),
            category = getCategory(),
            primaryResultValue = cagr,
            primaryResultText = "%${String.format("%.2f", cagr)}",
            secondaryDetails = mapOf(
                "Başlangıç Değeri" to "$${String.format("%.2f", startVal)}",
                "Bitiş Değeri" to "$${String.format("%.2f", endVal)}"
            )
        )
    }
}

/**
 * 3. Graham Sayısı İçsel Değer (GrahamNumberStrategy)
 */
@Singleton
class GrahamNumberStrategy @Inject constructor() : FinancialCalculatorStrategy {
    override fun getCategory() = CalculatorCategory.VALUATION
    override fun getName() = "Benjamin Graham Sayısı (İçsel Değer)"

    override fun calculate(inputs: Map<String, Double>): CalculationResult {
        val eps = inputs["eps"] ?: 12.5
        val bvps = inputs["bvps"] ?: 45.0

        val grahamVal = sqrt(22.5 * eps * bvps)

        return CalculationResult(
            calculatorName = getName(),
            category = getCategory(),
            primaryResultValue = grahamVal,
            primaryResultText = "$${String.format("%.2f", grahamVal)}",
            secondaryDetails = mapOf(
                "Hisse Başına Kâr (EPS)" to "$eps",
                "Hisse Başına Defter Değeri (BVPS)" to "$bvps"
            )
        )
    }
}

/**
 * Hesaplayıcı Fabrikası (FinancialCalculatorFactory)
 */
@Singleton
class FinancialCalculatorFactory @Inject constructor(
    val compoundInterest: CompoundInterestStrategy,
    val cagr: CagrStrategy,
    val graham: GrahamNumberStrategy
) {
    fun getCalculator(category: CalculatorCategory): FinancialCalculatorStrategy {
        return when (category) {
            CalculatorCategory.VALUATION -> graham
            CalculatorCategory.INVESTMENT -> compoundInterest
            else -> compoundInterest
        }
    }
}
