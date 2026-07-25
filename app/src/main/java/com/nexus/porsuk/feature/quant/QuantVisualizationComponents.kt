package com.nexus.porsuk.feature.quant

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.domain.model.*
import kotlin.math.abs

/**
 * 1. Faktör Korelasyon Matrisi Heatmap (Correlation Matrix)
 */
@Composable
fun FactorCorrelationMatrixHeatmap(matrix: FactorCorrelationMatrix) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "🔥 Faktör Çapraz Korelasyon Matrisi (Correlation Matrix)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Faktörler arası multikolinearite ve çeşitlendirme matrisi",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Grid header
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.weight(1.2f)) // Empty top-left corner
                matrix.factorNames.forEach { name ->
                    Box(
                        modifier = Modifier.weight(1.0f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = name.take(3).uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Grid rows
            matrix.matrixGrid.forEachIndexed { rowIdx, rowValues ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.weight(1.2f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = matrix.factorNames[rowIdx],
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }

                    rowValues.forEach { corrVal ->
                        val cellBg = when {
                            corrVal == 1.0 -> MaterialTheme.colorScheme.primaryContainer
                            corrVal > 0.5 -> Color(0xFF1E88E5).copy(alpha = (corrVal * 0.7f).toFloat())
                            corrVal < -0.3 -> Color(0xFFE53935).copy(alpha = (abs(corrVal) * 0.7f).toFloat())
                            else -> MaterialTheme.colorScheme.surface
                        }

                        Box(
                            modifier = Modifier
                                .weight(1.0f)
                                .height(28.dp)
                                .padding(1.dp)
                                .background(cellBg, RoundedCornerShape(4.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = String.format("%.2f", corrVal),
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 2. Faktör Sönümlenme ve Bilgi Katsayısı (Factor Decay & Persistence)
 */
@Composable
fun FactorDecayAndTimelineCard(
    decay: FactorDecayMetrics?,
    persistence: FactorPersistenceMetrics?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "📉 Faktör Sönümlenme (Decay) & IC Kararlılığı",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (decay != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Sinyal Yarılanma Ömrü (Half-Life):", style = MaterialTheme.typography.bodySmall)
                    Text("${decay.halfLifeDays} Gün", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Otokorelasyon (Lag 1 / Lag 21):", style = MaterialTheme.typography.bodySmall)
                    Text("${decay.autocorrelationLag1} / ${decay.autocorrelationLag21}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                }
            }

            if (persistence != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Ortalama IC (Information Coefficient):", style = MaterialTheme.typography.bodySmall)
                    Text("${persistence.meanIC} (Rank IC: ${persistence.rankIC})", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("IC IR (Information Ratio):", style = MaterialTheme.typography.bodySmall)
                    Text("${persistence.icIR} 🟢 (Pozitif IC %${persistence.positiveICRatioPct})", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary)
                }
            }
        }
    }
}

/**
 * 3. Performans Attribütörü (Brinson & Factor Performance Attribution)
 */
@Composable
fun PerformanceAttributionCard(attribution: PerformanceAttributionResult?) {
    if (attribution == null) return

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "🎯 Performans Attribüsyonu (Brinson & Factor)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Toplam Getiri: %${attribution.totalReturnPct}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                Text("Benchmark: %${attribution.benchmarkReturnPct}", style = MaterialTheme.typography.bodySmall)
                Text("Alfa (Excess): +%${attribution.excessReturnPct}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))

            Text("Bileşen Ayrışımı (Attribution Breakdown):", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            Text("• Sektör Alokasyon Etkisi: +%${attribution.allocationEffectPct}", style = MaterialTheme.typography.bodySmall)
            Text("• Hisse Seçim Etkisi: +%${attribution.selectionEffectPct}", style = MaterialTheme.typography.bodySmall)
            Text("• Etkileşim Etkisi (Interaction): +%${attribution.interactionEffectPct}", style = MaterialTheme.typography.bodySmall)

            Spacer(modifier = Modifier.height(6.dp))
            Text("Faktör Katkıları:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            attribution.factorContributionsMap.forEach { (factor, contrib) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("  - $factor", style = MaterialTheme.typography.bodySmall)
                    Text("+%$contrib", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * 4. Walk-Forward Doğrulama Kartı
 */
@Composable
fun WalkForwardValidationCard(wf: WalkForwardResult?) {
    if (wf == null) return

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🚶 Walk Forward Analysis (WFA)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (wf.isStabilityHigh) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
                ) {
                    Text(
                        text = if (wf.isStabilityHigh) "Yüksek Kararlılık 🟢" else "Aşırı Uyum Riski ⚠️",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Pencere Sayısı: ${wf.windowsCount} • In-Sample Sharpe: ${wf.inSampleSharpeRatio} • Out-of-Sample Sharpe: ${wf.outOfSampleSharpeRatio}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Genel Sharpe: ${wf.overallSharpeRatio} • Max Drawdown: -%${wf.maxDrawdownPct}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))

            Text("Pencere Test Adımları:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            wf.windowDetails.forEach { step ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("W${step.windowIndex} (${step.trainPeriod} -> ${step.testPeriod}):", style = MaterialTheme.typography.bodySmall)
                    Text("IS: %${step.trainReturnPct} | OOS: %${step.testReturnPct} (SR: ${step.sharpeRatio})", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
