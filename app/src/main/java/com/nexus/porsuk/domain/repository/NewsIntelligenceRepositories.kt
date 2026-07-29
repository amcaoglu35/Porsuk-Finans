package com.nexus.porsuk.domain.repository

import com.nexus.porsuk.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * 1. Haber Akışı Deposu Sözleşmesi (NewsIntelligenceRepository)
 */
interface NewsIntelligenceRepository {
    fun getAllNews(): Flow<List<NewsArticle>>
    fun getBreakingNews(): Flow<List<NewsArticle>>
    fun getNewsByCategory(category: NewsCategory): Flow<List<NewsArticle>>
    fun getNewsBySymbol(symbol: String): Flow<List<NewsArticle>>
    fun getBookmarkedNews(): Flow<List<NewsArticle>>
    suspend fun setBookmark(articleId: String, isBookmarked: Boolean)
    suspend fun markAsRead(articleId: String)
    suspend fun refreshNewsByCategory(category: NewsCategory): Result<Unit>
    suspend fun refreshNewsBySymbol(symbol: String): Result<Unit>
}

/**
 * 2. Haber Kategorileri Deposu Sözleşmesi (NewsCategoryRepository)
 */
interface NewsCategoryRepository {
    fun getAllCategories(): Flow<List<NewsCategoryItem>>
}

/**
 * 3. Haber Kaynakları Deposu Sözleşmesi (NewsSourceRepository)
 */
interface NewsSourceRepository {
    fun getAllSources(): Flow<List<NewsSourceItem>>
}
