package com.nexus.porsuk.data.provider

import com.nexus.porsuk.core.common.DataError
import com.nexus.porsuk.core.common.NetworkResult
import com.nexus.porsuk.domain.model.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TefasFundProvider @Inject constructor() : FundIntelligenceProvider {
    override val providerName: String = "TEFAS"

    override suspend fun fetchFundIntelligence(code: String): NetworkResult<FundIntelligence> {
        // Implementation logic for TEFAS scraping or API
        return NetworkResult.Error(DataError.Network.UNKNOWN_NETWORK_ERROR)
    }

    override suspend fun fetchPerformance(code: String): NetworkResult<FundPerformance> {
        return NetworkResult.Error(DataError.Network.UNKNOWN_NETWORK_ERROR)
    }

    override suspend fun fetchAllocations(code: String): NetworkResult<FundAllocation> {
        return NetworkResult.Error(DataError.Network.UNKNOWN_NETWORK_ERROR)
    }

    override suspend fun fetchRiskMetrics(code: String): NetworkResult<FundRiskMetrics> {
        return NetworkResult.Error(DataError.Network.UNKNOWN_NETWORK_ERROR)
    }

    override suspend fun search(query: String): NetworkResult<List<FundIntelligence>> {
        return NetworkResult.Success(emptyList())
    }
}
