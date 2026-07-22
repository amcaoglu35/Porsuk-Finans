package com.nexus.porsuk.ui.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.theme.*

data class MetricBenchmark(
    val metricName: String,   // "F/K Oranı", "PD/DD", "ROE Kârlılık", "Net Borç/FAVÖK", "FAVÖK Marjı"
    val stockValueStr: String,
    val sectorAvgStr: String,
    val isBetterThanSector: Boolean
)

data class SectorBenchmark(
    val symbol: String,
    val sectorName: String,
    val score: Int,           // 0-100
    val metrics: List<MetricBenchmark>
)

object SectorBenchmarkCalculator {

    fun calculate(symbol: String = "THYAO", sector: String = "Havacılık & Ulaştırma"): SectorBenchmark {
        val metrics = listOf(
            MetricBenchmark("F/K (P/E) Valüasyonu", "4.8", "7.5", isBetterThanSector = true),
            MetricBenchmark("PD/DD Oranı", "1.1", "1.8", isBetterThanSector = true),
            MetricBenchmark("ROE Özkaynak Kârlılığı", "%32.4", "%22.1", isBetterThanSector = true),
            MetricBenchmark("Net Borç / FAVÖK", "1.4x", "2.1x", isBetterThanSector = true),
            MetricBenchmark("Faaliyet Kâr Marjı", "%24.5", "%18.2", isBetterThanSector = true)
        )

        return SectorBenchmark(
            symbol = symbol,
            sectorName = sector,
            score = 88,
            metrics = metrics
        )
    }
}

@Composable
fun SectorBenchmarkCard(
    symbol: String = "THYAO",
    modifier: Modifier = Modifier
) {
    val benchmark = remember(symbol) { SectorBenchmarkCalculator.calculate(symbol) }

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
                    Text("🌐", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Sektörel Kıyaslama Matrixi ($symbol)",
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
                        "Skor: ${benchmark.score}/100",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = PrimaryTeal,
                        fontFamily = IBMPlexMono
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Metrik", style = MaterialTheme.typography.labelSmall, color = SubText, fontFamily = Manrope)
                    Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                        Text(symbol, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = PrimaryTeal, fontFamily = IBMPlexMono)
                        Text("Sektör Ort.", style = MaterialTheme.typography.labelSmall, color = SubText, fontFamily = IBMPlexMono)
                    }
                }

                benchmark.metrics.forEach { m ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(BackgroundNew)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = m.metricName,
                            style = MaterialTheme.typography.labelSmall,
                            color = InkText,
                            fontFamily = Manrope,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(20.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                m.stockValueStr,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (m.isBetterThanSector) EmeraldNew else RoseNew,
                                fontFamily = IBMPlexMono
                            )
                            Text(
                                m.sectorAvgStr,
                                style = MaterialTheme.typography.labelSmall,
                                color = SubText,
                                fontFamily = IBMPlexMono
                            )
                        }
                    }
                }
            }
        }
    }
}
