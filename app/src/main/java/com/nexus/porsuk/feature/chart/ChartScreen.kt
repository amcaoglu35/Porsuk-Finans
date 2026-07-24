package com.nexus.porsuk.feature.chart

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.nexus.porsuk.domain.model.ChartType
import com.nexus.porsuk.feature.chart.components.*

/**
 * Porsuk Professional Chart Center — Grafik Ekranı (ChartScreen)
 *
 * 10 Zaman diliminde 7 farklı grafik türünü, çizim araçlarını, soyut ChartRenderer yüzeyini ve portföy katmanını sunan ana ekran.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChartScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: ChartViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Grafik Merkezi (${uiState.symbol})",
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
            // 1. 10 Zaman Dilimi Barı (1m, 5m, 15m, 30m, 1h, 4h, 1D, 1W, 1M, 1Y)
            ChartTimeFrameBar(
                selectedTimeFrame = uiState.selectedTimeFrame,
                onTimeFrameSelected = { viewModel.selectTimeFrame(it) }
            )

            // 2. Çizim Araçları Barı
            ChartToolboxBar(
                selectedTool = uiState.selectedTool,
                onToolSelected = { viewModel.selectTool(it) }
            )

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Spacer(modifier = Modifier.height(2.dp))

                    // 3. Soyut Grafik Motoru Yüzeyi (ChartCanvasView)
                    ChartCanvasView(
                        candles = uiState.candles,
                        portfolioMarkers = uiState.portfolioMarkers,
                        showPortfolioOverlay = uiState.showPortfolioOverlay
                    )

                    // 4. Grafik Türü Seçim Chip Barı
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Grafik Türü:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Row {
                            ChartType.entries.take(4).forEach { type ->
                                FilterChip(
                                    selected = type == uiState.selectedChartType,
                                    onClick = { viewModel.selectChartType(type) },
                                    label = { Text(type.name, style = MaterialTheme.typography.labelSmall) },
                                    modifier = Modifier.padding(horizontal = 2.dp)
                                )
                            }
                        }
                    }

                    // 5. Geleceğe Hazır Orakul AI Pattern Detection & Auto Draw Stub Kartı
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "🤖 Orakul AI Otomatik Grafik Analizi & Formasyon Tespiti",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Orakul AI: Fincan Kulp formasyonu (Cup & Handle) tespit edildi. Hedef Fiyat: 310.0 TL (%91.5 Güvenilirlik Oranı).",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}
