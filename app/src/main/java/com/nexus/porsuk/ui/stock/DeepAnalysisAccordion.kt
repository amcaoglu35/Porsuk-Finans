package com.nexus.porsuk.ui.stock

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.analysis.*
import com.nexus.porsuk.ui.theme.*

data class AccordionItemData(
    val id: Int,
    val icon: String,
    val title: String,
    val subtitle: String,
    val badge: String,
    val accentColor: Color
)

@Composable
fun DeepAnalysisAccordion(
    symbol: String,
    modifier: Modifier = Modifier
) {
    val items = remember {
        listOf(
            AccordionItemData(1, "🔍", "DuPont ROE Ayrıştırması", "Net Marj × Devir Hızı × Kaldıraç", "DuPont Model", PrimaryTeal),
            AccordionItemData(2, "📊", "Piotroski F-Score Karnesi", "9 Kriterli Bilanço & Kurumsal Sağlık", "F-Score 0-9", Color(0xFF22B8D9)),
            AccordionItemData(3, "🛡️", "Altman Z & Beneish M-Score", "İflas Riski & Muhasebe Manipülasyonu", "Risk Güvenliği", Color(0xFFE15577)),
            AccordionItemData(4, "⚖️", "Sektörel Kıyaslama Matrisi", "Sektör Ortalamaları vs Hisse Metrikleri", "Kıyaslama", Color(0xFF7C6CF0)),
            AccordionItemData(5, "💵", "Nakit Akışı & FCF Kalitesi", "Serbest Nakit Akışı & Kâr Kalite Skoru", "FCF Yield", Color(0xFFE8A93B)),
            AccordionItemData(6, "📅", "Mevsimsellik & Volatilite", "Tarihsel Ay Performansları & Bilanço Etkisi", "Sezon Trend", PrimaryTeal),
            AccordionItemData(7, "🎯", "Analist Konsensüsü & Hedef Fiyat", "Kurumsal Hedef Fiyatlar & Al/Sat Dağılımı", "Konsensüs", Color(0xFF22B8D9))
        )
    }

    var expandedSet by remember { mutableStateOf(setOf<Int>()) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(PrimaryTeal)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "DERİN ANALİZ MODÜLLERİ",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = InkText,
                    fontFamily = Manrope
                )
            }
            Text(
                text = "${items.size} Modül",
                style = MaterialTheme.typography.labelSmall,
                color = SubText,
                fontFamily = JetBrainsMono
            )
        }

        items.forEach { item ->
            val isExpanded = expandedSet.contains(item.id)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardNew),
                border = BorderStroke(1.dp, if (isExpanded) item.accentColor.copy(alpha = 0.5f) else LineBorder)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                expandedSet = if (isExpanded) {
                                    expandedSet - item.id
                                } else {
                                    expandedSet + item.id
                                }
                            }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(item.accentColor.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(item.icon, fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = InkText,
                                    fontFamily = Manrope
                                )
                                Text(
                                    text = item.subtitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SubText,
                                    fontFamily = Manrope,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(item.accentColor.copy(alpha = 0.1f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = item.badge,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = item.accentColor,
                                    fontFamily = JetBrainsMono
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.ExpandMore else Icons.Default.ChevronRight,
                                contentDescription = if (isExpanded) "Kapat" else "Aç",
                                tint = SubText
                            )
                        }
                    }

                    // Expanded Content
                    AnimatedVisibility(visible = isExpanded) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                        ) {
                            Divider(color = LineBorder.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(12.dp))

                            when (item.id) {
                                1 -> {
                                    val dupont = remember(symbol) { DuPontAnalysis.calculate(15000.0, 120000.0, 180000.0, 65000.0) }
                                    DuPontAnalysisCard(symbol = symbol, duPont = dupont)
                                }
                                2 -> {
                                    val piotroski = remember(symbol) { PiotroskiFScoreCalculator.calculate() }
                                    PiotroskiFScoreCard(symbol = symbol, result = piotroski)
                                }
                                3 -> {
                                    val health = remember(symbol) { BankruptcyAndManipulationDetector.analyze() }
                                    BankruptcyAndManipulationCard(symbol = symbol, flags = health)
                                }
                                4 -> {
                                    val bench = remember(symbol) { SectorBenchmarkCalculator.calculate(symbol) }
                                    SectorBenchmarkMatrixCard(symbol = symbol, benchmark = bench)
                                }
                                5 -> {
                                    val cash = remember(symbol) { CashFlowMetricsCalculator.calculate(12000.0, 14500.0, 3200.0, 85000.0) }
                                    CashFlowMetricsCard(symbol = symbol, summary = cash)
                                }
                                6 -> {
                                    val season = remember(symbol) { SeasonalityCalculator.calculate(symbol) }
                                    SeasonalityCard(symbol = symbol, seasonality = season)
                                }
                                7 -> {
                                    val consensus = remember(symbol) { AnalystConsensusTracker.getConsensus(symbol) }
                                    AnalystConsensusCard(symbol = symbol, consensus = consensus)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BankruptcyAndManipulationCard(symbol: String, flags: FinancialHealthFlags) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            com.nexus.porsuk.ui.common.MetricBox(
                value = String.format(java.util.Locale.US, "%.2f", flags.altmanZScore),
                label = "Altman Z-Score",
                accentColor = Color(flags.altmanZoneColorHex),
                tag = flags.altmanZone,
                tagType = if (flags.altmanZScore >= 2.99) com.nexus.porsuk.ui.common.MetricTagType.GOOD else com.nexus.porsuk.ui.common.MetricTagType.BAD,
                modifier = Modifier.weight(1f)
            )
            com.nexus.porsuk.ui.common.MetricBox(
                value = String.format(java.util.Locale.US, "%.2f", flags.beneishMScore),
                label = "Beneish M-Score",
                accentColor = if (flags.isManipulationRiskHigh) NegatifRed else PrimaryTeal,
                tag = flags.beneishRating,
                tagType = if (flags.isManipulationRiskHigh) com.nexus.porsuk.ui.common.MetricTagType.BAD else com.nexus.porsuk.ui.common.MetricTagType.GOOD,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SectorBenchmarkMatrixCard(symbol: String, benchmark: SectorBenchmark) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        benchmark.metrics.forEach { metric ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(BackgroundNew)
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(metric.metricName, style = MaterialTheme.typography.bodySmall, color = InkText, fontFamily = Manrope, fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("Hisse: ${metric.stockValueStr}", style = MaterialTheme.typography.bodySmall, color = PrimaryTeal, fontFamily = JetBrainsMono, fontWeight = FontWeight.Bold)
                    Text("Sekt: ${metric.sectorAvgStr}", style = MaterialTheme.typography.bodySmall, color = SubText, fontFamily = JetBrainsMono)
                }
            }
        }
    }
}

@Composable
private fun CashFlowMetricsCard(symbol: String, summary: CashFlowAnalysisSummary) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        com.nexus.porsuk.ui.common.MetricBox(
            value = "${String.format(java.util.Locale.US, "%.0f", summary.freeCashFlowMillion)}M TL",
            label = "Serbest Nakit (FCF)",
            accentColor = PrimaryTeal,
            tag = "FCF Yield %${String.format(java.util.Locale.US, "%.1f", summary.fcfYieldPct)}",
            tagType = com.nexus.porsuk.ui.common.MetricTagType.GOOD,
            modifier = Modifier.weight(1f)
        )
        com.nexus.porsuk.ui.common.MetricBox(
            value = "${summary.profitQualityScore}/100",
            label = "Kâr Kalite Skoru",
            accentColor = Color(0xFFE8A93B),
            tag = summary.qualityRating,
            tagType = com.nexus.porsuk.ui.common.MetricTagType.NEUTRAL,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SeasonalityCard(symbol: String, seasonality: SeasonalitySummary) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        com.nexus.porsuk.ui.common.MetricBox(
            value = seasonality.bestMonthName,
            label = "En Güçlü Ay",
            accentColor = PrimaryTeal,
            subValue = "Tarihsel Ort. +%${String.format(java.util.Locale.US, "%.1f", seasonality.bestMonthAvgReturnPct)}",
            modifier = Modifier.weight(1f)
        )
        com.nexus.porsuk.ui.common.MetricBox(
            value = "±%${String.format(java.util.Locale.US, "%.1f", seasonality.postEarningsVolatilityPct)}",
            label = "Bilanço Volatilitesi",
            accentColor = Color(0xFF7C6CF0),
            subValue = "Ocak Etkisi +%${String.format(java.util.Locale.US, "%.1f", seasonality.januaryEffectAvgReturnPct)}",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun AnalystConsensusCard(symbol: String, consensus: AnalystConsensus) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        com.nexus.porsuk.ui.common.MetricBox(
            value = "${String.format(java.util.Locale.US, "%.1f", consensus.targetPriceAverage)} TL",
            label = "Konsensüs Hedef Fiyat",
            accentColor = PrimaryTeal,
            tag = "+%${String.format(java.util.Locale.US, "%.1f", consensus.upsidePotentialPct)} Potansiyel",
            tagType = com.nexus.porsuk.ui.common.MetricTagType.GOOD,
            modifier = Modifier.weight(1f)
        )
        com.nexus.porsuk.ui.common.MetricBox(
            value = consensus.consensusRating,
            label = "Analist Tavsiyesi",
            accentColor = Color(0xFF22B8D9),
            subValue = "${consensus.buyCount} Al / ${consensus.holdCount} Tut / ${consensus.sellCount} Sat",
            modifier = Modifier.weight(1f)
        )
    }
}
