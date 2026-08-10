package com.nexus.porsuk.feature.security

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexus.porsuk.feature.security.components.AuditLogRow
import com.nexus.porsuk.feature.security.components.IntegrityCheckCard
import com.nexus.porsuk.feature.security.components.SecurityScoreCard

/**
 * Porsuk Finans — Security & Privacy Center Ana Ekranı (SecurityCenterScreen)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityCenterScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: SecurityCenterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Security & Privacy Center", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri"
                        )
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
                // 1. Güvenlik Skor Kartı
                item {
                    SecurityScoreCard(metrics = uiState.metrics)
                }

                // 2. Kimlik Doğrulama & Biyometrik Kilit
                item {
                    Text(
                        text = "Kimlik Doğrulama & Biyometrik Kilit",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
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
                                Text("Biyometrik Kimlik Doğrulama", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text("Parmak izi / Yüz Tanıma", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = uiState.metrics.isBiometricEnabled,
                                onCheckedChange = { viewModel.toggleBiometrics(it) }
                            )
                        }
                    }
                }

                // 3. Uygulama Bütünlüğü
                item {
                    IntegrityCheckCard(metrics = uiState.metrics)
                }

                // 4. KVKK / GDPR Gizlilik Tercihleri
                item {
                    Text(
                        text = "KVKK / GDPR Gizlilik Yönetimi",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
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
                                Text("AI Kişiselleştirme İzni", style = MaterialTheme.typography.bodyMedium)
                                Switch(
                                    checked = uiState.privacyConsents.aiPersonalizationConsent,
                                    onCheckedChange = { viewModel.updatePrivacyConsent(uiState.privacyConsents.copy(aiPersonalizationConsent = it)) }
                                )
                            }
                        }
                    }
                }

                // 5. Güvenlik Denetim Günlüğü (Security Audit Log)
                item {
                    Text(
                        text = "Son Güvenlik Denetim Günlüğü (${uiState.auditLogs.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(uiState.auditLogs) { log ->
                    AuditLogRow(log = log)
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
