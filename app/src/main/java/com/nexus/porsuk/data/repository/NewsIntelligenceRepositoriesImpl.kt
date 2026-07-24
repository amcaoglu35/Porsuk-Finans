package com.nexus.porsuk.data.repository

import com.nexus.porsuk.data.local.dao.NewsIntelligenceDao
import com.nexus.porsuk.data.local.entity.NewsArticleEntity
import com.nexus.porsuk.data.local.entity.NewsCategoryEntity
import com.nexus.porsuk.data.local.entity.NewsSourceEntity
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsIntelligenceRepositoryImpl @Inject constructor(
    private val dao: NewsIntelligenceDao
) : NewsIntelligenceRepository {

    override fun getAllNews(): Flow<List<NewsArticle>> {
        return dao.getAllNewsArticles().map { list -> list.map { it.toDomainModel() } }
    }

    override fun getBreakingNews(): Flow<List<NewsArticle>> {
        return dao.getBreakingNews().map { list -> list.map { it.toDomainModel() } }
    }

    override fun getNewsByCategory(category: NewsCategory): Flow<List<NewsArticle>> {
        return if (category == NewsCategory.ALL) {
            getAllNews()
        } else {
            dao.getNewsByCategory(category.name).map { list -> list.map { it.toDomainModel() } }
        }
    }

    override fun getNewsBySymbol(symbol: String): Flow<List<NewsArticle>> {
        return dao.getNewsBySymbol(symbol).map { list -> list.map { it.toDomainModel() } }
    }

    override fun getBookmarkedNews(): Flow<List<NewsArticle>> {
        return dao.getBookmarkedNews().map { list -> list.map { it.toDomainModel() } }
    }

    override suspend fun setBookmark(articleId: String, isBookmarked: Boolean) {
        dao.setNewsBookmark(articleId, isBookmarked)
    }

    override suspend fun markAsRead(articleId: String) {
        dao.markNewsAsRead(articleId)
    }
}

@Singleton
class NewsCategoryRepositoryImpl @Inject constructor(
    private val dao: NewsIntelligenceDao
) : NewsCategoryRepository {

    override fun getAllCategories(): Flow<List<NewsCategoryItem>> {
        return dao.getAllCategories().map { list ->
            list.map { NewsCategoryItem(it.categoryId, it.name, it.icon) }
        }
    }
}

@Singleton
class NewsSourceRepositoryImpl @Inject constructor(
    private val dao: NewsIntelligenceDao
) : NewsSourceRepository {

    override fun getAllSources(): Flow<List<NewsSourceItem>> {
        return dao.getAllSources().map { list ->
            list.map { NewsSourceItem(it.sourceId, it.name, it.trustScore) }
        }
    }
}

// Mappers
private fun NewsArticleEntity.toDomainModel() = NewsArticle(
    articleId = articleId,
    title = title,
    summary = summary,
    content = content,
    source = source,
    category = NewsCategory.fromString(category),
    symbol = symbol,
    sector = sector,
    imageUrl = imageUrl,
    sourceUrl = sourceUrl,
    publishedAt = publishedAt,
    isBreaking = isBreaking,
    isBookmarked = isBookmarked,
    isRead = isRead,
    sentiment = try { NewsSentiment.valueOf(sentiment) } catch (e: Exception) { NewsSentiment.NEUTRAL },
    impactScore = impactScore
)
