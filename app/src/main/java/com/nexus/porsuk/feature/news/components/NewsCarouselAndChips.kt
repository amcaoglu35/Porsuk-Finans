package com.nexus.porsuk.feature.news.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nexus.porsuk.domain.model.NewsArticle
import com.nexus.porsuk.domain.model.NewsCategory

/**
 * Son Dakika Manşet Haber Carousel Bileşeni (BreakingNewsCarousel)
 */
@Composable
fun BreakingNewsCarousel(
    breakingNews: List<NewsArticle>,
    onArticleClick: (NewsArticle) -> Unit,
    modifier: Modifier = Modifier
) {
    if (breakingNews.isEmpty()) return

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "⚡ SON DAKİKA MANŞETLER",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = breakingNews,
                key = { it.articleId }
            ) { article ->
                Card(
                    modifier = Modifier
                        .width(280.dp)
                        .height(130.dp)
                        .clickable { onArticleClick(article) },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .padding(14.dp)
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.error
                        ) {
                            Text(
                                text = "SON DAKİKA",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onError,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Text(
                            text = article.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = article.source,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

/**
 * 13 Haber Kategorisi FilterChip Barı (NewsCategoryChipBar)
 */
@Composable
fun NewsCategoryChipBar(
    selectedCategory: NewsCategory,
    onCategorySelected: (NewsCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    ScrollableTabRow(
        selectedTabIndex = selectedCategory.ordinal,
        edgePadding = 16.dp,
        containerColor = MaterialTheme.colorScheme.surface,
        divider = {},
        modifier = modifier
    ) {
        NewsCategory.entries.forEach { cat ->
            FilterChip(
                selected = cat == selectedCategory,
                onClick = { onCategorySelected(cat) },
                label = { Text(cat.displayName) },
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}
