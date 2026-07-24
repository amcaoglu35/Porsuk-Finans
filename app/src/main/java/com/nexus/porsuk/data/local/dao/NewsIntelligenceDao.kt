package com.nexus.porsuk.data.local.dao

import androidx.room.*
import com.nexus.porsuk.data.local.entity.NewsArticleEntity
import com.nexus.porsuk.data.local.entity.NewsCategoryEntity
import com.nexus.porsuk.data.local.entity.NewsSourceEntity
import kotlinx.coroutines.flow.Flow

/**
 * Porsuk News Intelligence Center — Room DAO Sorguları
 */
@Dao
interface NewsIntelligenceDao {

    // Haber Makalesi Sorguları
    @Query("SELECT * FROM engine_news_articles ORDER BY published_at DESC")
    fun getAllNewsArticles(): Flow<List<NewsArticleEntity>>

    @Query("SELECT * FROM engine_news_articles WHERE is_breaking = 1 ORDER BY published_at DESC LIMIT 5")
    fun getBreakingNews(): Flow<List<NewsArticleEntity>>

    @Query("SELECT * FROM engine_news_articles WHERE category = :category ORDER BY published_at DESC")
    fun getNewsByCategory(category: String): Flow<List<NewsArticleEntity>>

    @Query("SELECT * FROM engine_news_articles WHERE symbol = :symbol ORDER BY published_at DESC")
    fun getNewsBySymbol(symbol: String): Flow<List<NewsArticleEntity>>

    @Query("SELECT * FROM engine_news_articles WHERE is_bookmarked = 1 ORDER BY published_at DESC")
    fun getBookmarkedNews(): Flow<List<NewsArticleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewsArticle(article: NewsArticleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewsArticles(articles: List<NewsArticleEntity>)

    @Query("UPDATE engine_news_articles SET is_bookmarked = :isBookmarked WHERE article_id = :articleId")
    suspend fun setNewsBookmark(articleId: String, isBookmarked: Boolean)

    @Query("UPDATE engine_news_articles SET is_read = 1 WHERE article_id = :articleId")
    suspend fun markNewsAsRead(articleId: String)

    // Kategori ve Kaynak Sorguları
    @Query("SELECT * FROM engine_news_categories")
    fun getAllCategories(): Flow<List<NewsCategoryEntity>>

    @Query("SELECT * FROM engine_news_sources")
    fun getAllSources(): Flow<List<NewsSourceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: NewsCategoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSource(source: NewsSourceEntity)
}
