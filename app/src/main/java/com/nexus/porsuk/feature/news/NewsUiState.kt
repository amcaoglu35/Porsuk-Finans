package com.nexus.porsuk.feature.news

import com.nexus.porsuk.domain.model.NewsArticle
import com.nexus.porsuk.domain.model.NewsCategory

/**
 * Porsuk News Intelligence Center — UI Ekran Durumu (NewsUiState)
 */
data class NewsUiState(
    val breakingNews: List<NewsArticle> = emptyList(),
    val allNews: List<NewsArticle> = emptyList(),
    val filteredNews: List<NewsArticle> = emptyList(),
    val selectedCategory: NewsCategory = NewsCategory.ALL,
    val searchQuery: String = "",
    val selectedArticleForDetail: NewsArticle? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
