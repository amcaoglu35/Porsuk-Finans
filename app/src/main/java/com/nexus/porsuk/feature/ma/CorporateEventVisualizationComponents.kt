package com.nexus.porsuk.feature.ma

import androidx.compose.foundation.background
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
import com.nexus.porsuk.domain.model.*

/**
 * 1. M&A İşlem Adımları Zaman Çizelgesi (Deal Milestone Timeline)
 */
@Composable
fun DealMilestoneTimelineCard(visuals: DealVisuals?) {
    if (visuals == null) return

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "📍 M&A İşlem Kapanış Adımları (Deal Milestone Timeline)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Anlaşma duyurusundan kapanışa kadar geçen süreç",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            visuals.dealTimelineMilestones.forEach { step ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (step.isCompleted) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = if (step.isCompleted) "✓ ${step.stepIndex}" else "${step.stepIndex}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1.0f)) {
                        Text(step.title, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        Text(step.dateLabel, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                    }

                    if (step.isCompleted) {
                        Text("Tamamlandı 🟢", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                    } else {
                        Text("Bekliyor ⏳", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

/**
 * 2. Sektör Değerleme Çarpanları Karşılaştırma Kartı (EV/EBITDA Multiples)
 */
@Composable
fun IndustryComparisonMultiplesCard(visuals: DealVisuals?) {
    if (visuals == null) return

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "📊 Sektörel EV/EBITDA Değerleme Çarpanları",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            visuals.industryComparisonMultiples.forEach { (company, multiple) ->
                val maxMultiple = 10.0
                val progress = (multiple / maxMultiple).coerceIn(0.0, 1.0).toFloat()

                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(company, style = MaterialTheme.typography.bodySmall)
                        Text("${multiple}x", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp),
                        color = if (company.contains("İşlem")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                    )
                }
            }
        }
    }
}

/**
 * 3. Etki & Sinerji Analiz Kartı
 */
@Composable
fun DealImpactAndSynergyCard(
    impact: DealImpactAnalysis?,
    ai: DealAiIntelligence?
) {
    if (impact == null || ai == null) return

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "⚡ M&A Sinerji & Finansal Etki Ayrışımı",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Tahmini Maliyet Sinerjisi:", style = MaterialTheme.typography.bodySmall)
                    Text("$${ai.costSynergyUsd / 1_000_000.0}M", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
                Column {
                    Text("Tahmini Gelir Sinerjisi:", style = MaterialTheme.typography.bodySmall)
                    Text("$${ai.revenueSynergyUsd / 1_000_000.0}M", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFF43A047))
                }
                Column {
                    Text("Ciro Etkisi:", style = MaterialTheme.typography.bodySmall)
                    Text("+%${impact.revenueImpactPct}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider()
            Spacer(modifier = Modifier.height(10.dp))

            Text("Sektörel ve Rekabetçi Etki:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            Text(impact.industryImpactSummary, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(4.dp))
            Text(impact.competitiveImpactSummary, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

/**
 * 4. M&A İşlem Kartı (Deal Card)
 */
@Composable
fun DealCard(deal: DealAnalyticsItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("${deal.acquirerSymbol} ➔ ${deal.targetSymbol}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "${deal.status.iconEmoji} ${deal.status.displayName}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text("${deal.acquirerName} -> ${deal.targetName} satın alım anlaşması.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Hacim: $${deal.dealValueUsd / 1_000_000.0}M", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text("Prim Oranı: +%${deal.premiumPaidPct}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Color(0xFF43A047))
                Text("EV/EBITDA: ${deal.evEbitdaMultiple}x", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
