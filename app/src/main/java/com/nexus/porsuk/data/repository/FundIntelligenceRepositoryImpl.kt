package com.nexus.porsuk.data.repository

import com.nexus.porsuk.core.common.NetworkResult
import com.nexus.porsuk.data.local.dao.FundIntelligenceDao
import com.nexus.porsuk.data.local.entity.*
import com.nexus.porsuk.data.provider.FundIntelligenceProvider
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.FundIntelligenceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FundIntelligenceRepositoryImpl @Inject constructor(
    private val fundDao: FundIntelligenceDao,
    private val providers: List<@JvmSuppressWildcards FundIntelligenceProvider>
) : FundIntelligenceRepository {

    override fun getFundIntelligence(code: String): Flow<FundIntelligence?> {
        return fundDao.getFundIntelligence(code).map { it?.toDomainModel() }
    }

    override fun getPerformance(code: String): Flow<FundPerformance?> {
        return fundDao.getFundPerformance(code).map { it?.toDomainModel() }
    }

    override fun getAllocations(code: String): Flow<FundAllocation?> {
        return fundDao.getFundAllocation(code).map { it?.toDomainModel() }
    }

    override fun getRiskMetrics(code: String): Flow<FundRiskMetrics?> {
        return fundDao.getFundRisk(code).map { it?.toDomainModel() }
    }

    override fun searchFunds(query: String): Flow<List<FundIntelligence>> {
        return fundDao.searchFunds(query).map { list -> list.map { it.toDomainModel() } }
    }

    override fun getFundsByCategory(type: FundType): Flow<List<FundIntelligence>> {
        return fundDao.getFundsByType(type).map { list -> list.map { it.toDomainModel() } }
    }

    override suspend fun syncFundIntelligence(code: String): NetworkResult<Unit> {
        // Multi-provider strategy: try each provider until success
        for (provider in providers) {
            val result = provider.fetchFundIntelligence(code)
            if (result is NetworkResult.Success) {
                val intelligence = result.data
                val perf = provider.fetchPerformance(code)
                val alloc = provider.fetchAllocations(code)
                val risk = provider.fetchRiskMetrics(code)
                
                // Persistence logic
                fundDao.insertFullFundIntelligence(
                    intelligence.toEntity(),
                    (perf as? NetworkResult.Success)?.data?.toEntity() ?: FundPerformance(code).toEntity(),
                    (alloc as? NetworkResult.Success)?.data?.toEntity() ?: FundAllocation(code).toEntity(),
                    (risk as? NetworkResult.Success)?.data?.toEntity() ?: FundRiskMetrics(code).toEntity()
                )
                return NetworkResult.Success(Unit)
            }
        }
        return NetworkResult.Error(com.nexus.porsuk.core.common.DataError.Network.UNKNOWN_NETWORK_ERROR, "No provider could fetch data for $code")
    }

    // Mapper Extensions
    private fun FundIntelligenceEntity.toDomainModel() = FundIntelligence(
        code, isin, name, type, manager, inceptionDate, currency, benchmark, aum, expenseRatio, dividendYield, riskLevel, replication, description, lastUpdated
    )

    private fun FundPerformanceEntity.toDomainModel() = FundPerformance(
        fundCode, daily, weekly, monthly, ytd, yearly1, yearly3, yearly5, yearly10, sinceInception
    )
    
    private val gson = com.google.gson.Gson()

    private fun FundAllocationEntity.toDomainModel() = FundAllocation(
        fundCode = fundCode,
        sectorAllocation = gson.fromJson(sectorJson, object : com.google.gson.reflect.TypeToken<Map<String, Double>>() {}.type),
        countryAllocation = gson.fromJson(countryJson, object : com.google.gson.reflect.TypeToken<Map<String, Double>>() {}.type),
        assetAllocation = gson.fromJson(assetJson, object : com.google.gson.reflect.TypeToken<Map<String, Double>>() {}.type),
        topHoldings = gson.fromJson(holdingsJson, object : com.google.gson.reflect.TypeToken<List<FundHolding>>() {}.type)
    )

    private fun FundRiskEntity.toDomainModel() = FundRiskMetrics(
        fundCode, volatility, sharpe, sortino, beta, alpha, maxDrawdown, trackingError, trackingDifference
    )

    private fun FundIntelligence.toEntity() = FundIntelligenceEntity(
        code, isin, name, type, manager, inceptionDate, currency, benchmark, aum, expenseRatio, dividendYield, riskLevel, replication, description, lastUpdated
    )

    private fun FundPerformance.toEntity() = FundPerformanceEntity(
        fundCode, daily, weekly, monthly, ytd, yearly1, yearly3, yearly5, yearly10, sinceInception
    )

    private fun FundAllocation.toEntity() = FundAllocationEntity(
        fundCode = fundCode,
        sectorJson = gson.toJson(sectorAllocation),
        countryJson = gson.toJson(countryAllocation),
        assetJson = gson.toJson(assetAllocation),
        holdingsJson = gson.toJson(topHoldings)
    )

    private fun FundRiskMetrics.toEntity() = FundRiskEntity(
        fundCode, volatility, sharpeRatio, sortinoRatio, beta, alpha, maxDrawdown, trackingError, trackingDifference
    )
}
