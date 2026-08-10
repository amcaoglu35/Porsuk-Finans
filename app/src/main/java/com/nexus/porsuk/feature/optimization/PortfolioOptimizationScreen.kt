package com.nexus.porsuk.feature.optimization

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexus.porsuk.domain.model.*

/**
 * Porsuk Portfolio Optimization & Asset Allocation Engine — Ana Ekran (PortfolioOptimizationScreen)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioOptimizationScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: PortfolioOptimizationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Portfolio Optimization Engine", fontWeight = FontWeight.Bold)
                        Text(
                            text = "Sharpe: ${uiState.riskMetrics.sharpeRatio} • Çeşitlendirme: ${uiState.riskMetrics.diversificationScore}/100",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri"
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
                // Optimizasyon Strateji Seçimi (LazyRow)
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(OptimizationStrategyType.entries) { strategy ->
                        FilterChip(
                            selected = uiState.selectedStrategy == strategy,
                            onClick = { viewModel.selectStrategy(strategy) },
                            label = { Text("${strategy.iconEmoji} ${strategy.displayName}") }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Risk-Getiri Etkin Sınır Kartı (Efficient Frontier)
                    item {
                        EfficientFrontierCard(
                            points = uiState.frontierPoints,
                            metrics = uiState.riskMetrics
                        )
                    }

                    // 2. Optimum Varlık Dağılımı (Asset Allocation Weights)
                    item {
                        Text(
                            text = "Mevcut vs. Optimum Varlık Dağılım Ağırlıkları",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(uiState.allocations) { item ->
                        AssetAllocationCard(allocation = item)
                    }

                    // 3. Rebalancing Önerileri (Smart Drift Check)
                    item {
                        Text(
                            text = "Yeniden Dengeleme Önerileri (Smart Rebalance)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(uiState.rebalanceSuggestions) { reb ->
                        RebalanceSuggestionCard(suggestion = reb)
                    }

                    // 4. Stres Testi Senaryoları (Stress Testing)
                    item {
                        Text(
                            text = "Portföy Şok & Stres Testi Senaryoları",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(uiState.stressTestScenarios) { scenario ->
                        StressTestCard(
                            scenario = scenario,
                            onRunTest = { viewModel.runScenarioStressTest(scenario.scenarioId) }
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
private fun EfficientFrontierCard(
    points: List<EfficientFrontierPoint>,
    metrics: PortfolioRiskMetrics
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("📈 Markowitz Etkin Sınır (Efficient Frontier)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Sharpe Oranı: ${metrics.sharpeRatio}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text("Sortino Oranı: ${metrics.sortinoRatio}", style = MaterialTheme.typography.bodySmall)
                Text("Max Drawdown: -%${metrics.maxDrawdownPct}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Riske Maruz Değer (VaR %95): -%${metrics.valueAtRiskVaR}", style = MaterialTheme.typography.labelSmall)
                Text("CVaR (Expected Shortfall): -%${metrics.conditionalVaR}", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
private fun AssetAllocationCard(allocation: AssetAllocationItem) {
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
                Text("${allocation.assetClass.iconEmoji} ${allocation.symbol} - ${allocation.name}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "Beklenen Getiri: %${allocation.expectedReturnPct}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Mevcut Ağırlık: %${allocation.currentWeightPct}", style = MaterialTheme.typography.bodySmall)
                Text("Optimum Hedef Ağırlık: %${allocation.targetOptimizedWeightPct}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
private fun RebalanceSuggestionCard(suggestion: RebalanceSuggestion) {
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
            Column {
                Text("⚖️ ${suggestion.symbol}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(suggestion.actionText, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
            Text("Sapma (Drift): %${suggestion.driftPct}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun StressTestCard(
    scenario: StressTestScenario,
    onRunTest: () -> Unit
) {
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
            Column(modifier = Modifier.weight(1f)) {
                Text(scenario.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text("Kategori: ${scenario.category}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Button(
                onClick = onRunTest,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("${scenario.expectedPortfolioChangePct}%", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
