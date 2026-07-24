package com.nexus.porsuk.feature.api

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexus.porsuk.domain.model.*

/**
 * Porsuk Enterprise API & Automation Platform — Ana Ekran (EnterpriseApiScreen)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnterpriseApiScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: EnterpriseApiViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Enterprise API & Developer Portal", fontWeight = FontWeight.Bold)
                        Text(
                            text = "${uiState.activeProtocol.displayName} • OpenID Connect & OAuth 2.0",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Protokol Seçim Çipleri (LazyRow)
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ApiProtocolType.entries) { protocol ->
                        FilterChip(
                            selected = uiState.activeProtocol == protocol,
                            onClick = { viewModel.selectProtocol(protocol) },
                            label = { Text("${protocol.iconEmoji} ${protocol.displayName}") }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Yeni API Key Üretim Kartı (Create API Key)
                    item {
                        CreateApiKeyCard(
                            keyNameInput = uiState.newKeyNameInput,
                            onInputChange = { viewModel.onNewKeyNameInputChange(it) },
                            onCreateKey = { viewModel.createNewApiKey() }
                        )
                    }

                    // 2. Aktif API Anahtarları (API Keys List)
                    item {
                        Text(
                            text = "Aktif API Anahtarlarınız (API Keys)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(uiState.apiKeys) { apiKey ->
                        ApiKeyCard(
                            apiKey = apiKey,
                            onRevoke = { viewModel.revokeApiKey(apiKey.keyId) }
                        )
                    }

                    // 3. Webhook Abonelikleri (Webhooks)
                    item {
                        Text(
                            text = "Webhook Bildirim Abonelikleri",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(uiState.webhooks) { webhook ->
                        WebhookCard(webhook = webhook)
                    }

                    // 4. Otomasyon Entegrasyonları (Zapier / Make / n8n)
                    item {
                        Text(
                            text = "Otomasyon Entegrasyonları (Zapier / Make / n8n)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(uiState.automations) { auto ->
                        AutomationCard(automation = auto)
                    }

                    // 5. Endpoint Gecikme & Çağrı Metrikleri (Endpoint Stats)
                    item {
                        Text(
                            text = "Endpoint Latency & Çağrı İstatistikleri",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(uiState.endpointStats) { stat ->
                        EndpointStatCard(stat = stat)
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun CreateApiKeyCard(
    keyNameInput: String,
    onInputChange: (String) -> Unit,
    onCreateKey: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("🔑 Yeni API Anahtarı Üret (Bearer Token)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = keyNameInput,
                onValueChange = onInputChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Anahtar Adı (Örn: Production Bot Key)") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onCreateKey,
                modifier = Modifier.align(Alignment.End),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Anahtarı Üret")
            }
        }
    }
}

@Composable
private fun ApiKeyCard(
    apiKey: ApiKeyItem,
    onRevoke: () -> Unit
) {
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
                Text(apiKey.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "${apiKey.rateLimitRpm} RPM",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("Key Prefix: ${apiKey.keyPrefix}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text("İzinler: ${apiKey.scopes.joinToString(", ")}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(modifier = Modifier.height(8.dp))
            TextButton(
                onClick = onRevoke,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Anahtarı İptal Et", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
private fun WebhookCard(webhook: WebhookSubscription) {
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
                Text("🔗 Webhook Endpoint", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(if (webhook.isVerified) "Doğrulandı 🟢" else "Bekliyor 🟡", style = MaterialTheme.typography.labelSmall)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(webhook.targetUrl, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Olaylar: ${webhook.eventTypes.joinToString(", ")}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun AutomationCard(automation: AutomationIntegration) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("${automation.iconEmoji} ${automation.providerName}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text("Aktif İş Akışları: ${automation.activeWorkflowsCount}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(if (automation.isConnected) "Bağlı 🟢" else "Bağlı Değil ⚪", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun EndpointStatCard(stat: EndpointStat) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("${stat.httpMethod} ${stat.endpointPath}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text("Toplam Çağrı: ${stat.totalCallsCount}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Ort. Gecikme: ${stat.avgLatencyMs} ms", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                Text("Hata Oranı: %${stat.errorRatePct}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
