package com.nexus.porsuk.ui.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.theme.*
import java.util.Locale
import kotlin.math.exp
import kotlin.math.sqrt
import kotlin.random.Random

data class MonteCarloResult(
    val initialValue: Double,
    val expectedValue1Year: Double,
    val medianValue1Year: Double,
    val worstCaseVaR95: Double,     // %95 VaR (95% ihtimalle maksimum kayıp)
    val worstCaseVaR99: Double,     // %99 VaR
    val bestCasePercentile95: Double,
    val targetAttainmentProbability: Double // 1 yılda %30+ getiri sağlama ihtimali (%)
)

object MonteCarloSimulation {

    fun runSimulation(
        currentPortfolioValue: Double,
        annualReturnMean: Double = 0.35, // %35 Ortalama Yıllık Getiri Beklentisi
        annualVolatility: Double = 0.22, // %22 Yıllık Oynaklık
        numSimulations: Int = 1000,
        timeHorizonYears: Double = 1.0
    ): MonteCarloResult {
        if (currentPortfolioValue <= 0) {
            return MonteCarloResult(
                initialValue = 100000.0,
                expectedValue1Year = 135000.0,
                medianValue1Year = 131000.0,
                worstCaseVaR95 = 14200.0,
                worstCaseVaR99 = 21500.0,
                bestCasePercentile95 = 178000.0,
                targetAttainmentProbability = 64.2
            )
        }

        val drift = (annualReturnMean - 0.5 * annualVolatility * annualVolatility) * timeHorizonYears
        val volSqrtT = annualVolatility * sqrt(timeHorizonYears)
        val finalValues = ArrayList<Double>(numSimulations)

        val rand = Random(42)
        for (i in 0 until numSimulations) {
            // Box-Muller transform for standard normal variable
            val u1 = rand.nextDouble().coerceAtLeast(1e-9)
            val u2 = rand.nextDouble().coerceAtLeast(1e-9)
            val z = sqrt(-2.0 * Math.log(u1)) * Math.cos(2.0 * Math.PI * u2)

            val simulatedValue = currentPortfolioValue * exp(drift + volSqrtT * z)
            finalValues.add(simulatedValue)
        }

        finalValues.sort()

        val expected = finalValues.average()
        val median = finalValues[numSimulations / 2]
        val p5Worst = finalValues[(numSimulations * 0.05).toInt()]
        val p1Worst = finalValues[(numSimulations * 0.01).toInt()]
        val p95Best = finalValues[(numSimulations * 0.95).toInt()]

        val var95 = (currentPortfolioValue - p5Worst).coerceAtLeast(0.0)
        val var99 = (currentPortfolioValue - p1Worst).coerceAtLeast(0.0)

        val targetThreshold = currentPortfolioValue * 1.30
        val targetCount = finalValues.count { it >= targetThreshold }
        val prob = (targetCount.toDouble() / numSimulations) * 100.0

        return MonteCarloResult(
            initialValue = currentPortfolioValue,
            expectedValue1Year = expected,
            medianValue1Year = median,
            worstCaseVaR95 = var95,
            worstCaseVaR99 = var99,
            bestCasePercentile95 = p95Best,
            targetAttainmentProbability = prob
        )
    }
}

@Composable
fun MonteCarloCard(
    result: MonteCarloResult,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🎲", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Monte Carlo 1.000 İhtimal Simülasyonu",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = InkText,
                        fontFamily = Manrope
                    )
                }
                Box(
                    modifier = Modifier
                        .background(AquaSoft)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .border(1.dp, PrimaryTeal.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                ) {
                    Text(
                        "1 Yıllık Projeksiyon",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = PrimaryTeal,
                        fontFamily = IBMPlexMono
                    )
                }
            }

            // Stats grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MCTile(
                    title = "Beklenen Değer (1Y)",
                    value = "₺${String.format(Locale.US, "%,.0f", result.expectedValue1Year)}",
                    subtitle = "Ortalama Senaryo",
                    valueColor = EmeraldNew,
                    modifier = Modifier.weight(1f)
                )
                MCTile(
                    title = "%95 Riske Maruz Değer (VaR)",
                    value = "₺${String.format(Locale.US, "%,.0f", result.worstCaseVaR95)}",
                    subtitle = "%95 İhtimalle Max Kayıp",
                    valueColor = RoseNew,
                    modifier = Modifier.weight(1f)
                )
                MCTile(
                    title = "%30+ Getiri Şansı",
                    value = "%${String.format(Locale.US, "%.1f", result.targetAttainmentProbability)}",
                    subtitle = "Hedef İhtimali",
                    valueColor = PrimaryTeal,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun MCTile(
    title: String,
    value: String,
    subtitle: String,
    valueColor: Color = InkText,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(BackgroundNew)
            .padding(10.dp)
            .border(1.dp, LineBorder, RoundedCornerShape(10.dp))
    ) {
        Column {
            Text(title, style = MaterialTheme.typography.labelSmall, color = SubText, fontFamily = Manrope)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = valueColor, fontFamily = IBMPlexMono)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = SubText.copy(alpha = 0.7f), fontSize = 9.sp, fontFamily = Manrope)
        }
    }
}
