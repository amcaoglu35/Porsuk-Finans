package com.nexus.porsuk.domain.model

/**
 * Porsuk News Intelligence Center — Haber Makalesi Domain Modeli
 */
data class NewsArticle(
    val articleId: String,
    val title: String,
    val summary: String,
    val content: String,
    val source: String,
    val category: NewsCategory,
    val symbol: String? = null,
    val sector: String? = null,
    val imageUrl: String? = null,
    val sourceUrl: String? = null,
    val publishedAt: Long = System.currentTimeMillis(),
    val isBreaking: Boolean = false,
    val isBookmarked: Boolean = false,
    val isRead: Boolean = false,
    val sentiment: NewsSentiment = NewsSentiment.NEUTRAL,
    val impactScore: Int = 7 // 1 - 10 Orakul AI Etki Skoru
)

/**
 * Haber Kategorisi Domain Modeli
 */
data class NewsCategoryItem(
    val categoryId: String,
    val name: String,
    val iconName: String
)

/**
 * Haber Kaynağı Domain Modeli
 */
data class NewsSourceItem(
    val sourceId: String,
    val name: String,
    val trustScore: Double = 9.5
)

/**
 * Geleceğe Hazır Orakul AI Haber Yorumu & Trend Analizi Stub Modeli
 */
data class AiNewsAnalysisStub(
    val articleId: String,
    val aiSummary: String = "Orakul AI Özet: Bu haber ilgili hissenin orta vadeli bilançosunu olumlu etkileyebilir.",
    val marketImpactRating: String = "Yüksek Etki Beklentisi",
    val relatedSymbols: List<String> = listOf("THYAO.IS", "GARAN.IS")
)
