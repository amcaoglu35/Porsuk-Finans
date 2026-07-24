package com.nexus.porsuk.feature.alerts

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexus.porsuk.domain.model.AlertCategory
import com.nexus.porsuk.feature.alerts.components.*

/**
 * Porsuk Smart Alert Engine — Alarmlar ve Bildirimler Ekranı (AlertsScreen)
 *
 * 9 Farklı alarm kategorisinde fiyat, hacim, haber, temettü ve Orakul AI alarmlarını yöneten ana ekran.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: AlertsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCreateSheet by remember { mutableStateOf(false) }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Smart Alert Engine",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { showCreateSheet = true }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Yeni Alarm Ekle",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateSheet = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Alarm Ekle",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 1. Sekmeler: Aktif Alarmlar vs Bildirim Geçmişi
            TabRow(
                selectedTabIndex = if (uiState.isShowingHistoryTab) 1 else 0,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Tab(
                    selected = !uiState.isShowingHistoryTab,
                    onClick = { viewModel.toggleTab(false) },
                    text = { Text("Aktif Alarmlar (${uiState.alertsList.size})") }
                )
                Tab(
                    selected = uiState.isShowingHistoryTab,
                    onClick = { viewModel.toggleTab(true) },
                    text = { Text("Bildirim Geçmişi (${uiState.notificationHistory.size})") }
                )
            }

            if (!uiState.isShowingHistoryTab) {
                // 2. Alarm Kategori Filtre Çubuğu
                ScrollableTabRow(
                    selectedTabIndex = if (uiState.selectedCategoryFilter == null) 0 else uiState.selectedCategoryFilter!!.ordinal + 1,
                    edgePadding = 16.dp,
                    containerColor = MaterialTheme.colorScheme.surface,
                    divider = {},
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    FilterChip(
                        selected = uiState.selectedCategoryFilter == null,
                        onClick = { viewModel.selectCategoryFilter(null) },
                        label = { Text("Tüm Alarmlar") },
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    AlertCategory.entries.forEach { cat ->
                        FilterChip(
                            selected = cat == uiState.selectedCategoryFilter,
                            onClick = { viewModel.selectCategoryFilter(cat) },
                            label = { Text(cat.displayName) },
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }

                // 3. Geleceğe Hazır Orakul AI Smart Alert Stubs Banner
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Orakul AI",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "🤖 Orakul AI Smart Alert Engine Aktif",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Arka plan WorkManager ile pil dostu 15 dakikalık periyotlarla denetleniyor.",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // 4. Aktif Alarm Listesi
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    } else if (uiState.filteredAlertsList.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Henüz kurulu bir alarm bulunmuyor.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(
                                items = uiState.filteredAlertsList,
                                key = { it.alertId }
                            ) { alert ->
                                AlertItemCard(
                                    alert = alert,
                                    onToggleEnabled = { viewModel.toggleAlertEnabled(alert.alertId, it) },
                                    onToggleMuted = { viewModel.toggleAlertMuted(alert.alertId, it) },
                                    onDeleteClick = { viewModel.deleteAlert(alert.alertId) }
                                )
                            }
                        }
                    }
                }
            } else {
                // 5. Bildirim Geçmişi Sekmesi
                NotificationHistoryList(
                    historyList = uiState.notificationHistory,
                    onClearHistoryClick = { viewModel.clearNotificationHistory() }
                )
            }
        }

        // Alarm Oluşturma BottomSheet
        if (showCreateSheet) {
            CreateAlertBottomSheet(
                onDismissRequest = { showCreateSheet = false },
                onCreateAlert = { symbol, cat, cond, valNum, note ->
                    viewModel.createNewAlert(symbol, cat, cond, valNum, note)
                }
            )
        }
    }
}
