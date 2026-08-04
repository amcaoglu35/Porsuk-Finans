package com.nexus.porsuk.ui.analysis

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * PiotroskiFScoreCalculator Unit Testleri
 *
 * 9 kriterden puan toplama (0–9):
 *   Kârlılık (0–4):   roaPositive, cfoPositive, roaDeltaPositive, cfoGreaterThanRoa
 *   Kaldıraç (0–3):   leverageDecreased, currentRatioIncreased, noShareDilution
 *   Verimlilik (0–2): grossMarginIncreased, assetTurnoverIncreased
 */
class PiotroskiFScoreCalculatorTest {

    @Test
    fun `tüm kriterler true olunca maksimum 9 puan alınır`() {
        val result = PiotroskiFScoreCalculator.calculate(
            roaPositive = true,
            cfoPositive = true,
            roaDeltaPositive = true,
            cfoGreaterThanRoa = true,
            leverageDecreased = true,
            currentRatioIncreased = true,
            noShareDilution = true,
            grossMarginIncreased = true,
            assetTurnoverIncreased = true
        )
        assertThat(result.totalScore).isEqualTo(9)
        assertThat(result.profitabilityScore).isEqualTo(4)
        assertThat(result.leverageScore).isEqualTo(3)
        assertThat(result.efficiencyScore).isEqualTo(2)
        assertThat(result.rating).contains("MÜKEMMEL")
    }

    @Test
    fun `tüm kriterler false olunca minimum 0 puan alınır`() {
        val result = PiotroskiFScoreCalculator.calculate(
            roaPositive = false,
            cfoPositive = false,
            roaDeltaPositive = false,
            cfoGreaterThanRoa = false,
            leverageDecreased = false,
            currentRatioIncreased = false,
            noShareDilution = false,
            grossMarginIncreased = false,
            assetTurnoverIncreased = false
        )
        assertThat(result.totalScore).isEqualTo(0)
        assertThat(result.profitabilityScore).isEqualTo(0)
        assertThat(result.leverageScore).isEqualTo(0)
        assertThat(result.efficiencyScore).isEqualTo(0)
        assertThat(result.rating).contains("ZAYIF")
    }

    @Test
    fun `4 puan orta rating (dengeli) döner`() {
        val result = PiotroskiFScoreCalculator.calculate(
            roaPositive = true,
            cfoPositive = true,
            roaDeltaPositive = true,
            cfoGreaterThanRoa = true,
            leverageDecreased = false,
            currentRatioIncreased = false,
            noShareDilution = false,
            grossMarginIncreased = false,
            assetTurnoverIncreased = false
        )
        assertThat(result.totalScore).isEqualTo(4)
        assertThat(result.rating).contains("DENGELİ")
    }

    @Test
    fun `3 puan zayıf rating döner`() {
        val result = PiotroskiFScoreCalculator.calculate(
            roaPositive = true,
            cfoPositive = true,
            roaDeltaPositive = true,
            cfoGreaterThanRoa = false, // 3 kriter true
            leverageDecreased = false,
            currentRatioIncreased = false,
            noShareDilution = false,
            grossMarginIncreased = false,
            assetTurnoverIncreased = false
        )
        assertThat(result.totalScore).isEqualTo(3)
        assertThat(result.rating).contains("ZAYIF")
    }

    @Test
    fun `7 puan mükemmel rating döner (alt sınır)`() {
        val result = PiotroskiFScoreCalculator.calculate(
            roaPositive = true,
            cfoPositive = true,
            roaDeltaPositive = true,
            cfoGreaterThanRoa = true,
            leverageDecreased = true,
            currentRatioIncreased = true,
            noShareDilution = true,
            grossMarginIncreased = false,
            assetTurnoverIncreased = false
        )
        assertThat(result.totalScore).isEqualTo(7)
        assertThat(result.rating).contains("MÜKEMMEL")
    }

    @Test
    fun `sadece verimlilik kriterleri 2 puan verir`() {
        val result = PiotroskiFScoreCalculator.calculate(
            roaPositive = false,
            cfoPositive = false,
            roaDeltaPositive = false,
            cfoGreaterThanRoa = false,
            leverageDecreased = false,
            currentRatioIncreased = false,
            noShareDilution = false,
            grossMarginIncreased = true,
            assetTurnoverIncreased = true
        )
        assertThat(result.efficiencyScore).isEqualTo(2)
        assertThat(result.profitabilityScore).isEqualTo(0)
        assertThat(result.leverageScore).isEqualTo(0)
        assertThat(result.totalScore).isEqualTo(2)
    }

    @Test
    fun `alt puan bileşenleri toplamı total score ile eşit olur`() {
        val result = PiotroskiFScoreCalculator.calculate(
            roaPositive = true,
            cfoPositive = false,
            roaDeltaPositive = true,
            cfoGreaterThanRoa = false,
            leverageDecreased = true,
            currentRatioIncreased = false,
            noShareDilution = true,
            grossMarginIncreased = true,
            assetTurnoverIncreased = false
        )
        val sumOfParts = result.profitabilityScore + result.leverageScore + result.efficiencyScore
        assertThat(result.totalScore).isEqualTo(sumOfParts)
    }

    @Test
    fun `varsayılan parametreler maksimum puan verir`() {
        // Tüm default değerler true → 9/9 beklenr
        val result = PiotroskiFScoreCalculator.calculate()
        assertThat(result.totalScore).isEqualTo(9)
    }
}
