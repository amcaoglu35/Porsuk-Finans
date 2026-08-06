package com.nexus.porsuk.feature.scanner

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.nexus.porsuk.domain.model.ScanMarketType
import com.nexus.porsuk.feature.scanner.components.PresetScanChipsBar
import com.nexus.porsuk.feature.scanner.components.ScanResultItemRow

/**
 * Porsuk Smart Scanner Engine — Piyasa Tarayıcı Ekranı (ScannerScreen)
 *
 * 10 Piyasada 11 hazır tarama stratejisini, Master Score ve gelişmiş filtrelerle tarama sonuçlarını sunan ana ekran.
 */
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: ScannerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Akıllı Piyasa Tarayıcısı",
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
            // 1. Piyasa Seçim Barı (BIST, NASDAQ, TEFAS, Kripto...)
            ScrollableTabRow(
                selectedTabIndex = uiState.selectedMarket.ordinal,
                edgePadding = 16.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                divider = {},
                modifier = Modifier.padding(vertical = 2.dp)
            ) {
                ScanMarketType.entries.forEach { mkt ->
                    Tab(
                        selected = mkt == uiState.selectedMarket,
                        onClick = { viewModel.selectMarket(mkt) },
                        text = {
                            Text(
                                text = mkt.displayName,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = if (mkt == uiState.selectedMarket) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // 2. 11 Hazır Tarama Stratejisi Chip Barı
            PresetScanChipsBar(
                selectedPreset = uiState.selectedPreset,
                onPresetSelected = { viewModel.selectPreset(it) }
            )

            // 3. Geleceğe Hazır Orakul AI Opportunity Scanner Stub Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🤖 Orakul AI Fırsat Taraması: ${uiState.scanResults.size} varlık kriterlere tam uyuyor (Master Score > 80+).",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // 4. Tarama Sonuç Akış Listesi
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (uiState.scanResults.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Seçilen kriterlere uygun varlık bulunamadı.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = uiState.scanResults,
                            key = { it.symbol }
                        ) { item ->
                            ScanResultItemRow(item = item)
                        }
                    }
                }
            }
        }
    }
}
