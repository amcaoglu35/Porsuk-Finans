package com.nexus.porsuk.feature.quant

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexus.porsuk.domain.model.*

/**
 * Porsuk Quant Research Studio — Ana Ekran (QuantResearchScreen)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuantResearchScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: QuantResearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Quant Research Studio", fontWeight = FontWeight.Bold)
                        Text(
                            text = uiState.activeWorkspace.title,
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
                // Faktör Kategorileri (LazyRow)
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = uiState.selectedFactorCategory == null,
                            onClick = { viewModel.selectFactorCategory(null) },
                            label = { Text("Tüm Faktörler") }
                        )
                    }
                    items(FactorCategory.entries) { cat ->
                        FilterChip(
                            selected = uiState.selectedFactorCategory == cat,
                            onClick = { viewModel.selectFactorCategory(cat) },
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
                    // 1. İstatistiksel Korelasyon & Regresyon Analiz Kartı
                    item {
                        StatisticalCorrelationCard(stats = uiState.statisticalResult)
                    }

                    // 2. Niceliksel Faktörler Listesi
                    item {
                        Text(
                            text = "Faktör Analizi & Metrikler (Factor Models)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    val filteredFactors = uiState.factorMetrics.filter {
                        uiState.selectedFactorCategory == null || it.category == uiState.selectedFactorCategory
                    }

                    items(filteredFactors) { factor ->
                        FactorMetricCard(factor = factor)
                    }

                    // 3. Risk & Portföy Dağılım Araştırması
                    item {
                        PortfolioResearchCard(port = uiState.portfolioResearch)
                    }

                    // 4. Araştırma Not Defteri (Research Notebook)
                    item {
                        ResearchNotebookCard(
                            workspace = uiState.activeWorkspace,
                            onSaveNotes = { viewModel.saveWorkspaceNotes(it) }
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
private fun StatisticalCorrelationCard(stats: StatisticalAnalysisResult) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("📊 İstatistiksel Regresyon & Korelasyon Analizi", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Varlık Çifti: ${stats.assetPair}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Korelasyon (r): ${stats.correlationCoefficient}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                Text("R² (R-Kare): ${stats.rSquared}", style = MaterialTheme.typography.bodySmall)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Beta (β): ${stats.beta}", style = MaterialTheme.typography.bodySmall)
                Text("Alfa (α): +%${stats.alpha * 100}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            }
            Text("Kovaryans: ${stats.covariance} • p-Value: ${stats.pValue} (İstatistiki Anlamlı 🟢)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun FactorMetricCard(factor: FactorMetric) {
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
                Text(
                    text = "${factor.category.iconEmoji} ${factor.name}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "Ham Değer: ${factor.rawValue}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(factor.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Z-Score: ${factor.zScore} • Yüzdelik Dilim (Percentile): %${factor.percentileRank}",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun PortfolioResearchCard(port: PortfolioResearchMetrics) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("🎯 Portföy Çeşitlendirme & Risk Nitelikleri", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Çeşitlendirme Skoru: ${port.diversificationScore} / 100", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            Text("Sharpe Oranı: ${port.sharpeRatio} • Sortino Oranı: ${port.sortinoRatio}", style = MaterialTheme.typography.bodySmall)
            Text("Maksimum Kayıp (Max Drawdown): -%${port.maxDrawdownPct}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)

            Spacer(modifier = Modifier.height(8.dp))
            Text("Sektörel Dağılım:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            port.sectorExposures.forEach { (sector, pct) ->
                Text("• $sector: %$pct", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun ResearchNotebookCard(
    workspace: ResearchWorkspace,
    onSaveNotes: (String) -> Unit
) {
    var notesText by remember(workspace.notebookNotes) { mutableStateOf(workspace.notebookNotes) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("📝 Research Notebook & Araştırma Notları", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = notesText,
                onValueChange = { notesText = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Not Defteri / Strateji Hipotezleri") },
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { onSaveNotes(notesText) },
                modifier = Modifier.align(Alignment.End),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Notu Kaydet")
            }
        }
    }
}
