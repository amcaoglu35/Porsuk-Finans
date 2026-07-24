package com.nexus.porsuk.feature.risk.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nexus.porsuk.domain.model.RiskIntelligenceReport
import com.nexus.porsuk.domain.model.RiskLevel

/**
 * Genel Risk Seviye Kartı (OverallRiskBadgeCard)
 */
@Composable
fun OverallRiskBadgeCard(
    report: RiskIntelligenceReport?,
    modifier: Modifier = Modifier
) {
    val level = report?.overallRiskLevel ?: RiskLevel.MODERATE

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(level.colorHex).copy(alpha = 0.15f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "PORSUK GENEL RİSK SEVİYESİ",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Color(level.colorHex)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(level.colorHex).copy(alpha = 0.25f)
            ) {
                Text(
                    text = level.displayName,
                    color = Color(level.colorHex),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Risk Skoru: ${report?.overallRiskScore ?: 50} / 100 (Düşük Risk = Güvenli)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * Koruma & Hedging Önerileri Kartı (ProtectionRecommendationsCard)
 */
@Composable
fun ProtectionRecommendationsCard(
    recommendations: List<String>,
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
                text = "🛡️ Koruma & Risk Hedging Önerileri",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            recommendations.forEach { item ->
                Text(
                    text = "• $item",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 3.dp)
                )
            }
        }
    }
}
