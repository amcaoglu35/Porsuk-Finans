package com.nexus.porsuk.feature.transcript

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
 * 1. Konuşmacı Konuşma Süresi Dağılım Kartı (Speaker Talk Time Distribution)
 */
@Composable
fun SpeakerTalkTimeDistributionCard(visuals: TranscriptVisuals?) {
    if (visuals == null) return

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "📊 Konuşmacı Konuşma Süresi Dağılımı (Talk Time Distribution)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "CEO vs CFO vs Analist Soru-Cevap Kelime Oranı",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Stacked distribution bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .background(Color.Gray.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
            ) {
                visuals.speakerTalkTimeDistribution.values.forEachIndexed { idx, pct ->
                    val color = when (idx % 3) {
                        0 -> Color(0xFF1E88E5)
                        1 -> Color(0xFF43A047)
                        else -> Color(0xFFAB47BC)
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight((pct / 100.0).toFloat().coerceAtLeast(0.01f))
                            .background(color)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            visuals.speakerTalkTimeDistribution.forEach { (speaker, pct) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("• $speaker", style = MaterialTheme.typography.bodySmall)
                    Text("%$pct", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * 2. Yönetici Güven Skoru & AI Özet Kartı
 */
@Composable
fun ManagementConfidenceGaugeCard(aiSummary: TranscriptAiSummary?) {
    if (aiSummary == null) return

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🤖 Yönetici Güven Skoru (Management Confidence)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "${aiSummary.managementConfidenceScore} / 100",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Toplantı Duygusu: ${aiSummary.overallSentiment}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(aiSummary.executiveSummary, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(modifier = Modifier.height(10.dp))
            Divider()
            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("🟢 Pozitif Sinyaller (Bullish):", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    aiSummary.bullishSignals.forEach { sig ->
                        Text("• $sig", style = MaterialTheme.typography.bodySmall)
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("🔴 Risk / Negatif Sinyaller:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    aiSummary.bearishSignals.forEach { sig ->
                        Text("• $sig", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

/**
 * 3. Transkript Konuşma Paragrafı (Utterance Card)
 */
@Composable
fun TranscriptUtteranceCard(utterance: TranscriptUtterance) {
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
                    Text(
                        text = utterance.speaker.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = if (utterance.speaker.role.isManagement) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(
                            text = utterance.speaker.role.name,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }

                Text(
                    text = utterance.timeLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = utterance.text,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = "🏷️ ${utterance.topicTag}",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                if (utterance.isGuidanceStatement) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.tertiaryContainer
                    ) {
                        Text(
                            text = "📈 Guidance",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * 4. Soru-Cevap Kartı (Q&A Exchange)
 */
@Composable
fun QnaExchangeCard(qna: QnaExchange) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${qna.category.iconEmoji} ${qna.category.title}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${qna.analystName} (${qna.analystFirm})",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("❓ Soru:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            Text(qna.questionUtterance.text, style = MaterialTheme.typography.bodySmall)

            Spacer(modifier = Modifier.height(8.dp))
            Text("💡 Yönetim Cevabı:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            qna.managementAnswers.forEach { ans ->
                Text(ans.text, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
