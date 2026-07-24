package com.nexus.porsuk.feature.technical.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nexus.porsuk.domain.model.SupportResistanceLevel
import com.nexus.porsuk.domain.model.TechnicalAnalysisReport
import com.nexus.porsuk.domain.model.TechnicalSignalType

/**
 * Genel Sinyal Kartı (TechnicalSummaryCard)
 */
@Composable
fun TechnicalSummaryCard(
    report: TechnicalAnalysisReport?,
    modifier: Modifier = Modifier
) {
    val overallSignal = report?.overallSignal ?: TechnicalSignalType.NEUTRAL

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "GENEL TEKNİK SİNYAL (${report?.timeFrame?.displayName ?: "Günlük"})",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(overallSignal.colorHex).copy(alpha = 0.2f)
            ) {
                Text(
                    text = overallSignal.displayName,
                    color = Color(overallSignal.colorHex),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = report?.trendSummary ?: "Trend analizi yükleniyor...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Destek ve Direnç Seviyeleri Kartı (SupportResistanceLevelsCard)
 */
@Composable
fun SupportResistanceLevelsCard(
    supports: List<SupportResistanceLevel>,
    resistances: List<SupportResistanceLevel>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "🎯 Pivot & Fibonacci Destek / Dirençler",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Destekler Kolonu
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Destek Seviyeleri (Supports)",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00C853)
                    )
                    supports.forEach { lvl ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(lvl.levelName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${lvl.price} TL", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Dirençler Kolonu
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Direnç Seviyeleri (Resistances)",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD50000)
                    )
                    resistances.forEach { lvl ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(lvl.levelName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${lvl.price} TL", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
