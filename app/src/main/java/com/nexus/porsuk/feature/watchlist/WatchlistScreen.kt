package com.nexus.porsuk.feature.watchlist

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexus.porsuk.feature.markets.components.MarketSearchBar
import com.nexus.porsuk.feature.watchlist.components.*

/**
 * Porsuk Watchlist Pro — Takip Listeleri Ekranı (WatchlistScreen)
 *
 * Sınırsız takip listeleri, Akıllı Klasörler (10 Kategori), toplu silme/ekleme ve geleceğe hazır Orakul AI analiz stubs sunan ana ekran.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchlistScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToCompanyDetail: (String) -> Unit = {},
    viewModel: WatchlistViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Watchlist Pro",
                        style = MaterialTheme.typography.headlineMedium,
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
                actions = {
                    // Favori Liste Belirleme Butonu
                    uiState.selectedGroup?.let { group ->
                        IconButton(onClick = { viewModel.toggleFavoriteGroup(group.groupId, !group.isFavorite) }) {
                            Icon(
                                imageVector = if (group.isFavorite) Icons.Default.Star else Icons.Outlined.StarBorder,
                                contentDescription = "Favori Liste Yap",
                                tint = if (group.isFavorite) Color(0xFFFFB300) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Toplu Seçim Modu Butonu
                    IconButton(onClick = { viewModel.toggleMultiSelectMode() }) {
                        Icon(
                            imageVector = Icons.Default.Checklist,
                            contentDescription = "Toplu Seçim Modu",
                            tint = if (uiState.isMultiSelectMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Yeni Liste Oluşturma Butonu
                    IconButton(onClick = { showCreateDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Yeni Liste Ekle",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            // Toplu Silme Barı (Toplu seçim modu aktifse görünür)
            if (uiState.isMultiSelectMode && uiState.selectedSymbolsForBulkDelete.isNotEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    tonalElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${uiState.selectedSymbolsForBulkDelete.size} varlık seçildi",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontWeight = FontWeight.Bold
                        )
                        Button(
                            onClick = { viewModel.executeBulkDelete() },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Sil")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Toplu Sil")
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 1. Çoklu Liste Sekmeleri TabBar
            WatchlistGroupTabRow(
                groups = uiState.groups,
                selectedGroup = uiState.selectedGroup,
                onGroupSelected = { viewModel.selectGroup(it) },
                onCreateGroupClick = { showCreateDialog = true }
            )

            // 2. 10 Akıllı Klasör (Smart Categories) Barı
            SmartCategoryChipBar(
                selectedCategory = uiState.selectedSmartCategory,
                onCategorySelected = { viewModel.selectSmartCategory(it) },
                modifier = Modifier.padding(vertical = 4.dp)
            )

            // 3. Arama Çubuğu
            MarketSearchBar(
                query = uiState.searchQuery,
                onQueryChange = { viewModel.updateSearchQuery(it) },
                placeholderText = "Listede sembol ara...",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )

            // 4. Geleceğe Hazır Orakul AI Watchlist Skoru & Analiz Kartı (Stub)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "🤖 Orakul Watchlist AI Skoru: 88/100",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Risk Dağılımı: Dengeli | Baskın Sektör: Teknoloji & Havacılık",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // 5. Liste Kalemleri (LazyColumn)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (uiState.filteredItems.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Bu takip listesinde henüz varlık bulunmuyor.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = uiState.filteredItems,
                            key = { it.symbol }
                        ) { item ->
                            WatchlistItemRowCard(
                                item = item,
                                isMultiSelectMode = uiState.isMultiSelectMode,
                                isSelectedForDelete = uiState.selectedSymbolsForBulkDelete.contains(item.symbol),
                                onItemClick = { onNavigateToCompanyDetail(item.symbol) },
                                onSelectToggle = { viewModel.toggleBulkSelectSymbol(item.symbol) }
                            )
                        }
                    }
                }
            }
        }

        // Yeni Liste Oluşturma Diyaloğu
        if (showCreateDialog) {
            CreateWatchlistDialog(
                onDismissRequest = { showCreateDialog = false },
                onCreateGroup = { title, category ->
                    viewModel.createNewGroup(title, category)
                }
            )
        }
    }
}
