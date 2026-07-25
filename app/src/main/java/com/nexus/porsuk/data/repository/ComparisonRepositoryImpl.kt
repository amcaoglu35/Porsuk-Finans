package com.nexus.porsuk.data.repository

import com.nexus.porsuk.data.engine.FundComparisonEngine
import com.nexus.porsuk.data.local.dao.FundIntelligenceDao
import com.nexus.porsuk.domain.model.FundComparison
import com.nexus.porsuk.domain.model.FundIntelligence
import com.nexus.porsuk.domain.repository.FundComparisonRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ComparisonRepositoryImpl @Inject constructor(
    private val fundDao: FundIntelligenceDao,
    private val comparisonEngine: FundComparisonEngine
) : FundComparisonRepository {

    override suspend fun compareFunds(baseCode: String, targetCode: String): FundComparison {
        val baseAlloc = fundDao.getFundAllocation(baseCode).first()?.toDomainModel()
        val targetAlloc = fundDao.getFundAllocation(targetCode).first()?.toDomainModel()
        
        if (baseAlloc == null || targetAlloc == null) {
            return FundComparison(baseCode, targetCode, 0.0, emptyList(), 0.0, emptyMap())
        }
        
        return comparisonEngine.compare(baseAlloc, targetAlloc)
    }

    override suspend fun analyzeOverlap(codes: List<String>): Map<String, Double> {
        return emptyMap() // TODO
    }

    override suspend fun findSimilarFunds(code: String): List<FundIntelligence> {
        return emptyList() // TODO
    }

    // Helper (Duplicate of repo logic to keep it independent but should ideally use a common mapper)
    private fun com.nexus.porsuk.data.local.entity.FundAllocationEntity.toDomainModel(): com.nexus.porsuk.domain.model.FundAllocation {
        val gson = com.google.gson.Gson()
        return com.nexus.porsuk.domain.model.FundAllocation(
            fundCode = fundCode,
            sectorAllocation = gson.fromJson(sectorJson, object : com.google.gson.reflect.TypeToken<Map<String, Double>>() {}.type),
            countryAllocation = gson.fromJson(countryJson, object : com.google.gson.reflect.TypeToken<Map<String, Double>>() {}.type),
            assetAllocation = gson.fromJson(assetJson, object : com.google.gson.reflect.TypeToken<Map<String, Double>>() {}.type),
            topHoldings = gson.fromJson(holdingsJson, object : com.google.gson.reflect.TypeToken<List<com.nexus.porsuk.domain.model.FundHolding>>() {}.type)
        )
    }
}
