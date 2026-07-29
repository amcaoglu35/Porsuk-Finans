package com.nexus.porsuk.ui.news

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.data.local.entity.NewsItemEntity
import com.nexus.porsuk.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(
    viewModel: NewsViewModel,
    onBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("📰 Finans Haberleri & Akış", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundNew)
            )
        },
        containerColor = BackgroundNew
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = { viewModel.loadNews("BIST") },
            modifier = Modifier.padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Arama Çubuğu
                item {
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Haber ara veya filtrele...", fontSize = 12.sp, color = SubText) },
                        leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = PrimaryTeal) },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = CardNew,
                            unfocusedContainerColor = CardNew,
                            focusedBorderColor = PrimaryTeal,
                            unfocusedBorderColor = LineBorder
                        )
                    )
                }

                // 2. Kategori Çipleri (8 Kategori)
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.categories) { category ->
                            val isSelected = uiState.selectedCategory == category
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.selectCategory(category) },
                                label = { Text(category, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PrimaryTeal,
                                    selectedLabelColor = Color.White,
                                    containerColor = CardNew,
                                    labelColor = InkText
                                ),
                                border = FilterChipDefaults.filterChipBorder(enabled = true, selected = isSelected, borderColor = LineBorder)
                            )
                        }
                    }
                }

                // 3. Haber Kartları Listesi
                if (uiState.filteredNews.isEmpty() && !uiState.isLoading) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                            Text("Aranan kriterlere uygun haber bulunamadı.", fontSize = 13.sp, color = SubText, fontFamily = Manrope)
                        }
                    }
                } else {
                    items(uiState.filteredNews, key = { it.id }) { news ->
                        val isFavorite = uiState.favoriteNewsIds.contains(news.id)
                        val isRead = uiState.readNewsIds.contains(news.id)
                        
                        NewsCardItem(
                            news = news,
                            isFavorite = isFavorite,
                            isRead = isRead,
                            onFavoriteClick = { viewModel.toggleFavorite(news.id) },
                            onItemClick = { viewModel.markAsRead(news.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NewsCardItem(
    news: NewsItemEntity,
    isFavorite: Boolean,
    isRead: Boolean,
    onFavoriteClick: () -> Unit,
    onItemClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onItemClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = if (isRead) CardNew.copy(alpha = 0.6f) else CardNew),
        border = BorderStroke(1.dp, LineBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        color = PrimaryTeal.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = news.source.ifBlank { "Finans Akışı" },
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryTeal,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontFamily = Manrope
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    // Sentiment Badge
                    val sentimentVal = news.sentiment ?: "NÖTR"
                    val (badgeBg, badgeText, label) = when (sentimentVal) {
                        "POZİTİF" -> Triple(PositiveGreen.copy(alpha = 0.15f), PositiveGreen, "POZİTİF")
                        "NEGATİF" -> Triple(NegativeRed.copy(alpha = 0.15f), NegativeRed, "NEGATİF")
                        else -> Triple(SubText.copy(alpha = 0.15f), SubText, "NÖTR")
                    }
                    Surface(color = badgeBg, shape = RoundedCornerShape(6.dp)) {
                        Text(
                            label,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = badgeText,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontFamily = JetBrainsMono
                        )
                    }

                    IconButton(onClick = onFavoriteClick, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Favori",
                            tint = if (isFavorite) PrimaryTeal else SubText,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Text(
                text = news.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = InkText,
                fontFamily = Manrope,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if (!news.summary.isNullOrBlank()) {
                Text(
                    text = news.summary,
                    fontSize = 12.sp,
                    color = SubText,
                    fontFamily = Manrope,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
