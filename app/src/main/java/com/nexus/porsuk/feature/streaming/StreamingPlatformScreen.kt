package com.nexus.porsuk.feature.streaming

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexus.porsuk.domain.model.*

/**
 * Porsuk Real-Time Streaming Data Platform — Ana Ekran (StreamingPlatformScreen)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StreamingPlatformScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: StreamingPlatformViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Real-Time Streaming Platform", fontWeight = FontWeight.Bold)
                        Text(
                            text = "${uiState.activeProvider.displayName} • ${uiState.streamHealth.latencyMs} ms",
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
                // Sağlayıcı Seçim Çipleri (LazyRow)
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(StreamingProviderType.entries) { provider ->
                        FilterChip(
                            selected = uiState.activeProvider == provider,
                            onClick = { viewModel.selectProvider(provider) },
                            label = { Text("${provider.iconEmoji} ${provider.displayName}") }
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
                    // 1. Akış Sağlığı ve Bağlantı Kartı
                    item {
                        StreamHealthCard(
                            state = uiState.connectionState,
                            health = uiState.streamHealth,
                            onToggleConnection = { viewModel.toggleConnection() }
                        )
                    }

                    // 2. Abone Olunan Semboller
                    item {
                        Text(
                            text = "Abone Olunan Semboller (Batch Stream)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(uiState.subscribedSymbols.toList()) { symbol ->
                                InputChip(
                                    selected = true,
                                    onClick = { viewModel.unsubscribeSymbol(symbol) },
                                    label = { Text(symbol, fontWeight = FontWeight.Bold) }
                                )
                            }
                        }
                    }

                    // 3. Canlı Fiyat Tıkları (Tick Stream Feed)
                    item {
                        Text(
                            text = "Canlı İşlem Akışı (Trade & Quote Ticks)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(uiState.latestTicks) { tick ->
                        TickRowCard(tick = tick)
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
private fun StreamHealthCard(
    state: ConnectionState,
    health: StreamHealthMetrics,
    onToggleConnection: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(state.colorHex).copy(alpha = 0.2f)
                ) {
                    Text(
                        text = state.displayName,
                        color = Color(state.colorHex),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                TextButton(onClick = onToggleConnection) {
                    Text(if (state == ConnectionState.CONNECTED) "Bağlantıyı Kes" else "Yeniden Bağlan")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Gecikme (Latency): ${health.latencyMs} ms", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                Text("Tık Hızı: ${String.format(java.util.Locale.US, "%.1f", health.tickRatePerSec)} tick/s", style = MaterialTheme.typography.bodySmall)
            }
            Text("Paket Kaybı: %${health.packetLossPct} • Ping/Pong Heartbeat: Aktif 🟢", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun TickRowCard(tick: MarketTickEvent) {
    val isPositive = tick.changeAmount >= 0
    val cardBg by animateColorAsState(
        targetValue = if (isPositive) Color(0xFF00C853).copy(alpha = 0.1f) else Color(0xFFD50000).copy(alpha = 0.1f),
        label = "TickBgAnimation"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(tick.symbol, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text("Hacim: ${tick.volume} • Bid: ${tick.bidPrice} Ask: ${tick.askPrice}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${tick.lastPrice}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isPositive) Color(0xFF00C853) else Color(0xFFD50000)
                )
                Text(
                    text = "${if (isPositive) "+" else ""}${tick.changePct}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isPositive) Color(0xFF00C853) else Color(0xFFD50000),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
