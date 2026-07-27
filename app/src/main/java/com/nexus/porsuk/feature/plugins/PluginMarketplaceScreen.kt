package com.nexus.porsuk.feature.plugins

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PluginMarketplaceScreen(
    onBack: () -> Unit,
    viewModel: PluginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val tabs = listOf("Yüklü", "Pazaryeri", "API Sağlık", "Güvenlik")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Plugin & API Marketplace",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Manrope
                        )
                        Text(
                            text = "Modüler Veri ve AI Servis Yönetimi",
                            style = MaterialTheme.typography.labelSmall,
                            color = SubText,
                            fontFamily = Manrope
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundNew)
            )
        },
        containerColor = BackgroundNew
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Tab Row
            ScrollableTabRow(
                selectedTabIndex = uiState.selectedTab,
                containerColor = BackgroundNew,
                contentColor = PrimaryTeal,
                edgePadding = 16.dp,
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = uiState.selectedTab == index,
                        onClick = { viewModel.selectTab(index) },
                        text = {
                            Text(
                                text = title,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = Manrope
                            )
                        }
                    )
                }
            }

            Crossfade(targetState = uiState.selectedTab, label = "plugin_tabs") { tabIndex ->
                when (tabIndex) {
                    0 -> InstalledPluginsTab(uiState, viewModel)
                    1 -> MarketplaceTab(uiState, viewModel)
                    2 -> ApiHealthTab(uiState, viewModel)
                    3 -> SecuritySettingsTab()
                }
            }
        }
    }
}

@Composable
fun InstalledPluginsTab(state: PluginUiState, viewModel: PluginViewModel) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(state.installedPlugins) { plugin ->
            PluginManagementCard(
                plugin = plugin,
                onToggle = { viewModel.togglePlugin(plugin.manifest.pluginId, it) },
                onTest = { viewModel.testConnection(plugin.manifest.pluginId) },
                onUninstall = { viewModel.uninstallPlugin(plugin.manifest.pluginId) }
            )
        }
    }
}

@Composable
fun MarketplaceTab(state: PluginUiState, viewModel: PluginViewModel) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(state.availablePlugins) { plugin ->
            MarketplacePluginCard(
                plugin = plugin,
                onInstall = { viewModel.installPlugin(plugin.manifest) }
            )
        }
    }
}

@Composable
fun ApiHealthTab(state: PluginUiState, viewModel: PluginViewModel) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Aktif Servis Sağlık Durumu", fontWeight = FontWeight.Bold, color = PrimaryTeal)
        }
        items(state.installedPlugins) { plugin ->
            ApiHealthCard(pluginId = plugin.manifest.pluginId)
        }
    }
}

@Composable
fun SecuritySettingsTab() {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Orange.copy(alpha = 0.1f))) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, null, tint = Orange)
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "API anahtarları cihazınızda şifreli olarak saklanır ve asla üçüncü taraflarla paylaşılmaz.",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFC2410C)
                    )
                }
            }
        }
    }
}

@Composable
fun PluginManagementCard(
    plugin: PluginItem,
    onToggle: (Boolean) -> Unit,
    onTest: () -> Unit,
    onUninstall: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(plugin.manifest.category.iconEmoji, fontSize = 24.sp)
                    Column {
                        Text(plugin.manifest.pluginName, fontWeight = FontWeight.Bold, color = InkText)
                        Text("v${plugin.manifest.version} • ${plugin.manifest.developerName}", fontSize = 10.sp, color = SubText)
                    }
                }
                Switch(
                    checked = plugin.state == PluginState.ENABLED,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(checkedTrackColor = PrimaryTeal)
                )
            }
            
            Spacer(Modifier.height(12.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onTest,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(Icons.Default.Link, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Bağlantı Testi", fontSize = 11.sp)
                }
                if (!plugin.isBuiltIn) {
                    OutlinedButton(
                        onClick = onUninstall,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NegatifRed)
                    ) {
                        Text("Kaldır", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun MarketplacePluginCard(plugin: PluginItem, onInstall: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f)) {
                Text(plugin.manifest.category.iconEmoji, fontSize = 28.sp)
                Column {
                    Text(plugin.manifest.pluginName, fontWeight = FontWeight.Bold)
                    Text(plugin.manifest.developerName, fontSize = 10.sp, color = SubText)
                }
            }
            Button(
                onClick = onInstall,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)
            ) {
                Text("Yükle", fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun ApiHealthCard(pluginId: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BackgroundNew)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(pluginId.substringAfterLast("."), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                HealthMetric("Gecikme", "24ms", EmeraldNew)
                HealthMetric("Başarı", "99.8%", EmeraldNew)
                HealthMetric("Kullanım", "1.2k", PrimaryTeal)
            }
        }
    }
}

@Composable
fun HealthMetric(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 9.sp, color = SubText)
        Text(value, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = color, fontFamily = IBMPlexMono)
    }
}
