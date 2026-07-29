package com.nexus.porsuk.data.repository

import com.nexus.porsuk.core.common.NetworkResult
import com.nexus.porsuk.data.local.SettingsManager
import com.nexus.porsuk.data.local.dao.NewsIntelligenceDao
import com.nexus.porsuk.data.local.entity.NewsArticleEntity
import com.nexus.porsuk.data.remote.GeminiService
import com.nexus.porsuk.data.remote.datasource.NewsRemoteDataSource
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsIntelligenceRepositoryImpl @Inject constructor(
    private val dao: NewsIntelligenceDao,
    private val remoteDataSource: NewsRemoteDataSource,
    private val settingsManager: SettingsManager
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

    override suspend fun refreshNewsByCategory(category: NewsCategory): Result<Unit> {
        val networkResult = if (category == NewsCategory.LATEST || category == NewsCategory.ALL) {
            remoteDataSource.fetchTopHeadlines("business")
        } else {
            remoteDataSource.fetchLatestNews(category.displayName)
        }

        return when (networkResult) {
            is NetworkResult.Success -> {
                val articles = networkResult.data.articles.map { dto ->
                    NewsArticleEntity(
                        articleId = UUID.randomUUID().toString(),
                        title = dto.title,
                        summary = dto.description ?: "",
                        content = dto.content ?: "",
                        source = dto.source.name,
                        category = category.name,
                        imageUrl = dto.urlToImage,
                        sourceUrl = dto.url,
                        publishedAt = parseDate(dto.publishedAt),
                        sentiment = "NEUTRAL"
                    )
                }
                
                val enrichedArticles = enrichWithAi(articles)
                dao.insertNewsArticles(enrichedArticles)
                Result.success(Unit)
            }
            is NetworkResult.Error -> Result.failure(Exception(networkResult.message))
            is NetworkResult.Exception -> Result.failure(networkResult.throwable)
            else -> Result.failure(Exception("Unknown network error"))
        }
    }

    override suspend fun refreshNewsBySymbol(symbol: String): Result<Unit> {
        val result = remoteDataSource.fetchLatestNews(symbol)
        return when (result) {
            is NetworkResult.Success -> {
                val entities = result.data.articles.map { dto ->
                    NewsArticleEntity(
                        articleId = UUID.randomUUID().toString(),
                        title = dto.title,
                        summary = dto.description ?: "",
                        content = dto.content ?: "",
                        source = dto.source.name,
                        category = NewsCategory.COMPANY.name,
                        symbol = symbol,
                        imageUrl = dto.urlToImage,
                        sourceUrl = dto.url,
                        publishedAt = parseDate(dto.publishedAt)
                    )
                }
                dao.insertNewsArticles(entities)
                Result.success(Unit)
            }
            else -> Result.failure(Exception("Failed to fetch news for $symbol"))
        }
    }

    private suspend fun enrichWithAi(articles: List<NewsArticleEntity>): List<NewsArticleEntity> {
        val apiKey = settingsManager.getGeminiApiKey()
        if (apiKey.isNullOrBlank()) return articles

        val gemini = GeminiService(apiKey)
        return articles.mapIndexed { index, article ->
            if (index < 3) {
                try {
                    val prompt = "Aşağıdaki haber metnini analiz et. Tek cümlelik bir AI özeti çıkar ve duyarlılığını (POSITIVE, NEGATIVE, NEUTRAL) belirle. Format: Özet | SENTIMENT\n\n${article.title}\n${article.summary}"
                    val response = gemini.generateRawContent(prompt)
                    val parts = response.split("|")
                    if (parts.size >= 2) {
                        article.copy(
                            aiSummary = parts[0].trim(),
                            sentiment = parts[1].trim().uppercase()
                        )
                    } else article
                } catch (_: Exception) {
                    article
                }
            } else article
        }
    }

    private fun parseDate(dateStr: String): Long {
        return try {
            java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.US).parse(dateStr)?.time ?: System.currentTimeMillis()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }
}

@Singleton
class NewsCategoryRepositoryImpl @Inject constructor(
    private val dao: NewsIntelligenceDao
) : NewsCategoryRepository {
    override fun getAllCategories(): Flow<List<NewsCategoryItem>> {
        return dao.getAllCategories().map { list ->
            list.map { entity ->
                NewsCategoryItem(
                    categoryId = entity.categoryId,
                    name = entity.name,
                    iconName = entity.icon
                )
            }
        }
    }
}

@Singleton
class NewsSourceRepositoryImpl @Inject constructor(
    private val dao: NewsIntelligenceDao
) : NewsSourceRepository {
    override fun getAllSources(): Flow<List<NewsSourceItem>> {
        return dao.getAllSources().map { list ->
            list.map { entity ->
                NewsSourceItem(
                    sourceId = entity.sourceId,
                    name = entity.name,
                    trustScore = entity.trustScore
                )
            }
        }
    }
}

private fun NewsArticleEntity.toDomainModel() = NewsArticle(
    articleId = articleId,
    title = title,
    summary = summary,
    aiSummary = aiSummary,
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
