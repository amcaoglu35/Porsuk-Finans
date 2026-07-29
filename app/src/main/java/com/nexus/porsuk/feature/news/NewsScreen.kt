package com.nexus.porsuk.feature.news

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.nexus.porsuk.domain.model.NewsArticle
import com.nexus.porsuk.domain.model.NewsCategory
import com.nexus.porsuk.domain.model.NewsSentiment
import com.nexus.porsuk.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: NewsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Haber Merkezi", fontWeight = FontWeight.Bold, fontFamily = Manrope)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshNews() }) {
                        Icon(Icons.Default.Refresh, "Yenile")
                    }
                }
            )
        },
        containerColor = BackgroundNew
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Arama
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                placeholder = { Text("Haberlerde ara...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                shape = RoundedCornerShape(12.dp)
            )

            // Kategoriler
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(NewsCategory.entries) { category ->
                    FilterChip(
                        selected = uiState.selectedCategory == category,
                        onClick = { viewModel.selectCategory(category) },
                        label = { Text(category.displayName) },
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryTeal)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.allNews) { article ->
                        NewsCard(
                            article = article,
                            onBookmark = { viewModel.toggleBookmark(article.articleId, article.isBookmarked) },
                            onRead = { viewModel.markAsRead(article.articleId) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NewsCard(
    article: NewsArticle,
    onBookmark: () -> Unit,
    onRead: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = BorderStroke(1.dp, LineBorder)
    ) {
        Column {
            if (!article.imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = article.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SentimentBadge(sentiment = article.sentiment)
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = onBookmark) {
                        Icon(
                            if (article.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            null,
                            tint = if (article.isBookmarked) PrimaryTeal else SubText
                        )
                    }
                }
                
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (article.isRead) SubText else InkText
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = article.summary,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    color = SubText
                )

                if (!article.aiSummary.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(TealSoft.copy(alpha = 0.3f))
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AutoAwesome, null, tint = PrimaryTeal, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("AI Özeti", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryTeal)
                            }
                            Text(article.aiSummary, fontSize = 11.sp, color = InkText)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(article.source, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PrimaryTeal)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("•", fontSize = 10.sp, color = SubText)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date(article.publishedAt)),
                        fontSize = 10.sp,
                        color = SubText
                    )
                }
            }
        }
    }
}

@Composable
fun SentimentBadge(sentiment: NewsSentiment) {
    val color = when (sentiment) {
        NewsSentiment.POSITIVE -> EmeraldNew
        NewsSentiment.NEGATIVE -> NegatifRed
        else -> SubText
    }
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Text(
            sentiment.displayName,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}
