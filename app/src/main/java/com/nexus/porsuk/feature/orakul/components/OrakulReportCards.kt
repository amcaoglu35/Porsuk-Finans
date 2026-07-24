package com.nexus.porsuk.feature.orakul.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nexus.porsuk.domain.model.OrakulAnalysisReport

/**
 * Porsuk Orakul Core — Genel Durum ve Özet Kartı (AnalysisReportCard)
 */
@Composable
fun AnalysisReportCard(
    report: OrakulAnalysisReport?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "🤖 Orakul Core Analiz Raporu",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = report?.executiveSummary ?: "Analiz raporu oluşturuluyor...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * Standart Rapor — Güçlü Yönler, Zayıf Yönler, Riskler & Fırsatlar Kartı
 */
@Composable
fun StrengthsWeaknessesCard(
    report: OrakulAnalysisReport?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Güçlü Yönler (Strengths)
        ReportSectionCard("💪 Güçlü Yönler", report?.strengths ?: emptyList(), MaterialTheme.colorScheme.primary)

        // Zayıf Yönler (Weaknesses)
        ReportSectionCard("⚠️ Zayıf Yönler", report?.weaknesses ?: emptyList(), MaterialTheme.colorScheme.error)

        // Riskler (Risks)
        ReportSectionCard("🛡️ Riskler", report?.risks ?: emptyList(), MaterialTheme.colorScheme.tertiary)

        // Fırsatlar (Opportunities)
        ReportSectionCard("🚀 Fırsatlar", report?.opportunities ?: emptyList(), MaterialTheme.colorScheme.secondary)

        // İzlenecek Noktalar (Key Watchpoints)
        ReportSectionCard("📌 İzlenecek Kritik Noktalar", report?.keyWatchpoints ?: emptyList(), MaterialTheme.colorScheme.outline)
    }
}

@Composable
private fun ReportSectionCard(title: String, items: List<String>, accentColor: androidx.compose.ui.graphics.Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
            Spacer(modifier = Modifier.height(6.dp))
            items.forEach { item ->
                Text(
                    text = "• $item",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
    }
}
