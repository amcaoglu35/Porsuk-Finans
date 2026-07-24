package com.nexus.porsuk.feature.risk.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nexus.porsuk.domain.model.RiskIntelligenceReport

/**
 * Porsuk Risk Engine — 6 Risk Kategorisi Dağılım Kartı (RiskCategoryGridCard)
 */
@Composable
fun RiskCategoryGridCard(
    report: RiskIntelligenceReport?,
    modifier: Modifier = Modifier
) {
    val marketData = report?.marketRisk
    val liquidityData = report?.liquidityRisk
    val financialData = report?.financialRisk
    val priceData = report?.priceRisk

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "📊 6 Detaylı Risk Metrikleri",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // 1. Piyasa & Likidite Riski
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RiskMetricItem(
                    modifier = Modifier.weight(1f),
                    label = "Beta / Volatilite",
                    value = "Beta: ${marketData?.beta ?: 1.0} (%${marketData?.historicalVolatilityPct ?: 0.0})"
                )
                RiskMetricItem(
                    modifier = Modifier.weight(1f),
                    label = "Likidite Skoru",
                    value = "${liquidityData?.liquidityScore ?: 0}/100 (Yüksek)"
                )
            }

            // 2. Finansal & Fiyat Riski
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RiskMetricItem(
                    modifier = Modifier.weight(1f),
                    label = "Altman Z-Score",
                    value = "${financialData?.altmanZScore ?: 0.0} (Güvenli)"
                )
                RiskMetricItem(
                    modifier = Modifier.weight(1f),
                    label = "Maksimum Düşüş (Max DD)",
                    value = "%${priceData?.maxDrawdownPct ?: 0.0}"
                )
            }
        }
    }
}

@Composable
private fun RiskMetricItem(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
