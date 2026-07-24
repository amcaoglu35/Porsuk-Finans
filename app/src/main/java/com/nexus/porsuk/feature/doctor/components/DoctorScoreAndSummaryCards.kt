package com.nexus.porsuk.feature.doctor.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nexus.porsuk.domain.model.HealthScoreLevel
import com.nexus.porsuk.domain.model.PortfolioDoctorReport

/**
 * 0-100 Portföy Sağlık Skoru Kartı (HealthScoreGaugeCard)
 */
@Composable
fun HealthScoreGaugeCard(
    report: PortfolioDoctorReport?,
    modifier: Modifier = Modifier
) {
    val score = report?.healthScore ?: 0
    val level = report?.healthLevel ?: HealthScoreLevel.MODERATE

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(level.colorHex).copy(alpha = 0.15f)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "PORTFÖY SAĞLIK SKORU",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Color(level.colorHex)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier.size(120.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { score / 100f },
                    modifier = Modifier.fillMaxSize(),
                    color = Color(level.colorHex),
                    strokeWidth = 10.dp,
                    trackColor = Color(level.colorHex).copy(alpha = 0.15f)
                )
                Text(
                    text = "$score",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(level.colorHex)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(level.colorHex).copy(alpha = 0.25f)
            ) {
                Text(
                    text = level.displayName,
                    color = Color(level.colorHex),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

/**
 * Teşhis Notu ve Özet Kartı (DoctorSummaryReportCard)
 */
@Composable
fun DoctorSummaryReportCard(
    report: PortfolioDoctorReport?,
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
                text = "🩺 Portföy Doktoru Teşhis Notu",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = report?.doctorExecutiveSummary ?: "Teşhis raporu hazırlanıyor...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
