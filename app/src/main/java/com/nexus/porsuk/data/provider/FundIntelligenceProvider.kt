package com.nexus.porsuk.data.provider

import com.nexus.porsuk.core.common.NetworkResult
import com.nexus.porsuk.domain.model.*

/**
 * Porsuk Fund Intelligence — Provider Contract
 */
interface FundIntelligenceProvider {
    val providerName: String
    
    suspend fun fetchFundIntelligence(code: String): NetworkResult<FundIntelligence>
    suspend fun fetchPerformance(code: String): NetworkResult<FundPerformance>
    suspend fun fetchAllocations(code: String): NetworkResult<FundAllocation>
    suspend fun fetchRiskMetrics(code: String): NetworkResult<FundRiskMetrics>
    
    suspend fun search(query: String): NetworkResult<List<FundIntelligence>>
}
