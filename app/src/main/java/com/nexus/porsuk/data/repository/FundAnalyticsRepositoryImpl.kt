package com.nexus.porsuk.data.repository

import com.nexus.porsuk.domain.repository.FundAnalyticsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FundAnalyticsRepositoryImpl @Inject constructor() : FundAnalyticsRepository {
    override suspend fun calculateTrackingError(code: String, benchmarkCode: String): Double {
        return 0.0 // Advanced math here
    }

    override suspend fun calculateAUMTrend(code: String): List<Pair<Long, Double>> {
        return emptyList()
    }
}
