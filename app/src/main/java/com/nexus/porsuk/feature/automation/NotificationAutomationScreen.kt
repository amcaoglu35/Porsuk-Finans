package com.nexus.porsuk.feature.automation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexus.porsuk.feature.automation.components.*

/**
 * Porsuk Notification & Automation Center — Ana Ekran (NotificationAutomationScreen)
 *
 * Event-Driven bildirimleri, IF/AND/OR otomasyon kurallarını ve otomatik bülten iş akışlarını sunar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationAutomationScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: AutomationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Notification & Automation Center",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri"
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // 1. Canlı Olay Bildirimleri
                item {
                    Text("🔔 Son Bildirimler & Alarmlar", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }

                if (uiState.isLoading) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                } else {
                    items(uiState.notifications) { notif ->
                        NotificationItemCard(item = notif)
                    }
                }

                // 2. Aktif Otomasyon Kuralları (Rule Engine)
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("⚡ Aktif Otomasyon Kuralları (IF / AND / OR)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }

                items(uiState.rules) { rule ->
                    AutomationRuleRowCard(rule = rule)
                }

                // 3. Otomatik İş Akışları (Morning Brief & Digest Workflows)
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("🤖 Otomatik Bülten İş Akışları", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }

                items(uiState.workflows) { wf ->
                    WorkflowTemplateCard(workflow = wf)
                }

                // 4. Geleceğe Hazır AI Smart Digest Stub Kartı
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "🧠 Orakul AI Akıllı Günlük Özet (Smart Digest)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Orakul AI: Bugün okumadığınız 5 kritik bildirimi önceliklendirip 1 dakikalık özet rapora dönüştürdü.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}
