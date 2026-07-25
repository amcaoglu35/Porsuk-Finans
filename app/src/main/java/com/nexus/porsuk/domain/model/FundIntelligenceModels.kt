package com.nexus.porsuk.domain.model

import java.util.Date

/**
 * Porsuk Finans — Professional ETF & Fund Intelligence Domain Models
 * Designed to Morningstar Direct standards.
 */

enum class FundType {
    ETF, MUTUAL_FUND, INDEX_FUND, BOND_FUND, COMMODITY_FUND, 
    MONEY_MARKET, BALANCED_FUND, SECTOR_FUND, INTERNATIONAL_FUND, CRYPTO_FUND
}

enum class ReplicationMethod {
    PHYSICAL, SYNTHETIC, SAMPLING
}

data class FundIntelligence(
    val code: String,
    val isin: String,
    val name: String,
    val type: FundType,
    val manager: String,
    val inceptionDate: Long?,
    val currency: String,
    val benchmark: String?,
    val aum: Double, // Assets Under Management
    val expenseRatio: Double,
    val dividendYield: Double?,
    val riskLevel: Int, // 1-7
    val replication: ReplicationMethod?,
    val description: String?,
    val lastUpdated: Long = System.currentTimeMillis()
)

data class FundPerformance(
    val fundCode: String,
    val daily: Double = 0.0,
    val weekly: Double = 0.0,
    val monthly: Double = 0.0,
    val ytd: Double = 0.0,
    val yearly1: Double = 0.0,
    val yearly3: Double = 0.0,
    val yearly5: Double = 0.0,
    val yearly10: Double = 0.0,
    val sinceInception: Double = 0.0
)

data class FundHolding(
    val symbol: String,
    val name: String,
    val weight: Double,
    val marketValue: Double? = null,
    val shares: Double? = null
)

data class FundAllocation(
    val fundCode: String,
    val sectorAllocation: Map<String, Double> = emptyMap(),
    val countryAllocation: Map<String, Double> = emptyMap(),
    val assetAllocation: Map<String, Double> = emptyMap(), // Stocks, Bonds, Cash, etc.
    val topHoldings: List<FundHolding> = emptyList()
)

data class FundRiskMetrics(
    val fundCode: String,
    val volatility: Double = 0.0,
    val sharpeRatio: Double = 0.0,
    val sortinoRatio: Double = 0.0,
    val beta: Double = 0.0,
    val alpha: Double = 0.0,
    val maxDrawdown: Double = 0.0,
    val trackingError: Double = 0.0,
    val trackingDifference: Double = 0.0,
    val informationRatio: Double = 0.0
)

data class FundComparison(
    val baseFundCode: String,
    val targetFundCode: String,
    val overlapPercentage: Double,
    val commonHoldings: List<String>,
    val feeDifference: Double,
    val performanceDifference: Map<String, Double>
)

data class FundIntelligenceAiSummary(
    val fundCode: String,
    val summary: String,
    val pros: List<String>,
    val cons: List<String>,
    val riskAssessment: String,
    val suitableFor: String, // Dynamic investor profile
    val similarFunds: List<String>,
    val alternativeFunds: List<String>
)

data class FundScreenerCriteria(
    val categories: List<FundType>? = null,
    val regions: List<String>? = null,
    val maxExpenseRatio: Double? = null,
    val minPerformance: Double? = null,
    val minDividendYield: Double? = null,
    val minRiskLevel: Int? = null,
    val maxRiskLevel: Int? = null,
    val esgScore: Double? = null,
    val currency: String? = null
)
