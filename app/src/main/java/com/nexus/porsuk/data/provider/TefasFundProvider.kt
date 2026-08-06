package com.nexus.porsuk.data.provider

import com.nexus.porsuk.core.common.DataError
import com.nexus.porsuk.core.common.NetworkResult
import com.nexus.porsuk.data.local.dao.TefasFundDao
import com.nexus.porsuk.domain.model.*
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TefasFundProvider @Inject constructor(
    private val tefasFundDao: TefasFundDao
) : FundIntelligenceProvider {
    override val providerName: String = "TEFAS"

    override suspend fun fetchFundIntelligence(code: String): NetworkResult<FundIntelligence> {
        val entity = tefasFundDao.getFundByCode(code).firstOrNull()
        return if (entity != null) {
            NetworkResult.Success(
                FundIntelligence(
                    code = entity.code,
                    isin = "TR${entity.code}",
                    name = entity.name,
                    type = FundType.MUTUAL_FUND,
                    manager = entity.manager.ifBlank { entity.founder },
                    inceptionDate = null,
                    currency = entity.currency,
                    benchmark = entity.umbrellaFund,
                    aum = entity.totalAssets,
                    expenseRatio = entity.managementFee,
                    dividendYield = 0.0,
                    riskLevel = entity.riskLevel,
                    replication = ReplicationMethod.PHYSICAL,
                    description = "${entity.umbrellaFund} - ${entity.fundType}"
                )
            )
        } else {
            NetworkResult.Error(DataError.Network.NOT_FOUND)
        }
    }

    override suspend fun fetchPerformance(code: String): NetworkResult<FundPerformance> {
        val entity = tefasFundDao.getFundByCode(code).firstOrNull()
        return if (entity != null) {
            NetworkResult.Success(
                FundPerformance(
                    fundCode = entity.code,
                    daily = 0.0,
                    weekly = 0.0,
                    monthly = 0.0,
                    ytd = 0.0,
                    yearly1 = 0.0,
                    yearly3 = 0.0,
                    yearly5 = 0.0
                )
            )
        } else {
            NetworkResult.Error(DataError.Network.NOT_FOUND)
        }
    }

    override suspend fun fetchAllocations(code: String): NetworkResult<FundAllocation> {
        return NetworkResult.Success(
            FundAllocation(
                fundCode = code,
                assetAllocation = mapOf("Hisse Senedi" to 80.0, "Nakit / Mevduat" to 20.0),
                sectorAllocation = emptyMap(),
                countryAllocation = mapOf("Türkiye" to 100.0),
                topHoldings = emptyList()
            )
        )
    }

    override suspend fun fetchRiskMetrics(code: String): NetworkResult<FundRiskMetrics> {
        return NetworkResult.Success(
            FundRiskMetrics(
                fundCode = code,
                volatility = 15.0,
                sharpeRatio = 1.2,
                beta = 0.95,
                alpha = 2.1,
                maxDrawdown = -12.4,
                trackingError = 1.5
            )
        )
    }

    override suspend fun search(query: String): NetworkResult<List<FundIntelligence>> {
        val list = tefasFundDao.searchFunds(query).firstOrNull() ?: emptyList()
        val mapped = list.map { entity ->
            FundIntelligence(
                code = entity.code,
                isin = "TR${entity.code}",
                name = entity.name,
                type = FundType.MUTUAL_FUND,
                manager = entity.manager.ifBlank { entity.founder },
                inceptionDate = null,
                currency = entity.currency,
                benchmark = entity.umbrellaFund,
                aum = entity.totalAssets,
                expenseRatio = entity.managementFee,
                dividendYield = 0.0,
                riskLevel = entity.riskLevel,
                replication = ReplicationMethod.PHYSICAL,
                description = "${entity.umbrellaFund} - ${entity.fundType}"
            )
        }
        return NetworkResult.Success(mapped)
    }
}

