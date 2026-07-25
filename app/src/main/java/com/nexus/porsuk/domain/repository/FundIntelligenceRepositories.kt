package com.nexus.porsuk.domain.repository

import com.nexus.porsuk.core.common.NetworkResult
import com.nexus.porsuk.domain.model.*
import kotlinx.coroutines.flow.Flow

interface FundIntelligenceRepository {
    fun getFundIntelligence(code: String): Flow<FundIntelligence?>
    fun getPerformance(code: String): Flow<FundPerformance?>
    fun getAllocations(code: String): Flow<FundAllocation?>
    fun getRiskMetrics(code: String): Flow<FundRiskMetrics?>
    
    fun searchFunds(query: String): Flow<List<FundIntelligence>>
    fun getFundsByCategory(type: FundType): Flow<List<FundIntelligence>>
    
    suspend fun syncFundIntelligence(code: String): NetworkResult<Unit>
}

interface FundComparisonRepository {
    suspend fun compareFunds(baseCode: String, targetCode: String): FundComparison
    suspend fun analyzeOverlap(codes: List<String>): Map<String, Double>
    suspend fun findSimilarFunds(code: String): List<FundIntelligence>
}

interface FundAnalyticsRepository {
    suspend fun calculateTrackingError(code: String, benchmarkCode: String): Double
    suspend fun calculateAUMTrend(code: String): List<Pair<Long, Double>>
}

interface FundAiIntelligenceRepository {
    suspend fun getAiSummary(code: String): FundIntelligenceAiSummary
    suspend fun getProsAndCons(code: String): Pair<List<String>, List<String>>
}
