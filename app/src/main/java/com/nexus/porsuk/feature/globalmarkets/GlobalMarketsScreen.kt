package com.nexus.porsuk.feature.globalmarkets

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
import com.nexus.porsuk.feature.globalmarkets.components.*

/**
 * Porsuk Global Markets Center — Ana Ekran (GlobalMarketsScreen)
 *
 * 8 Küresel bölgeyi, 5 borsa durumunu, 10 sektörü ve dünya ısı haritası verilerini sunan ana ekran.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalMarketsScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: GlobalMarketsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Global Markets Center",
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
            // 1. 8 Bölge Seçim Chip Barı
            RegionSelectorChips(
                selectedRegion = uiState.selectedRegion,
                onRegionSelected = { viewModel.selectRegion(it) }
            )

            // 2. Borsa Açılış/Kapanış Durumu Banner Kartı
            MarketStatusBanner(
                statusInfo = uiState.exchangeStatus,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            // 3. Dünya Isı Haritası & Sermaye Akışı Kartı
            WorldHeatMapCard(
                heatMapData = uiState.heatMapData,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            // 4. 10 Sektör Takip Merkezi
            SectorPerformanceGrid(
                sectors = uiState.sectors,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            // 5. Piyasa Varlık Akış Listesi
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (uiState.tickers.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Seçilen bölgede piyasa verisi bulunamadı.",
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
                            items = uiState.tickers,
                            key = { it.symbol }
                        ) { ticker ->
                            TickerDashboardCard(item = ticker)
                        }
                    }
                }
            }
        }
    }
}
