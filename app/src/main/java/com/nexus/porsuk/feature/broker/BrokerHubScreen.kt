package com.nexus.porsuk.feature.broker

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexus.porsuk.domain.model.BrokerProviderType

/**
 * Porsuk Broker Integration Hub — Ana Ekran (BrokerHubScreen)
 *
 * 7 Aracı kurum bağlantı durumlarını (Midas, IBKR, Alpaca, Binance), portföy senkronizasyonunu ve akıllı emir yönlendirmesini sunar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrokerHubScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: BrokerHubViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Broker Integration Hub",
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
            // 1. 7 Aracı Kurum Seçim Barı
            ScrollableTabRow(
                selectedTabIndex = uiState.selectedProvider.ordinal,
                edgePadding = 16.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                divider = {},
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                BrokerProviderType.entries.forEach { provider ->
                    FilterChip(
                        selected = provider == uiState.selectedProvider,
                        onClick = { viewModel.selectProvider(provider) },
                        label = { Text("${provider.countryEmoji} ${provider.displayName}") },
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }

            // 2. Bağlı Hesap Özet Kartları
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Text("🏦 Bağlı Kurumsal Hesaplar", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }

                items(uiState.accounts) { acc ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
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
                                Text(acc.accountName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text("Nakit: $${acc.cashBalanceUsd} • Alım Gücü: $${acc.buyingPowerUsd}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text("$${acc.portfolioValueUsd}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF00C853))
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("📦 Senkronize Edilen Portföy Pozisyonları", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }

                items(uiState.holdings) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
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
                                Text(item.symbol, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text("${item.quantity} Adet • Ort. Maliyet: $${item.averageCostUsd}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text("+$${item.unrealizedPlUsd}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color(0xFF00C853))
                        }
                    }
                }
            }
        }
    }
}
