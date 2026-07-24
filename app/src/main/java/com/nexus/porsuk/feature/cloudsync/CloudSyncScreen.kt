package com.nexus.porsuk.feature.cloudsync

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexus.porsuk.domain.model.SyncModuleType
import com.nexus.porsuk.feature.cloudsync.components.BackupPayloadCard
import com.nexus.porsuk.feature.cloudsync.components.CloudProviderCard
import com.nexus.porsuk.feature.cloudsync.components.RegisteredDeviceCard

/**
 * Porsuk Finans — Cloud Sync & Multi-Device Platform Ana Ekranı (CloudSyncScreen)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CloudSyncScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: CloudSyncViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Cloud Sync & Multi-Device", fontWeight = FontWeight.Bold)
                        Text(
                            text = uiState.syncStatus.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(uiState.syncStatus.colorHex)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.triggerSync() }) {
                        Text("🔄")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Bulut Sağlayıcı Kartı
                item {
                    Text(
                        text = "Aktif Bulut Sağlayıcısı Engine",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CloudProviderCard(provider = uiState.activeProvider)
                }

                // 2. Depolama Kullanımı & Veri Politikası
                item {
                    Text(
                        text = "Depolama Kullanımı & Veri Politikaları",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Bulut Kullanımı: 24.5 MB / 10 GB", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            Text("Yerel Önbellek: 12.1 MB", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Sadece Wi-Fi ile Senkronizasyon", style = MaterialTheme.typography.bodySmall)
                                Text("Açık 📶", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }

                // 3. 13 Senkronizasyon Modülü
                item {
                    Text(
                        text = "13 Senkronizasyon Modülü",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            SyncModuleType.entries.take(5).forEach { module ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("${module.iconEmoji} ${module.displayName}", style = MaterialTheme.typography.bodyMedium)
                                    Text("Senkron 🟢", style = MaterialTheme.typography.labelSmall, color = Color(0xFF00C853))
                                }
                            }
                        }
                    }
                }

                // 4. Kayıtlı Cihazlar & Oturumlar
                item {
                    Text(
                        text = "Bağlı Cihazlar (${uiState.devices.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(uiState.devices) { device ->
                    RegisteredDeviceCard(device = device)
                }

                // 5. Bulut Yedeği & Geri Yükleme
                item {
                    Text(
                        text = "Yedekler & Geri Yükleme",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(uiState.backups) { backup ->
                    BackupPayloadCard(backup = backup)
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
