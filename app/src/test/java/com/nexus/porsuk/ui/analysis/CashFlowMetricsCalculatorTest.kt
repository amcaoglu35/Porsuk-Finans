package com.nexus.porsuk.ui.analysis

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * CashFlowMetricsCalculator Unit Testleri
 *
 * Test Edilen:
 * 1. FCF = OperatingCF - CapEx hesaplaması
 * 2. FCF Verimi = FCF / MarketCap × 100
 * 3. Kâr Kalite Skoru (qualityRatio × 75, [10..100] aralığında)
 * 4. Kalite derecelendirmesi eşikleri
 * 5. Sıfır market cap (sıfıra bölme)
 * 6. Negatif FCF
 * 7. Sıfır net kâr (qualityRatio = 1.0 varsayılan)
 */
class CashFlowMetricsCalculatorTest {

    @Test
    fun `FCF operatingCF eksi capEx olarak hesaplanır`() {
        val result = CashFlowMetricsCalculator.calculate(
            netProfitMillion = 100.0,
            operatingCashFlowMillion = 150.0,
            capExMillion = 30.0,
            marketCapMillion = 1000.0
        )
        assertThat(result.freeCashFlowMillion).isWithin(1e-6).of(120.0)
    }

    @Test
    fun `FCF verimi doğru yüzde olarak hesaplanır`() {
        // FCF = 120M, MarketCap = 1000M → verim = %12
        val result = CashFlowMetricsCalculator.calculate(
            netProfitMillion = 100.0,
            operatingCashFlowMillion = 150.0,
            capExMillion = 30.0,
            marketCapMillion = 1000.0
        )
        assertThat(result.fcfYieldPct).isWithin(1e-6).of(12.0)
    }

    @Test
    fun `sıfır market cap ile FCF verimi sıfır döner (sıfıra bölme koruması)`() {
        val result = CashFlowMetricsCalculator.calculate(
            netProfitMillion = 100.0,
            operatingCashFlowMillion = 150.0,
            capExMillion = 30.0,
            marketCapMillion = 0.0 // sıfıra bölme riski
        )
        assertThat(result.fcfYieldPct).isEqualTo(0.0)
    }

    @Test
    fun `negatif FCF (capEx operatingCF den büyük) doğru hesaplanır`() {
        val result = CashFlowMetricsCalculator.calculate(
            netProfitMillion = 100.0,
            operatingCashFlowMillion = 50.0,
            capExMillion = 120.0, // CapEx > OperatingCF → negatif FCF
            marketCapMillion = 1000.0
        )
        assertThat(result.freeCashFlowMillion).isLessThan(0.0)
        assertThat(result.freeCashFlowMillion).isWithin(1e-6).of(-70.0)
    }

    @Test
    fun `mükemmel nakit kalitesi - operatingCF net kardan çok büyükse`() {
        // qualityRatio = 200/100 = 2.0 → score = 2×75 = 150 → clamp(10,100) → 100
        val result = CashFlowMetricsCalculator.calculate(
            netProfitMillion = 100.0,
            operatingCashFlowMillion = 200.0, // 2× net kar
            capExMillion = 10.0,
            marketCapMillion = 500.0
        )
        assertThat(result.profitQualityScore).isEqualTo(100)
        assertThat(result.qualityRating).contains("Mükemmel")
    }

    @Test
    fun `orta nakit kalitesi - operatingCF net kara yakın`() {
        // qualityRatio = 80/100 = 0.8 → score = 0.8×75 = 60 → "İyi Nakit Akışı"
        val result = CashFlowMetricsCalculator.calculate(
            netProfitMillion = 100.0,
            operatingCashFlowMillion = 80.0,
            capExMillion = 10.0,
            marketCapMillion = 500.0
        )
        assertThat(result.profitQualityScore).isEqualTo(60)
        assertThat(result.qualityRating).contains("İyi")
    }

    @Test
    fun `düşük nakit kalitesi - operatingCF net karın çok altında`() {
        // qualityRatio = 30/100 = 0.3 → score = 0.3×75 = 22.5 → 22 (int) → "Düşük"
        val result = CashFlowMetricsCalculator.calculate(
            netProfitMillion = 100.0,
            operatingCashFlowMillion = 30.0,
            capExMillion = 5.0,
            marketCapMillion = 500.0
        )
        assertThat(result.profitQualityScore).isLessThan(50)
        assertThat(result.qualityRating).contains("Düşük")
    }

    @Test
    fun `sıfır net karda qualityRatio 1 olarak varsayılır ve hesap yapılır`() {
        // netProfit = 0 → qualityRatio varsayılan 1.0 → score = 75 → "Mükemmel"
        val result = CashFlowMetricsCalculator.calculate(
            netProfitMillion = 0.0, // sıfıra bölme riski
            operatingCashFlowMillion = 100.0,
            capExMillion = 20.0,
            marketCapMillion = 500.0
        )
        assertThat(result.profitQualityScore).isEqualTo(75) // 1.0 × 75 = 75
        assertThat(result.qualityRating).contains("Mükemmel")
    }

    @Test
    fun `skor her zaman 10 ile 100 arasında kalır (clamp doğrulama)`() {
        // Çok düşük qualityRatio → minimum 10
        val veryLow = CashFlowMetricsCalculator.calculate(
            netProfitMillion = 1000.0,
            operatingCashFlowMillion = 0.01, // ratio ≈ 0
            capExMillion = 0.0,
            marketCapMillion = 100.0
        )
        assertThat(veryLow.profitQualityScore).isAtLeast(10)
        assertThat(veryLow.profitQualityScore).isAtMost(100)

        // Çok yüksek qualityRatio → maksimum 100
        val veryHigh = CashFlowMetricsCalculator.calculate(
            netProfitMillion = 1.0,
            operatingCashFlowMillion = 1000.0, // ratio = 1000
            capExMillion = 0.0,
            marketCapMillion = 100.0
        )
        assertThat(veryHigh.profitQualityScore).isAtLeast(10)
        assertThat(veryHigh.profitQualityScore).isAtMost(100)
    }
}
