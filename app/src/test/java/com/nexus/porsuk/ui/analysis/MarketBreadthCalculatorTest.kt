package com.nexus.porsuk.ui.analysis

import com.google.common.truth.Truth.assertThat
import com.nexus.porsuk.data.local.entity.Company
import org.junit.Test

/**
 * MarketBreadthCalculator Unit Testleri
 *
 * Test Edilen:
 * 1. Boş liste → varsayılan mock verisi döner
 * 2. Yükselen/düşen/değişmeyen hisse sayımı
 * 3. Advance/Decline (A/D) oranı hesabı
 * 4. A/D oranı sıfıra bölme koruması (hiç düşen yok)
 * 5. %3 filtresiyle 52 hafta yüksek/düşük yaklaşımı
 * 6. Toplam hisse sayısı = yükselen + düşen + değişmeyen
 */
class MarketBreadthCalculatorTest {

    @Test
    fun `boş liste ile varsayılan mock veri döner`() {
        val result = MarketBreadthCalculator.calculate(emptyList())
        // Varsayılan mock verileri
        assertThat(result.totalStocks).isEqualTo(100)
        assertThat(result.advancingStocks).isEqualTo(58)
        assertThat(result.decliningStocks).isEqualTo(34)
        assertThat(result.advanceDeclineRatio).isWithin(0.01).of(1.70)
    }

    @Test
    fun `tüm hisseler yükselince advancing count eşit total döner`() {
        val companies = listOf(
            makeCompany("THYAO.IS", changePercent = 2.0),
            makeCompany("ASELS.IS", changePercent = 1.5),
            makeCompany("EREGL.IS", changePercent = 0.5)
        )
        val result = MarketBreadthCalculator.calculate(companies)
        assertThat(result.advancingStocks).isEqualTo(3)
        assertThat(result.decliningStocks).isEqualTo(0)
        assertThat(result.unchangedStocks).isEqualTo(0)
        assertThat(result.totalStocks).isEqualTo(3)
    }

    @Test
    fun `tüm hisseler düşünce declining count eşit total döner`() {
        val companies = listOf(
            makeCompany("THYAO.IS", changePercent = -2.0),
            makeCompany("ASELS.IS", changePercent = -1.5),
            makeCompany("EREGL.IS", changePercent = -3.5)
        )
        val result = MarketBreadthCalculator.calculate(companies)
        assertThat(result.advancingStocks).isEqualTo(0)
        assertThat(result.decliningStocks).isEqualTo(3)
        assertThat(result.unchangedStocks).isEqualTo(0)
    }

    @Test
    fun `hisseler değişmeden kaldığında unchanged count doğru hesaplanır`() {
        val companies = listOf(
            makeCompany("A", changePercent = 1.0),
            makeCompany("B", changePercent = 0.0), // değişmedi
            makeCompany("C", changePercent = 0.0), // değişmedi
            makeCompany("D", changePercent = -1.0)
        )
        val result = MarketBreadthCalculator.calculate(companies)
        assertThat(result.unchangedStocks).isEqualTo(2)
        assertThat(result.advancingStocks).isEqualTo(1)
        assertThat(result.decliningStocks).isEqualTo(1)
    }

    @Test
    fun `hiç düşen hisse yokken A-D oranı advancing count kadar döner (sıfıra bölme koruması)`() {
        val companies = listOf(
            makeCompany("A", changePercent = 2.0),
            makeCompany("B", changePercent = 1.0),
            makeCompany("C", changePercent = 0.5)
        )
        val result = MarketBreadthCalculator.calculate(companies)
        // declining = 0 → adRatio = advancing.toDouble() = 3.0
        assertThat(result.advanceDeclineRatio).isWithin(1e-6).of(3.0)
    }

    @Test
    fun `A-D oranı yükselen hisseleri düşenlere böler`() {
        // 6 yükselen, 3 düşen → A/D = 2.0
        val companies = (1..6).map { makeCompany("UP$it", changePercent = 1.0) } +
                (1..3).map { makeCompany("DOWN$it", changePercent = -1.0) }
        val result = MarketBreadthCalculator.calculate(companies)
        assertThat(result.advanceDeclineRatio).isWithin(1e-6).of(2.0)
    }

    @Test
    fun `52 hafta yüksek yaklaşımı yüzde 3 ve üzeri değişim gösteren hisseleri sayar`() {
        val companies = listOf(
            makeCompany("A", changePercent = 5.0),   // 52w high
            makeCompany("B", changePercent = 3.0),   // tam eşik → 52w high
            makeCompany("C", changePercent = 2.5),   // altında
            makeCompany("D", changePercent = -3.0),  // tam eşik → 52w low
            makeCompany("E", changePercent = -4.0),  // 52w low
            makeCompany("F", changePercent = -2.5)   // altında
        )
        val result = MarketBreadthCalculator.calculate(companies)
        assertThat(result.new52WeekHighs).isEqualTo(2) // A ve B
        assertThat(result.new52WeekLows).isEqualTo(2)  // D ve E
    }

    @Test
    fun `toplam hisse sayısı yükselen artı düşen artı değişmeyen eşit olmalı`() {
        val companies = listOf(
            makeCompany("A", changePercent = 2.0),
            makeCompany("B", changePercent = -1.0),
            makeCompany("C", changePercent = 0.0),
            makeCompany("D", changePercent = 1.5),
            makeCompany("E", changePercent = -2.5)
        )
        val result = MarketBreadthCalculator.calculate(companies)
        val sumCheck = result.advancingStocks + result.decliningStocks + result.unchangedStocks
        assertThat(sumCheck).isEqualTo(result.totalStocks)
    }

    @Test
    fun `MKK yabancı payı her zaman sabit 38_4 döner`() {
        val companies = listOf(makeCompany("TEST", changePercent = 1.0))
        val result = MarketBreadthCalculator.calculate(companies)
        assertThat(result.mkkForeignSharePct).isWithin(1e-6).of(38.4)
    }

    // ─────────────────────────────────────────────────────────────
    // Yardımcı factory metot
    // ─────────────────────────────────────────────────────────────

    private fun makeCompany(symbol: String, changePercent: Double) = Company(
        symbol = symbol,
        name = symbol,
        market = "BIST",
        logoUrl = null,
        logoInitials = symbol.take(2),
        sector = "TEST",
        currentPrice = 100.0,
        changePercent = changePercent
    )
}
