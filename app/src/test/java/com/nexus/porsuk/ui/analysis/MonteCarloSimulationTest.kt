package com.nexus.porsuk.ui.analysis

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.Test

/**
 * MonteCarloSimulation Unit Testleri
 */
class MonteCarloSimulationTest {

    @Test
    fun `sifir portfoy degerinde varsayilan sonuc doner`() = runBlocking {
        val result = MonteCarloSimulation.runSimulation(currentPortfolioValue = 0.0)
        assertThat(result.initialValue).isEqualTo(100000.0)
        assertThat(result.expectedValue1Year).isGreaterThan(100000.0)
    }

    @Test
    fun `negatif portfoy degerinde varsayilan sonuc doner`() = runBlocking {
        val result = MonteCarloSimulation.runSimulation(currentPortfolioValue = -5000.0)
        assertThat(result.initialValue).isEqualTo(100000.0)
    }

    @Test
    fun `pozitif degerde simülasyon mantikli aralikta sonuc uretir`() = runBlocking {
        val initial = 10000.0
        val result = MonteCarloSimulation.runSimulation(
            currentPortfolioValue = initial,
            annualReturnMean = 0.10,
            annualVolatility = 0.15,
            numSimulations = 500
        )
        assertThat(result.initialValue).isEqualTo(initial)
        assertThat(result.expectedValue1Year).isAtLeast(initial * 0.5)
        assertThat(result.expectedValue1Year).isAtMost(initial * 2.0)
    }

    @Test
    fun `yuksek volatilite daha genis bir var araligi uretir`() = runBlocking {
        val initial = 10000.0
        val lowVol = MonteCarloSimulation.runSimulation(
            currentPortfolioValue = initial,
            annualVolatility = 0.05,
            numSimulations = 500
        )
        val highVol = MonteCarloSimulation.runSimulation(
            currentPortfolioValue = initial,
            annualVolatility = 0.40,
            numSimulations = 500
        )
        assertThat(highVol.worstCaseVaR99).isGreaterThan(lowVol.worstCaseVaR99)
    }

    @Test
    fun `beklenen getiri arttikca hedef tutma ihtimali artar`() = runBlocking {
        val initial = 10000.0
        val result1 = MonteCarloSimulation.runSimulation(
            currentPortfolioValue = initial,
            annualReturnMean = 0.05
        )
        val result2 = MonteCarloSimulation.runSimulation(
            currentPortfolioValue = initial,
            annualReturnMean = 0.50
        )
        assertThat(result2.targetAttainmentProbability).isAtLeast(result1.targetAttainmentProbability)
    }

    @Test
    fun `var95 degeri her zaman var99 degerinden kucuk veya esit olmalidir`() = runBlocking {
        val result = MonteCarloSimulation.runSimulation(currentPortfolioValue = 10000.0)
        assertThat(result.worstCaseVaR95).isAtMost(result.worstCaseVaR99)
    }
}
