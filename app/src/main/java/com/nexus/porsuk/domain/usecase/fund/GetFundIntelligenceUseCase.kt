package com.nexus.porsuk.domain.usecase.fund

import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.FundIntelligenceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetFundIntelligenceUseCase @Inject constructor(
    private val repository: FundIntelligenceRepository
) {
    operator fun invoke(code: String): Flow<FundFullIntelligence> {
        return combine(
            repository.getFundIntelligence(code),
            repository.getPerformance(code),
            repository.getAllocations(code),
            repository.getRiskMetrics(code)
        ) { info, perf, alloc, risk ->
            FundFullIntelligence(info, perf, alloc, risk)
        }
    }
}

data class FundFullIntelligence(
    val info: FundIntelligence?,
    val performance: FundPerformance?,
    val allocation: FundAllocation?,
    val riskMetrics: FundRiskMetrics?
)
