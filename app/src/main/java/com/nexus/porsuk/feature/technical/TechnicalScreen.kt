package com.nexus.porsuk.feature.technical

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
import com.nexus.porsuk.domain.model.TimeFrame
import com.nexus.porsuk.feature.technical.components.*

/**
 * Porsuk Technical Engine — Teknik Analiz Ekranı (TechnicalScreen)
 *
 * 9 Zaman diliminde 5 farklı gösterge grubunun hesaplamalarını, destek & direnç seviyelerini ve sinyal durumlarını sunan ana ekran.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TechnicalScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: TechnicalViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Teknik Analiz (${uiState.symbol})",
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
            // 1. 9 Zaman Dilimi Barı (1m, 5m, 15m, 30m, 1h, 4h, 1D, 1W, 1M)
            ScrollableTabRow(
                selectedTabIndex = uiState.selectedTimeFrame.ordinal,
                edgePadding = 16.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                divider = {},
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                TimeFrame.entries.forEach { tf ->
                    val isSelected = tf == uiState.selectedTimeFrame
                    Tab(
                        selected = isSelected,
                        onClick = { viewModel.selectTimeFrame(tf) },
                        text = {
                            Text(
                                text = tf.displayName,
                                style = if (isSelected) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodySmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
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
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Spacer(modifier = Modifier.height(4.dp))

                    // 2. Genel Teknik Sinyal Kartı
                    TechnicalSummaryCard(report = uiState.report)

                    // 3. Destek & Direnç Seviyeleri Kartı
                    SupportResistanceLevelsCard(
                        supports = uiState.report?.supportLevels ?: emptyList(),
                        resistances = uiState.report?.resistanceLevels ?: emptyList()
                    )

                    // 4. Formasyon Tanıma Stub Kartı
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "📐 Tespit Edilen Formasyonlar (Pattern Recognition)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            uiState.report?.detectedPatterns?.forEach { pattern ->
                                Text(
                                    text = "• ${pattern.patternName} (${pattern.reliability} Güvenilirlik)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // 5. Kategoriye Göre 5 İndikatör Grubu Tablu Izgarası
                    IndicatorsTabbedGrid(
                        selectedCategory = uiState.selectedCategory,
                        onCategorySelected = { viewModel.selectCategory(it) },
                        indicators = uiState.report?.indicators ?: emptyList()
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}
