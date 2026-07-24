package com.nexus.porsuk.feature.security.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nexus.porsuk.domain.model.SecurityAuditLog
import com.nexus.porsuk.domain.model.SecurityScoreMetrics

/**
 * Güvenlik Skoru Kartı (SecurityScoreCard)
 */
@Composable
fun SecurityScoreCard(
    metrics: SecurityScoreMetrics,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Bankacılık Seviyesi Güvenlik",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = metrics.securityLevel.displayName,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(metrics.securityLevel.colorHex)
                )
            }
            Badge(containerColor = Color(0xFF00C853)) {
                Text("${metrics.score} / 100", modifier = Modifier.padding(6.dp), fontWeight = FontWeight.Bold)
            }
        }
    }
}

/**
 * Bütünlük Kontrolü Kartı (IntegrityCheckCard)
 */
@Composable
fun IntegrityCheckCard(
    metrics: SecurityScoreMetrics,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text("Uygulama Bütünlüğü & Tehdit Tespiti", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Rooted Device Check", style = MaterialTheme.typography.bodySmall)
                Text(if (metrics.isRooted) "Tehdit! 🔴" else "Temiz 🟢", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Emulator Detection", style = MaterialTheme.typography.bodySmall)
                Text(if (metrics.isEmulator) "Emülatör 🟡" else "Gerçek Cihaz 🟢", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Debugger Attached", style = MaterialTheme.typography.bodySmall)
                Text(if (metrics.isDebuggerAttached) "Bağlı 🔴" else "Yok 🟢", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            }
        }
    }
}

/**
 * Denetim Günlüğü Satırı (AuditLogRow)
 */
@Composable
fun AuditLogRow(
    log: SecurityAuditLog,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("${log.category.iconEmoji} ${log.title}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(log.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
