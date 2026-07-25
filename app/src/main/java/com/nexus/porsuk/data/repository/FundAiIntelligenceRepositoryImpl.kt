package com.nexus.porsuk.data.repository

import com.nexus.porsuk.data.local.dao.FundIntelligenceDao
import com.nexus.porsuk.data.remote.FundAiIntelligenceEngine
import com.nexus.porsuk.domain.model.FundIntelligenceAiSummary
import com.nexus.porsuk.domain.repository.FundAiIntelligenceRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FundAiIntelligenceRepositoryImpl @Inject constructor(
    private val fundDao: FundIntelligenceDao,
    private val aiEngine: FundAiIntelligenceEngine
) : FundAiIntelligenceRepository {

    override suspend fun getAiSummary(code: String): FundIntelligenceAiSummary {
        val intelligence = fundDao.getFundIntelligence(code).first()?.toDomainModel() ?: return emptySummary(code)
        val perf = fundDao.getFundPerformance(code).first()?.toDomainModel() ?: return emptySummary(code)
        val risk = fundDao.getFundRisk(code).first()?.toDomainModel() ?: return emptySummary(code)
        
        return aiEngine.generateFundReport(intelligence, perf, risk)
    }

    override suspend fun getProsAndCons(code: String): Pair<List<String>, List<String>> {
        val summary = getAiSummary(code)
        return summary.pros to summary.cons
    }

    private fun emptySummary(code: String) = FundIntelligenceAiSummary(
        code, "Yetersiz veri.", emptyList(), emptyList(), "", "", emptyList(), emptyList()
    )

    // Common Mapper
    private fun com.nexus.porsuk.data.local.entity.FundIntelligenceEntity.toDomainModel() = com.nexus.porsuk.domain.model.FundIntelligence(
        code, isin, name, type, manager, inceptionDate, currency, benchmark, aum, expenseRatio, dividendYield, riskLevel, replication, description, lastUpdated
    )
    private fun com.nexus.porsuk.data.local.entity.FundPerformanceEntity.toDomainModel() = com.nexus.porsuk.domain.model.FundPerformance(
        fundCode, daily, weekly, monthly, ytd, yearly1, yearly3, yearly5, yearly10, sinceInception
    )
    private fun com.nexus.porsuk.data.local.entity.FundRiskEntity.toDomainModel() = com.nexus.porsuk.domain.model.FundRiskMetrics(
        fundCode, volatility, sharpe, sortino, beta, alpha, maxDrawdown, trackingError, trackingDifference
    )
}
