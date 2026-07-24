package com.nexus.porsuk.feature.plugins

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
 * Porsuk Plugin & Extension SDK Platform — Ana Ekran (PluginManagerScreen)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PluginManagerScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: PluginManagerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Plugin & Extension SDK Platform", fontWeight = FontWeight.Bold)
                        Text(
                            text = "SDK ${uiState.sdkVersion} • ${uiState.installedPlugins.size} Yüklü Eklenti",
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
                // Kategori Filtresi (LazyRow)
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = uiState.selectedCategoryFilter == null,
                            onClick = { viewModel.selectCategoryFilter(null) },
                            label = { Text("Tüm Kategoriler") }
                        )
                    }
                    items(PluginCategory.entries) { cat ->
                        FilterChip(
                            selected = uiState.selectedCategoryFilter == cat,
                            onClick = { viewModel.selectCategoryFilter(cat) },
                            label = { Text("${cat.iconEmoji} ${cat.displayName}") }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Yüklü Eklentiler Bölümü
                    item {
                        Text(
                            text = "Yüklü & Etkin Eklentiler",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    val filteredInstalled = uiState.installedPlugins.filter {
                        uiState.selectedCategoryFilter == null || it.manifest.category == uiState.selectedCategoryFilter
                    }

                    items(filteredInstalled) { plugin ->
                        InstalledPluginCard(
                            plugin = plugin,
                            metrics = uiState.activeSandboxMetrics[plugin.manifest.pluginId],
                            onToggle = { enable -> viewModel.togglePluginState(plugin.manifest.pluginId, enable) },
                            onUninstall = { viewModel.uninstallPlugin(plugin.manifest.pluginId) },
                            onRunSandbox = { viewModel.runPluginSandboxTest(plugin.manifest.pluginId) }
                        )
                    }

                    // 2. Kullanılabilir / Önerilen Eklentiler
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Kullanılabilir Eklentiler (Marketplace Ready)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(uiState.availablePlugins) { plugin ->
                        AvailablePluginCard(
                            plugin = plugin,
                            onInstall = { viewModel.installPlugin(plugin.manifest) }
                        )
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
private fun InstalledPluginCard(
    plugin: PluginItem,
    metrics: PluginSandboxMetrics?,
    onToggle: (Boolean) -> Unit,
    onUninstall: () -> Unit,
    onRunSandbox: () -> Unit
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${plugin.manifest.category.iconEmoji} ${plugin.manifest.pluginName}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "v${plugin.manifest.version} • Geliştirici: ${plugin.manifest.developerName}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = plugin.state == PluginState.ENABLED,
                    onCheckedChange = onToggle
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Genişletme Noktası: ${plugin.manifest.extensionPoint.pointName}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Gerekli İzinler: ${plugin.manifest.requiredPermissions.joinToString { it.permissionName }}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (metrics != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = "Sandbox Süresi: ${metrics.executionTimeMs} ms • Bellek: ${metrics.memoryFootprintMb} MB • İzole 🟢",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onRunSandbox,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Sandbox Test", style = MaterialTheme.typography.labelSmall)
                }

                if (!plugin.isBuiltIn) {
                    TextButton(
                        onClick = onUninstall,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Kaldır", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

@Composable
private fun AvailablePluginCard(
    plugin: PluginItem,
    onInstall: () -> Unit
) {
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${plugin.manifest.category.iconEmoji} ${plugin.manifest.pluginName}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "v${plugin.manifest.version} • ${plugin.manifest.developerName}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = onInstall,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Yükle", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
