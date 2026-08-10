package com.nexus.porsuk.ui.analysis

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.Test

/**
 * RiskMetricsCalculator Unit Testleri
 */
class RiskMetricsCalculatorTest {

    @Test
    fun `uc fiyat altinda hesaplamada varsayilan notr sonuc doner`() = runBlocking {
        val result = RiskMetricsCalculator.calculate(listOf(100.0))
        assertThat(result.sharpeRatio).isEqualTo(0.0)
        assertThat(result.annualizedVolatilityPct).isEqualTo(0.0)
        assertThat(result.riskRatingLabel).isEqualTo("Nötr")
    }

    @Test
    fun `iki fiyat ile hesaplamada varsayilan notr sonuc doner`() = runBlocking {
        val result = RiskMetricsCalculator.calculate(listOf(100.0, 110.0))
        assertThat(result.sharpeRatio).isEqualTo(0.0)
        assertThat(result.riskRatingLabel).isEqualTo("Nötr")
    }

    @Test
    fun `surekli yukselen fiyatlarda maksimum drawdown sifir olur`() = runBlocking {
        val prices = listOf(100.0, 105.0, 110.0, 120.0, 130.0)
        val result = RiskMetricsCalculator.calculate(prices)
        assertThat(result.maxDrawdownPct).isWithin(1e-6).of(0.0)
    }

    @Test
    fun `tepe sonrasi dususte drawdown dogru hesaplanir`() = runBlocking {
        val prices = listOf(100.0, 200.0, 150.0) // %25 düşüş
        val result = RiskMetricsCalculator.calculate(prices, annualRiskFreeRate = 0.0)
        assertThat(result.maxDrawdownPct).isWithin(1e-6).of(25.0)
    }

    @Test
    fun `surekli dusen fiyatlarda drawdown yuksek olur`() = runBlocking {
        val prices = listOf(100.0, 90.0, 80.0, 70.0, 50.0) // %50 düşüş
        val result = RiskMetricsCalculator.calculate(prices, annualRiskFreeRate = 0.0)
        assertThat(result.maxDrawdownPct).isWithin(1e-6).of(50.0)
    }

    @Test
    fun `tum fiyatlar ayni oldugunda volatilite sifir olur`() = runBlocking {
        val prices = listOf(100.0, 100.0, 100.0, 100.0, 100.0)
        val result = RiskMetricsCalculator.calculate(prices, annualRiskFreeRate = 0.0)
        assertThat(result.annualizedVolatilityPct).isWithin(1e-6).of(0.0)
    }

    @Test
    fun `yuksek fiyat dalgalanmasi yuksek volatilite uretir`() = runBlocking {
        val lowVolPrices = listOf(100.0, 101.0, 100.0, 101.0, 100.0)
        val highVolPrices = listOf(100.0, 150.0, 50.0, 200.0, 10.0)
        val lowVol = RiskMetricsCalculator.calculate(lowVolPrices, annualRiskFreeRate = 0.0)
        val highVol = RiskMetricsCalculator.calculate(highVolPrices, annualRiskFreeRate = 0.0)
        assertThat(highVol.annualizedVolatilityPct).isGreaterThan(lowVol.annualizedVolatilityPct)
    }

    @Test
    fun `risk free orandan yuksek getiri pozitif sharpe uretir`() = runBlocking {
        val prices = listOf(100.0, 150.0, 200.0, 250.0) // Çok yüksek getiri
        val result = RiskMetricsCalculator.calculate(prices, annualRiskFreeRate = 0.0)
        assertThat(result.sharpeRatio).isGreaterThan(0.0)
    }

    @Test
    fun `risk free orandan dusuk getiri negatif sharpe uretir`() = runBlocking {
        val prices = listOf(100.0, 101.0, 102.0, 103.0) // Düşük getiri
        val result = RiskMetricsCalculator.calculate(prices, annualRiskFreeRate = 0.40) // %40 faiz
        assertThat(result.sharpeRatio).isLessThan(0.0)
    }

    @Test
    fun `benchmark ile ayni hareket eden portfoyu betasi 1 olur`() = runBlocking {
        val prices = listOf(100.0, 110.0, 120.0, 130.0)
        val result = RiskMetricsCalculator.calculate(
            portfolioPrices = prices,
            benchmarkPrices = prices,
            annualRiskFreeRate = 0.0
        )
        assertThat(result.beta).isWithin(0.01).of(1.0)
    }

    @Test
    fun `benchmarktan daha agresif hareket eden portfoyun betasi 1 den buyuk olur`() = runBlocking {
        val bench = listOf(100.0, 110.0, 120.0, 130.0)
        val port = listOf(100.0, 120.0, 140.0, 160.0) // İki kat volatil
        val result = RiskMetricsCalculator.calculate(
            portfolioPrices = port,
            benchmarkPrices = bench,
            annualRiskFreeRate = 0.0
        )
        assertThat(result.beta).isGreaterThan(1.0)
    }

    @Test
    fun `beta ve volatiliteye gore risk rating etiketi dogru belirlenir`() = runBlocking {
        val prices = listOf(100.0, 110.0, 100.0, 110.0, 100.0)
        val result = RiskMetricsCalculator.calculate(prices, benchmarkPrices = emptyList(), annualRiskFreeRate = 0.0)
        assertThat(result.betaAssessment).contains("Dengeli")
    }
}
