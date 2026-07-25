package com.nexus.porsuk.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nexus.porsuk.domain.model.FundType
import com.nexus.porsuk.domain.model.ReplicationMethod

/**
 * Porsuk Finans — Professional Fund Intelligence Persistence Entities
 */

@Entity(
    tableName = "fund_intelligence",
    indices = [Index(value = ["code"], unique = true), Index(value = ["type"])]
)
data class FundIntelligenceEntity(
    @PrimaryKey val code: String,
    val isin: String,
    val name: String,
    val type: FundType,
    val manager: String,
    @ColumnInfo(name = "inception_date") val inceptionDate: Long?,
    val currency: String,
    val benchmark: String?,
    val aum: Double,
    @ColumnInfo(name = "expense_ratio") val expenseRatio: Double,
    @ColumnInfo(name = "dividend_yield") val dividendYield: Double?,
    @ColumnInfo(name = "risk_level") val riskLevel: Int,
    val replication: ReplicationMethod?,
    val description: String?,
    @ColumnInfo(name = "last_updated") val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "fund_performance",
    indices = [Index(value = ["fund_code"], unique = true)]
)
data class FundPerformanceEntity(
    @PrimaryKey @ColumnInfo(name = "fund_code") val fundCode: String,
    val daily: Double,
    val weekly: Double,
    val monthly: Double,
    val ytd: Double,
    val yearly1: Double,
    val yearly3: Double,
    val yearly5: Double,
    val yearly10: Double,
    @ColumnInfo(name = "since_inception") val sinceInception: Double
)

@Entity(
    tableName = "fund_allocations",
    indices = [Index(value = ["fund_code"], unique = true)]
)
data class FundAllocationEntity(
    @PrimaryKey @ColumnInfo(name = "fund_code") val fundCode: String,
    @ColumnInfo(name = "sector_json") val sectorJson: String, // Map serialized
    @ColumnInfo(name = "country_json") val countryJson: String, // Map serialized
    @ColumnInfo(name = "asset_json") val assetJson: String, // Map serialized
    @ColumnInfo(name = "holdings_json") val holdingsJson: String // List serialized
)

@Entity(
    tableName = "fund_risk_metrics",
    indices = [Index(value = ["fund_code"], unique = true)]
)
data class FundRiskEntity(
    @PrimaryKey @ColumnInfo(name = "fund_code") val fundCode: String,
    val volatility: Double,
    val sharpe: Double,
    val sortino: Double,
    val beta: Double,
    val alpha: Double,
    @ColumnInfo(name = "max_drawdown") val maxDrawdown: Double,
    @ColumnInfo(name = "tracking_error") val trackingError: Double,
    @ColumnInfo(name = "tracking_difference") val trackingDifference: Double
)
