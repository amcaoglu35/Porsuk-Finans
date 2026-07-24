package com.nexus.porsuk.feature.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.nexus.porsuk.feature.settings.components.CurrencyRow
import com.nexus.porsuk.feature.settings.components.ThemeSelectionCard

/**
 * Porsuk Finans — Settings & Personalization Center Ana Ekranı (SettingsCenterScreen)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsCenterScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: SettingsCenterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Ayarlar & Kişiselleştirme", fontWeight = FontWeight.Bold)
                        Text(uiState.appVersion, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                // 1. Tema Seçim Kartı
                item {
                    ThemeSelectionCard(
                        currentTheme = uiState.themeSettings.themeMode,
                        onThemeSelect = { viewModel.setThemeMode(it) }
                    )
                }

                // 2. Para Birimi & Bölge Ayarları
                item {
                    CurrencyRow(
                        currentCurrency = uiState.regionPreferences.currency,
                        onCurrencySelect = { viewModel.setCurrency(it) }
                    )
                }

                // 3. Portföy Tercihleri
                item {
                    Text(
                        text = "Portföy & Gizlilik Tercihleri",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
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
                                Text("Portföy Değerini Gizle", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text("Varlık bakiyelerini *** şeklinde maskeler", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = uiState.marketAiPreferences.isPortfolioValueHidden,
                                onCheckedChange = { viewModel.toggleHidePortfolioValue(it) }
                            )
                        }
                    }
                }

                // 4. AI Modeli & Analiz Ayarları
                item {
                    Text(
                        text = "Orakul AI Tercihleri",
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
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Varsayılan AI Modeli: ${uiState.marketAiPreferences.defaultAiModel}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            Text("Analiz Detay Seviyesi: Derinlemesine Kurumsal (Institutional)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
