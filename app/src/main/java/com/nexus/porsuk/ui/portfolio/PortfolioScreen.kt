package com.nexus.porsuk.ui.portfolio

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexus.porsuk.ui.portfolio.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioScreen(
    viewModel: PortfolioViewModel = hiltViewModel(),
    onStockClick: (String, String) -> Unit,
    onNavigateToSettings: () -> Unit = {},
    onLedgerClick: () -> Unit = {},
    onAnalysisClick: () -> Unit = {}
) {
    val totalBalance by viewModel.totalBalanceTry.collectAsState()
    val totalChange by viewModel.totalChangePercent.collectAsState()
    val numberFormat by viewModel.numberFormat.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    val consolidatedHoldings by viewModel.consolidatedHoldings.collectAsState()
    val portfolioChartData by viewModel.portfolioChartData.collectAsState()
    val selectedTimeframe by viewModel.selectedChartTimeframe.collectAsState()
    val riskMetrics by viewModel.portfolioRiskMetrics.collectAsState()
    val aiInsight by viewModel.aiPortfolioInsight.collectAsState()

    var isBalanceVisible by remember { mutableStateOf(true) }

    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isVisible = true
        if (portfolioChartData.isEmpty()) {
            viewModel.updateChartTimeframe(0)
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            PortfolioTopBar(
                onSearchClick = {},
                onNotificationClick = onNavigateToSettings
            )
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.refreshAllData() },
            modifier = Modifier.padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 36.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // 1. Portföy Özeti Kartı
                item(key = "portfolio_summary_hero") {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(tween(400)) + slideInVertically(initialOffsetY = { 40 })
                    ) {
                        PortfolioSummaryHeroCard(
                            totalBalance = totalBalance,
                            totalChange = totalChange,
                            isBalanceVisible = isBalanceVisible,
                            onToggleVisibility = { isBalanceVisible = !isBalanceVisible },
                            numberFormat = numberFormat,
                            riskMetrics = riskMetrics
                        )
                    }
                }

                // 2. Varlık Dağılımı & 3. Sektör Dağılımı (Side-by-Side Donut Grid)
                item(key = "allocations_donuts_section") {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(tween(550)) + slideInVertically(initialOffsetY = { 40 })
                    ) {
                        AllocationsDonutGridSection(
                            onAnalysisClick = onAnalysisClick,
                            riskMetrics = riskMetrics
                        )
                    }
                }

                // 4. Portföy Performansı (Chart & Hover Touch Tooltip)
                item(key = "portfolio_performance_chart") {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(tween(700)) + slideInVertically(initialOffsetY = { 40 })
                    ) {
                        PortfolioPerformanceChartCard(
                            selectedTimeframe = selectedTimeframe,
                            onTimeframeSelected = { viewModel.updateChartTimeframe(it) },
                            chartData = portfolioChartData,
                            numberFormat = numberFormat
                        )
                    }
                }

                // 5. Varlıklarım Tablosu
                item(key = "my_holdings_section") {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(tween(850)) + slideInVertically(initialOffsetY = { 40 })
                    ) {
                        MyHoldingsSection(
                            onStockClick = onStockClick,
                            holdings = consolidatedHoldings,
                            numberFormat = numberFormat
                        )
                    }
                }

                // 6. AI Portföy Doktoru & 7. Oracle Kartı
                item(key = "ai_doctor_and_oracle_section") {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(tween(1000)) + slideInVertically(initialOffsetY = { 40 })
                    ) {
                        AiDoctorAndOracleSection(
                            onAnalysisClick = onAnalysisClick,
                            riskMetrics = riskMetrics,
                            aiInsight = aiInsight,
                            onGenerateInsight = { viewModel.generateAiPortfolioInsight() }
                        )
                    }
                }

                // 8. Hızlı İşlemler
                item(key = "quick_actions_and_reports") {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(tween(1150)) + slideInVertically(initialOffsetY = { 40 })
                    ) {
                        QuickActionsAndReportsSection(
                            onLedgerClick = onLedgerClick,
                            onAnalysisClick = onAnalysisClick
                        )
                    }
                }
            }
        }
    }
}
