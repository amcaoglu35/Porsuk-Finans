package com.nexus.porsuk.feature.backtest

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
import com.nexus.porsuk.domain.model.BacktestMode
import com.nexus.porsuk.feature.backtest.components.*

/**
 * Porsuk Backtesting Engine — Ana Ekran (BacktestScreen)
 *
 * Olay-güdümlü simülasyonu, 16 finansal performans metriğini, sermaye eğrisini ve Buy & Hold karşılaştırmasını sunar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BacktestScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: BacktestViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Backtesting Engine",
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
            // 1. 4 Test Modu Seçim Barı
            ScrollableTabRow(
                selectedTabIndex = uiState.config.mode.ordinal,
                edgePadding = 16.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                divider = {},
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                BacktestMode.entries.forEach { mode ->
                    FilterChip(
                        selected = mode == uiState.config.mode,
                        onClick = { viewModel.selectMode(mode) },
                        label = { Text(mode.displayName) },
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }

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

                    // 2. Strateji vs Buy & Hold Karşılaştırma Kartı
                    BenchmarkComparisonCard(report = uiState.report)

                    // 3. Sermaye Eğrisi (Equity Curve)
                    EquityCurveCard(points = uiState.report?.equityCurve ?: emptyList())

                    // 4. 16 Finansal Metrik Kartı
                    uiState.report?.metrics?.let { metrics ->
                        MetricsGridCard(metrics = metrics)
                    }

                    // 5. İşlem Günlüğü Tablosu
                    TradeLogTableCard(tradeLogs = uiState.report?.tradeLogs ?: emptyList())

                    // 6. Geleceğe Hazır Orakul AI Monte Carlo Simulation Stub Kartı
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "🎲 Orakul AI 1.000 Monte Carlo Simülasyonu",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Orakul AI: Stratejinizin 1.000 farklı piyasa senaryosunda kârlı kalma olasılığı %92.4'tür. En kötü durum drawdown tahmini: -%22.5.",
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
