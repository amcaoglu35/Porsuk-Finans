package com.nexus.porsuk.feature.cloudsync.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nexus.porsuk.domain.model.CloudBackupPayload
import com.nexus.porsuk.domain.model.CloudProviderType
import com.nexus.porsuk.domain.model.UserDeviceSession

/**
 * Bulut Sağlayıcı Kartı (CloudProviderCard)
 */
@Composable
fun CloudProviderCard(
    provider: CloudProviderType,
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
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "${provider.iconEmoji} ${provider.displayName}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "End-to-End Encrypted Sync • Offline First Architecture",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Badge(containerColor = Color(0xFF00C853)) {
                Text("Bağlı ⚡", modifier = Modifier.padding(4.dp))
            }
        }
    }
}

/**
 * Kayıtlı Cihaz Kartı (RegisteredDeviceCard)
 */
@Composable
fun RegisteredDeviceCard(
    device: UserDeviceSession,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (device.isCurrentDevice) "${device.deviceName} (Bu Cihaz)" else device.deviceName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (device.isCurrentDevice) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${device.deviceType} • Oturum Aktif",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Bulut Yedekleme Kartı (BackupPayloadCard)
 */
@Composable
fun BackupPayloadCard(
    backup: CloudBackupPayload,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
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
            Column {
                Text(backup.backupName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text("${backup.providerType.displayName} • 1.45 MB", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            TextButton(onClick = {}) {
                Text("Geri Yükle")
            }
        }
    }
}
