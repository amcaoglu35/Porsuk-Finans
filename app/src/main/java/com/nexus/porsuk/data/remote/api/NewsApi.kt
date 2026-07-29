package com.nexus.porsuk.data.remote.api

import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {
    @GET("v2/everything")
    suspend fun getNews(
        @Query("q") query: String,
        @Query("sortBy") sortBy: String = "publishedAt",
        @Query("language") language: String = "en"
    ): NewsResponseDto

    @GET("v2/top-headlines")
    suspend fun getTopHeadlines(
        @Query("category") category: String? = null,
        @Query("country") country: String = "us",
        @Query("language") language: String = "en"
    ): NewsResponseDto
}

data class NewsResponseDto(
    val status: String,
    val totalResults: Int,
    val articles: List<NewsArticleDto>
)

data class NewsArticleDto(
    val source: NewsSourceDto,
    val author: String?,
    val title: String,
    val description: String?,
    val url: String,
    val urlToImage: String?,
    val publishedAt: String,
    val content: String?
)

data class NewsSourceDto(
    val id: String?,
    val name: String
)
