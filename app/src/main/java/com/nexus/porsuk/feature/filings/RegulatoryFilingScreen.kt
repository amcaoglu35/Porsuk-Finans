package com.nexus.porsuk.feature.filings

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
 * Porsuk Regulatory Filings & Disclosure Intelligence Platform — Ana Ekran (RegulatoryFilingScreen)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegulatoryFilingScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: RegulatoryFilingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Regulatory Filings & Disclosure", fontWeight = FontWeight.Bold)
                        Text(
                            text = "${uiState.activeProvider.displayName} • ${uiState.filings.size} Bildirim",
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
                // Veri Sağlayıcı Seçim Çipleri (LazyRow)
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(FilingProviderType.entries) { provider ->
                        FilterChip(
                            selected = uiState.activeProvider == provider,
                            onClick = { viewModel.selectProvider(provider) },
                            label = { Text("${provider.iconEmoji} ${provider.displayName}") }
                        )
                    }
                }

                // Sınıflandırma Kategori Çipleri (LazyRow)
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = uiState.selectedCategoryFilter == null,
                            onClick = { viewModel.selectCategoryFilter(null) },
                            label = { Text("Tüm Kategoriler") }
                        )
                    }
                    items(FilingCategory.entries) { cat ->
                        FilterChip(
                            selected = uiState.selectedCategoryFilter == cat,
                            onClick = { viewModel.selectCategoryFilter(cat) },
                            label = { Text("${cat.iconEmoji} ${cat.displayName}") }
                        )
                    }
                }

                // Arama Kutusu (Search Field)
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChange(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    placeholder = { Text("Şirket kodu, konu veya KAP bildirimlerde ara...") },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(4.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            text = "Son Bildirimler & Özel Durum Açıklamaları",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    val filteredFilings = uiState.filings.filter { filing ->
                        val matchesProvider = filing.provider == uiState.activeProvider
                        val matchesCategory = uiState.selectedCategoryFilter == null || filing.category == uiState.selectedCategoryFilter
                        val matchesQuery = uiState.searchQuery.isEmpty() ||
                                filing.companySymbol.contains(uiState.searchQuery, ignoreCase = true) ||
                                filing.title.contains(uiState.searchQuery, ignoreCase = true)
                        matchesProvider && matchesCategory && matchesQuery
                    }

                    items(filteredFilings) { filing ->
                        FilingCard(
                            filing = filing,
                            onAiSummaryClick = { viewModel.loadAiSummaryForFiling(filing) }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }

    // AI Özet ModalBottomSheet veya Dialog
    if (uiState.selectedFilingForSummary != null && uiState.activeAiSummary != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearAiSummaryModal() },
            title = {
                Text(
                    text = "🤖 AI Bilanço & KAP Özeti",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = uiState.selectedFilingForSummary!!.title,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = uiState.activeAiSummary!!.executiveSummary,
                        style = MaterialTheme.typography.bodySmall
                    )

                    Text(
                        text = "Öne Çıkan Değişiklikler:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                    uiState.activeAiSummary!!.keyChangesList.forEach { change ->
                        Text("• $change", style = MaterialTheme.typography.bodySmall)
                    }
                }
            },
            confirmButton = {
                Button(onClick = { viewModel.clearAiSummaryModal() }) {
                    Text("Kapat")
                }
            }
        )
    }
}

@Composable
private fun FilingCard(
    filing: RegulatoryFiling,
    onAiSummaryClick: () -> Unit
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
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = filing.companySymbol,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = filing.filingDate,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "${filing.category.iconEmoji} ${filing.title}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = filing.summaryText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onAiSummaryClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("🤖 AI Özet Çıkar", style = MaterialTheme.typography.labelSmall)
                }

                OutlinedButton(
                    onClick = {},
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("PDF İncele", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}
