package com.nexus.porsuk.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Porsuk News Intelligence Center — Haber Makaleleri Tablosu (NewsArticleEntity)
 */
@Entity(
    tableName = "engine_news_articles",
    indices = [
        Index(value = ["article_id"], unique = true),
        Index(value = ["category"]),
        Index(value = ["symbol"]),
        Index(value = ["is_breaking"]),
        Index(value = ["is_bookmarked"])
    ]
)
data class NewsArticleEntity(
    @PrimaryKey
    @ColumnInfo(name = "article_id")
    val articleId: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "summary")
    val summary: String,

    @ColumnInfo(name = "ai_summary")
    val aiSummary: String? = null,

    @ColumnInfo(name = "content")
    val content: String,

    @ColumnInfo(name = "source")
    val source: String,

    @ColumnInfo(name = "category")
    val category: String, // COMPANY, KAP, TURKEY_ECONOMY, FED vb.

    @ColumnInfo(name = "symbol")
    val symbol: String? = null,

    @ColumnInfo(name = "sector")
    val sector: String? = null,

    @ColumnInfo(name = "image_url")
    val imageUrl: String? = null,

    @ColumnInfo(name = "source_url")
    val sourceUrl: String? = null,

    @ColumnInfo(name = "published_at")
    val publishedAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "is_breaking")
    val isBreaking: Boolean = false,

    @ColumnInfo(name = "is_bookmarked")
    val isBookmarked: Boolean = false,

    @ColumnInfo(name = "is_read")
    val isRead: Boolean = false,

    @ColumnInfo(name = "sentiment")
    val sentiment: String = "NEUTRAL", // POSITIVE, NEUTRAL, NEGATIVE

    @ColumnInfo(name = "impact_score")
    val impactScore: Int = 7
)

/**
 * Haber Kategorileri Tablosu (NewsCategoryEntity)
 */
@Entity(
    tableName = "engine_news_categories",
    indices = [Index(value = ["category_id"], unique = true)]
)
data class NewsCategoryEntity(
    @PrimaryKey
    @ColumnInfo(name = "category_id")
    val categoryId: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "icon")
    val icon: String
)

/**
 * Haber Kaynakları Tablosu (NewsSourceEntity)
 */
@Entity(
    tableName = "engine_news_sources",
    indices = [Index(value = ["source_id"], unique = true)]
)
data class NewsSourceEntity(
    @PrimaryKey
    @ColumnInfo(name = "source_id")
    val sourceId: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "trust_score")
    val trustScore: Double = 9.5
)
