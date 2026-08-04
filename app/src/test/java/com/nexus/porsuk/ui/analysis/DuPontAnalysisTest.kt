package com.nexus.porsuk.ui.analysis

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * DuPontAnalysis Unit Testleri
 *
 * DuPont Formülü: ROE = NetProfitMargin × AssetTurnover × FinancialLeverage
 * Formül: (netProfit/revenue) × (revenue/totalAssets) × (totalAssets/equity)
 *
 * Test Edilen:
 * 1. Normal hesaplama doğruluğu
 * 2. Sıfır gelir koruması (margin = 0)
 * 3. Sıfır toplam varlık koruması (turnover = 0)
 * 4. Sıfır özkaynak koruması (leverage = 0)
 * 5. Negatif özkaynak (zarar durumu)
 * 6. Negatif net kar (zarar eden şirket)
 */
class DuPontAnalysisTest {

    @Test
    fun `normal değerlerle ROE doğru hesaplanır`() {
        // netProfit=100, revenue=500, assets=1000, equity=400
        // margin = 100/500 = 0.2 → %20
        // turnover = 500/1000 = 0.5x
        // leverage = 1000/400 = 2.5x
        // ROE = 0.2 × 0.5 × 2.5 × 100 = %25
        val result = DuPontAnalysis.calculate(
            netProfit = 100.0,
            revenue = 500.0,
            totalAssets = 1000.0,
            equity = 400.0
        )
        assertThat(result.netProfitMarginPct).isWithin(1e-6).of(20.0)
        assertThat(result.assetTurnover).isWithin(1e-6).of(0.5)
        assertThat(result.financialLeverage).isWithin(1e-6).of(2.5)
        assertThat(result.calculatedRoePct).isWithin(1e-6).of(25.0)
    }

    @Test
    fun `sıfır gelirde marj sıfır döner ve hata fırlatmaz`() {
        val result = DuPontAnalysis.calculate(
            netProfit = 100.0,
            revenue = 0.0,  // sıfıra bölme riski
            totalAssets = 1000.0,
            equity = 400.0
        )
        assertThat(result.netProfitMarginPct).isEqualTo(0.0)
        // Turnover = 0/1000 = 0
        assertThat(result.assetTurnover).isWithin(1e-6).of(0.0)
    }

    @Test
    fun `sıfır toplam varlıkta varlık devir hızı sıfır döner`() {
        val result = DuPontAnalysis.calculate(
            netProfit = 100.0,
            revenue = 500.0,
            totalAssets = 0.0, // sıfıra bölme riski
            equity = 400.0
        )
        assertThat(result.assetTurnover).isEqualTo(0.0)
    }

    @Test
    fun `sıfır özkaynak durumunda kaldıraç sıfır döner (negatif özkaynak edge case)`() {
        val result = DuPontAnalysis.calculate(
            netProfit = 100.0,
            revenue = 500.0,
            totalAssets = 1000.0,
            equity = 0.0 // sıfıra bölme riski
        )
        assertThat(result.financialLeverage).isEqualTo(0.0)
        assertThat(result.calculatedRoePct).isEqualTo(0.0)
    }

    @Test
    fun `negatif özkaynak durumunda kaldıraç negatif döner`() {
        // Bankrupt şirket: borçları varlığından fazla
        val result = DuPontAnalysis.calculate(
            netProfit = -50.0,
            revenue = 300.0,
            totalAssets = 800.0,
            equity = -200.0 // negatif özkaynak
        )
        assertThat(result.financialLeverage).isLessThan(0.0)
        // negatif marj × pozitif turnover × negatif leverage = pozitif ROE (matematiksel)
        // Bu davranışı belgelemek için test yazılır (bug değil, gerçek matematiksel sonuç)
        assertThat(result.netProfitMarginPct).isLessThan(0.0)
    }

    @Test
    fun `yüksek kaldıraç yüksek ROE üretir`() {
        // Aynı marj ve turnover ama farklı kaldıraç
        val lowLeverage = DuPontAnalysis.calculate(
            netProfit = 50.0, revenue = 500.0, totalAssets = 1000.0, equity = 500.0
        )
        val highLeverage = DuPontAnalysis.calculate(
            netProfit = 50.0, revenue = 500.0, totalAssets = 1000.0, equity = 100.0
        )
        assertThat(highLeverage.calculatedRoePct).isGreaterThan(lowLeverage.calculatedRoePct)
    }

    @Test
    fun `tam zarar eden şirkette net kar marjı negatif döner`() {
        val result = DuPontAnalysis.calculate(
            netProfit = -100.0,
            revenue = 500.0,
            totalAssets = 1000.0,
            equity = 400.0
        )
        assertThat(result.netProfitMarginPct).isLessThan(0.0)
    }

    @Test
    fun `ROE DuPont ıdentity ile tutarlı (netProfit ekseninden doğrulama)`() {
        // ROE (muhasebe tanımı) = netProfit / equity = 200/500 = %40
        // DuPont bileşik formülü de %40 vermelidir
        val result = DuPontAnalysis.calculate(
            netProfit = 200.0,
            revenue = 1000.0,
            totalAssets = 2000.0,
            equity = 500.0
        )
        val expectedRoe = (200.0 / 500.0) * 100.0 // %40
        assertThat(result.calculatedRoePct).isWithin(1e-6).of(expectedRoe)
    }
}
