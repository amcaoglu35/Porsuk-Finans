package com.nexus.porsuk.data.remote.datasource

import com.nexus.porsuk.core.common.NetworkResult
import com.nexus.porsuk.data.local.entity.NewsEntity
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Porsuk Data Center — Haberler ve KAP Duyuruları Uzak Veri Kaynağı Arayüzü
 */
interface NewsRemoteDataSource {
    suspend fun fetchLatestNews(): NetworkResult<List<NewsEntity>>
    suspend fun fetchNewsForSymbol(symbol: String): NetworkResult<List<NewsEntity>>
}

@Singleton
class NewsRemoteDataSourceImpl @Inject constructor() : NewsRemoteDataSource {
    override suspend fun fetchLatestNews(): NetworkResult<List<NewsEntity>> {
        return NetworkResult.Success(emptyList())
    }

    override suspend fun fetchNewsForSymbol(symbol: String): NetworkResult<List<NewsEntity>> {
        return NetworkResult.Success(emptyList())
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
