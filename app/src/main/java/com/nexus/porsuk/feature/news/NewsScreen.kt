package com.nexus.porsuk.feature.news

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexus.porsuk.feature.markets.components.MarketSearchBar
import com.nexus.porsuk.feature.news.components.BreakingNewsCarousel
import com.nexus.porsuk.feature.news.components.NewsArticleCard
import com.nexus.porsuk.feature.news.components.NewsCategoryChipBar
import com.nexus.porsuk.feature.news.components.NewsDetailBottomSheet

/**
 * Porsuk News Intelligence Center — Haberler Ekranı (NewsScreen)
 *
 * 13 Haber kategorisini, Son Dakika Manşet Carousel'ini, AI duygu analizini ve detay okuma BottomSheet'ini sunan ana ekran.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: NewsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "News Intelligence",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
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
                placeholderText = "Haber, KAP bildirim veya duyuru ara...",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )

            // 2. Son Dakika Manşet Carousel
            BreakingNewsCarousel(
                breakingNews = uiState.breakingNews,
                onArticleClick = { viewModel.selectArticleForDetail(it) }
            )

            // 3. 13 Haber Kategorisi FilterChip Barı
            NewsCategoryChipBar(
                selectedCategory = uiState.selectedCategory,
                onCategorySelected = { viewModel.selectCategory(it) },
                modifier = Modifier.padding(vertical = 6.dp)
            )

            // 4. Ana Haber Akış Listesi
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (uiState.filteredNews.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Aradığınız kriterlere uygun haber bulunamadı.",
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
                            items = uiState.filteredNews,
                            key = { it.articleId }
                        ) { article ->
                            NewsArticleCard(
                                article = article,
                                onArticleClick = { viewModel.selectArticleForDetail(article) },
                                onBookmarkToggle = { viewModel.toggleBookmark(article.articleId, article.isBookmarked) }
                            )
                        }
                    }
                }
            }
        }

        // Haber Detayı Okuma BottomSheet
        uiState.selectedArticleForDetail?.let { article ->
            NewsDetailBottomSheet(
                article = article,
                onDismissRequest = { viewModel.selectArticleForDetail(null) }
            )
        }
    }
}
