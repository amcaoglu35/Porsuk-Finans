package com.nexus.porsuk.feature.markets

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexus.porsuk.feature.markets.components.MarketFilterSortSheet
import com.nexus.porsuk.feature.markets.components.MarketItemCard
import com.nexus.porsuk.feature.markets.components.MarketSearchBar
import com.nexus.porsuk.feature.markets.components.MarketTabRow

/**
 * Porsuk Markets Module — Piyasalar Ekranı (MarketsScreen)
 *
 * Kullanıcıların 10 farklı sekmede tüm finansal enstrümanları takip edebildiği,
 * arayabildiği, sıralayabildiği ve Liste/Grid görünümleri arasında geçiş yapabildiği ana ekran.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketsScreen(
    onNavigateToDetail: (String) -> Unit = {},
    viewModel: MarketsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showSortSheet by remember { mutableStateOf(false) }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Piyasalar",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    // Liste / Grid Görünümü Değiştirici
                    IconButton(onClick = { viewModel.toggleViewMode() }) {
                        Icon(
                            imageVector = if (uiState.isGridView) Icons.Default.ViewList else Icons.Default.GridView,
                            contentDescription = "Görünümü Değiştir",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Sıralama Menüsü Açıcı
                    IconButton(onClick = { showSortSheet = true }) {
                        Icon(
                            imageVector = Icons.Default.Sort,
                            contentDescription = "Sırala",
                            tint = MaterialTheme.colorScheme.primary
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
            // 1. Arama Çubuğu
            MarketSearchBar(
                query = uiState.searchQuery,
                onQueryChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // 2. 10 Sekmeli Piyasa Tab Çubuğu
            MarketTabRow(
                selectedTab = uiState.selectedTab,
                onTabSelected = { viewModel.selectTab(it) }
            )

            // 3. İçerik Alanı (Liste veya Grid)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else if (uiState.filteredQuotesList.isEmpty()) {
                    // Boş Ekran Durumu
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (uiState.selectedTab == MarketTab.FAVORITES)
                                "Henüz favorilere eklenmiş enstrüman bulunmuyor."
                            else
                                "Aradığınız kriterlere uygun varlık bulunamadı.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    AnimatedContent(
                        targetState = uiState.isGridView,
                        label = "ViewModeAnimation"
                    ) { isGrid ->
                        if (isGrid) {
                            // Grid Görünümü
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                contentPadding = PaddingValues(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(
                                    items = uiState.filteredQuotesList,
                                    key = { it.symbol }
                                ) { quote ->
                                    MarketItemCard(
                                        quote = quote,
                                        isFavorite = uiState.favoriteSymbols.contains(quote.symbol),
                                        onFavoriteToggle = { viewModel.toggleFavorite(quote.symbol) },
                                        onCardClick = { onNavigateToDetail(quote.symbol) },
                                        isGridView = true
                                    )
                                }
                            }
                        } else {
                            // Liste Görünümü
                            LazyColumn(
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(
                                    items = uiState.filteredQuotesList,
                                    key = { it.symbol }
                                ) { quote ->
                                    MarketItemCard(
                                        quote = quote,
                                        isFavorite = uiState.favoriteSymbols.contains(quote.symbol),
                                        onFavoriteToggle = { viewModel.toggleFavorite(quote.symbol) },
                                        onCardClick = { onNavigateToDetail(quote.symbol) },
                                        isGridView = false
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Sıralama BottomSheet
        if (showSortSheet) {
            MarketFilterSortSheet(
                selectedOption = uiState.sortOption,
                onOptionSelected = { viewModel.updateSortOption(it) },
                onDismissRequest = { showSortSheet = false }
            )
        }
    }
}
