package com.nexus.porsuk.data.remote.datasource

import com.nexus.porsuk.core.common.NetworkResult
import com.nexus.porsuk.core.network.BaseRemoteDataSource
import com.nexus.porsuk.core.network.ErrorHandler
import com.nexus.porsuk.data.local.entity.NewsEntity
import com.nexus.porsuk.data.remote.api.NewsApi
import com.nexus.porsuk.data.remote.api.NewsResponseDto
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Porsuk Data Center — Haberler ve KAP Duyuruları Uzak Veri Kaynağı Arayüzü
 */
interface NewsRemoteDataSource {
    suspend fun fetchLatestNews(query: String): NetworkResult<NewsResponseDto>
    suspend fun fetchTopHeadlines(category: String? = null): NetworkResult<NewsResponseDto>
}

@Singleton
class NewsRemoteDataSourceImpl @Inject constructor(
    private val newsApi: NewsApi,
    errorHandler: ErrorHandler
) : BaseRemoteDataSource(errorHandler), NewsRemoteDataSource {
    
    override suspend fun fetchLatestNews(query: String): NetworkResult<NewsResponseDto> {
        return safeApiCall { newsApi.getNews(query) }
    }

    override suspend fun fetchTopHeadlines(category: String?): NetworkResult<NewsResponseDto> {
        return safeApiCall { newsApi.getTopHeadlines(category = category) }
    }
}

/**
 * Makroekonomik Takvim Veri Modeli
 */
data class EconomicEventDto(
    val eventId: String,
    val title: String,
    val country: String, // TR, US, EU
    val impactLevel: String, // HIGH, MEDIUM, LOW
    val actual: String? = null,
    val forecast: String? = null,
    val previous: String? = null,
    val eventTimeMs: Long
)

/**
 * Porsuk Data Center — Ekonomik Takvim Uzak Veri Kaynağı Arayüzü
 */
interface EconomicCalendarRemoteDataSource {
    suspend fun fetchUpcomingEvents(): NetworkResult<List<EconomicEventDto>>
}

@Singleton
class EconomicCalendarRemoteDataSourceImpl @Inject constructor() : EconomicCalendarRemoteDataSource {
    override suspend fun fetchUpcomingEvents(): NetworkResult<List<EconomicEventDto>> {
        return NetworkResult.Success(emptyList())
    }
}
