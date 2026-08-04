package com.nexus.porsuk.ui.analysis

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * RiskMetricsCalculator Unit Testleri
 *
 * Test Edilen:
 * 1. Yetersiz veri (<3 fiyat) — varsayılan nötr sonuç
 * 2. Volatilite (yıllıklaştırılmış)
 * 3. Sharpe Oranı (getiri / risk)
 * 4. Maksimum Drawdown (tepe-dip düşüş)
 * 5. Beta (benchmark ile kovaryans)
 * 6. Risk rating etiketleri
 */
class RiskMetricsCalculatorTest {

    // ─────────────────────────────────────────────────────────────
    // Yetersiz veri (guard clause)
    // ─────────────────────────────────────────────────────────────

    @Test
    fun `tek fiyat ile hesaplamada varsayılan nötr sonuç döner`() {
        val result = RiskMetricsCalculator.calculate(listOf(100.0))
        assertThat(result.sharpeRatio).isEqualTo(0.0)
        assertThat(result.maxDrawdownPct).isEqualTo(0.0)
        assertThat(result.annualizedVolatilityPct).isEqualTo(0.0)
        assertThat(result.beta).isEqualTo(1.0)
        assertThat(result.riskRatingLabel).isEqualTo("Nötr")
    }

    @Test
    fun `iki fiyat ile hesaplamada varsayılan nötr sonuç döner`() {
        val result = RiskMetricsCalculator.calculate(listOf(100.0, 110.0))
        assertThat(result.riskRatingLabel).isEqualTo("Nötr")
    }

    // ─────────────────────────────────────────────────────────────
    // Max Drawdown
    // ─────────────────────────────────────────────────────────────

    @Test
    fun `sürekli yükselen fiyatlarda maksimum drawdown sıfır olur`() {
        val prices = listOf(100.0, 110.0, 120.0, 130.0, 140.0, 150.0)
        val result = RiskMetricsCalculator.calculate(prices)
        assertThat(result.maxDrawdownPct).isWithin(1e-6).of(0.0)
    }

    @Test
    fun `tepe sonrası düşüşte drawdown doğru hesaplanır`() {
        // 100 → 200 (tepe) → 100 → drawdown = (200-100)/200 = %50
        val prices = listOf(100.0, 200.0, 100.0)
        val result = RiskMetricsCalculator.calculate(prices, annualRiskFreeRate = 0.0)
        assertThat(result.maxDrawdownPct).isWithin(0.01).of(50.0)
    }

    @Test
    fun `sürekli düşen fiyatlarda drawdown yüksek olur`() {
        // 100 → 80 → 60 → 40: tepe 100, minimum 40 → MDD = %60
        val prices = listOf(100.0, 80.0, 60.0, 40.0)
        val result = RiskMetricsCalculator.calculate(prices, annualRiskFreeRate = 0.0)
        assertThat(result.maxDrawdownPct).isWithin(0.01).of(60.0)
    }

    // ─────────────────────────────────────────────────────────────
    // Volatilite
    // ─────────────────────────────────────────────────────────────

    @Test
    fun `tüm fiyatlar aynı olduğunda volatilite sıfır olur`() {
        val prices = List(10) { 100.0 }
        val result = RiskMetricsCalculator.calculate(prices, annualRiskFreeRate = 0.0)
        assertThat(result.annualizedVolatilityPct).isWithin(1e-6).of(0.0)
    }

    @Test
    fun `yüksek fiyat dalgalanması yüksek volatilite üretir`() {
        // Düşük volatilite referansı: sabit fiyatlar
        val lowVolPrices = listOf(100.0, 101.0, 100.0, 101.0, 100.0)
        val highVolPrices = listOf(100.0, 150.0, 70.0, 130.0, 80.0)
        val lowVol = RiskMetricsCalculator.calculate(lowVolPrices, annualRiskFreeRate = 0.0)
        val highVol = RiskMetricsCalculator.calculate(highVolPrices, annualRiskFreeRate = 0.0)
        assertThat(highVol.annualizedVolatilityPct).isGreaterThan(lowVol.annualizedVolatilityPct)
    }

    // ─────────────────────────────────────────────────────────────
    // Sharpe Oranı
    // ─────────────────────────────────────────────────────────────

    @Test
    fun `yüksek getirili seri negatif riskfree rate ile sharpe pozitif döner`() {
        // Sürekli yükselen → yüksek getiri → Sharpe > 0 (risk-free = 0 ile)
        val prices = listOf(100.0, 110.0, 121.0, 133.1, 146.4, 161.1)
        val result = RiskMetricsCalculator.calculate(prices, annualRiskFreeRate = 0.0)
        assertThat(result.sharpeRatio).isGreaterThan(0.0)
    }

    @Test
    fun `sıfır volatilitede sharpe hesaplaması sıfır döner ve hata fırlatmaz (sıfıra bölme koruması)`() {
        // Sabit fiyatlar → vol = 0 → Sharpe hesaplanamaz → 0 dönmeli
        val prices = List(10) { 100.0 }
        val result = RiskMetricsCalculator.calculate(prices, annualRiskFreeRate = 0.40)
        assertThat(result.sharpeRatio).isEqualTo(0.0)
    }

    @Test
    fun `negatif getiri ile sharpe negatif döner`() {
        // Sürekli düşen fiyatlar → negatif getiri → Sharpe < 0 (risk-free = 0)
        val prices = listOf(100.0, 90.0, 81.0, 72.9, 65.6)
        val result = RiskMetricsCalculator.calculate(prices, annualRiskFreeRate = 0.0)
        assertThat(result.sharpeRatio).isLessThan(0.0)
    }

    // ─────────────────────────────────────────────────────────────
    // Beta
    // ─────────────────────────────────────────────────────────────

    @Test
    fun `portföy benchmark ile aynı hareket edince beta 1 civarında olur`() {
        val prices = listOf(100.0, 105.0, 102.0, 108.0, 106.0, 110.0)
        val result = RiskMetricsCalculator.calculate(
            portfolioPrices = prices,
            benchmarkPrices = prices // birebir aynı
        )
        // Tam aynı seriler: covariance = variance → beta = 1
        assertThat(result.beta).isWithin(0.01).of(1.0)
    }

    @Test
    fun `benchmark sıfır varyansa sahipken beta 1 varsayılan döner (sıfıra bölme koruması)`() {
        val portfolio = listOf(100.0, 110.0, 120.0, 115.0, 125.0)
        val flatBenchmark = List(5) { 100.0 } // sıfır varyans
        val result = RiskMetricsCalculator.calculate(
            portfolioPrices = portfolio,
            benchmarkPrices = flatBenchmark
        )
        // benchVariance = 0 → beta = 1.0 (korumalı)
        assertThat(result.beta).isEqualTo(1.0)
    }

    // ─────────────────────────────────────────────────────────────
    // Risk rating etiketleri
    // ─────────────────────────────────────────────────────────────

    @Test
    fun `yüksek volatilite serisinde yüksek risk etiketi döner`() {
        // Büyük salınımlar → yüksek vol → Yüksek Risk etiketi beklenir
        val prices = listOf(100.0, 60.0, 140.0, 50.0, 180.0, 40.0, 200.0)
        val result = RiskMetricsCalculator.calculate(prices, benchmarkPrices = emptyList(), annualRiskFreeRate = 0.0)
        assertThat(result.riskRatingLabel).contains("Yüksek Risk")
    }

    @Test
    fun `beta degerlendirme mesajlari dogru atanik gelir`() {
        // Aynı fiyat → beta ~ 1.0 → "Piyasa İle Dengeli"
        val prices = listOf(100.0, 105.0, 102.0, 108.0, 106.0, 110.0)
        val result = RiskMetricsCalculator.calculate(
            portfolioPrices = prices,
            benchmarkPrices = prices
        )
        assertThat(result.betaAssessment).contains("Dengeli")
    }
}
