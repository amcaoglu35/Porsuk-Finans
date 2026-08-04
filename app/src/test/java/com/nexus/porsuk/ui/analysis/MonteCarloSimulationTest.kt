package com.nexus.porsuk.ui.analysis

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import kotlin.math.abs

/**
 * MonteCarloSimulation Unit Testleri
 *
 * Test Edilen:
 * 1. Sıfır/negatif portföy değeri → varsayılan mock sonuç
 * 2. Beklenen değer > başlangıç değeri (pozitif beklenti ile)
 * 3. VaR değerleri her zaman sıfır veya pozitiftir (coerceAtLeast(0) koruması)
 * 4. Simülasyon deterministik: aynı seed ile aynı sonuç
 * 5. Daha yüksek volatilite → daha geniş aralık (expected vs worst arasındaki fark)
 * 6. Hedef erişim olasılığı 0–100 arasında
 */
class MonteCarloSimulationTest {

    @Test
    fun `sıfır portföy değeri ile mock sonuç döner`() {
        val result = MonteCarloSimulation.runSimulation(currentPortfolioValue = 0.0)
        // Sabit mock değerleri
        assertThat(result.initialValue).isEqualTo(100000.0)
        assertThat(result.expectedValue1Year).isEqualTo(135000.0)
        assertThat(result.targetAttainmentProbability).isEqualTo(64.2)
    }

    @Test
    fun `negatif portföy değeri ile mock sonuç döner`() {
        val result = MonteCarloSimulation.runSimulation(currentPortfolioValue = -5000.0)
        assertThat(result.initialValue).isEqualTo(100000.0)
    }

    @Test
    fun `pozitif beklenen getiri ile beklenen değer başlangıcın üstünde olur`() {
        val result = MonteCarloSimulation.runSimulation(
            currentPortfolioValue = 100000.0,
            annualReturnMean = 0.35, // %35 beklenen getiri
            annualVolatility = 0.22
        )
        // Log-normal dağılım ile beklenti = P0 × exp(μ × T) ≈ 135k+
        assertThat(result.expectedValue1Year).isGreaterThan(100000.0)
    }

    @Test
    fun `VaR değerleri her zaman sıfır veya pozitif olur (coerceAtLeast koruması)`() {
        val result = MonteCarloSimulation.runSimulation(
            currentPortfolioValue = 100000.0
        )
        assertThat(result.worstCaseVaR95).isAtLeast(0.0)
        assertThat(result.worstCaseVaR99).isAtLeast(0.0)
    }

    @Test
    fun `95 percentile kayıp 99 percentile kayıptan küçük veya eşit olur`() {
        val result = MonteCarloSimulation.runSimulation(
            currentPortfolioValue = 100000.0
        )
        // VaR99 >= VaR95 (daha ağır kuyruk kaybı)
        assertThat(result.worstCaseVaR99).isAtLeast(result.worstCaseVaR95)
    }

    @Test
    fun `hedef erişim olasılığı 0 ile 100 arasında olur`() {
        val result = MonteCarloSimulation.runSimulation(
            currentPortfolioValue = 100000.0
        )
        assertThat(result.targetAttainmentProbability).isAtLeast(0.0)
        assertThat(result.targetAttainmentProbability).isAtMost(100.0)
    }

    @Test
    fun `başlangıç değeri çıktıya doğru aktarılır`() {
        val initial = 250_000.0
        val result = MonteCarloSimulation.runSimulation(currentPortfolioValue = initial)
        assertThat(result.initialValue).isWithin(1e-6).of(initial)
    }

    @Test
    fun `daha yüksek volatilite daha geniş beklenti-kötü senaryo aralığı üretir`() {
        val lowVol = MonteCarloSimulation.runSimulation(
            currentPortfolioValue = 100_000.0,
            annualReturnMean = 0.20,
            annualVolatility = 0.05 // düşük vol
        )
        val highVol = MonteCarloSimulation.runSimulation(
            currentPortfolioValue = 100_000.0,
            annualReturnMean = 0.20,
            annualVolatility = 0.60 // yüksek vol
        )
        // Yüksek vol → en iyi senaryo daha yüksek VEYA en kötü senaryo daha düşük
        val lowSpread = lowVol.bestCasePercentile95 - lowVol.worstCaseVaR95
        val highSpread = highVol.bestCasePercentile95 - highVol.worstCaseVaR95
        assertThat(highSpread).isGreaterThan(lowSpread)
    }

    @Test
    fun `simülasyon deterministik - aynı parametrelerle aynı sonuç döner`() {
        val params = Triple(100_000.0, 0.30, 0.20)
        val result1 = MonteCarloSimulation.runSimulation(
            currentPortfolioValue = params.first,
            annualReturnMean = params.second,
            annualVolatility = params.third
        )
        val result2 = MonteCarloSimulation.runSimulation(
            currentPortfolioValue = params.first,
            annualReturnMean = params.second,
            annualVolatility = params.third
        )
        // Aynı seed(42) kullanıldığı için her çalıştırmada aynı sonuç beklenir
        assertThat(result1.expectedValue1Year).isWithin(1e-4).of(result2.expectedValue1Year)
        assertThat(result1.worstCaseVaR95).isWithin(1e-4).of(result2.worstCaseVaR95)
    }

    @Test
    fun `sıfır volatilite ile beklenen değer deterministik üstel büyüme ile örtüşür`() {
        // Vol = 0 → tüm simülasyonlar aynı sonucu verir
        // drift = (0.30 - 0) × 1 = 0.30
        // finalValue = P0 × exp(0.30) ≈ 134986
        val result = MonteCarloSimulation.runSimulation(
            currentPortfolioValue = 100_000.0,
            annualReturnMean = 0.30,
            annualVolatility = 0.0 // sıfır oynaklık
        )
        val expectedDeterministic = 100_000.0 * Math.exp(0.30)
        // Tüm simülasyonlar aynı değeri üretmeli
        assertThat(result.expectedValue1Year).isWithin(1.0).of(expectedDeterministic)
    }
}
