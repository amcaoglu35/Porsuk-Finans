package com.nexus.porsuk.data.local.dao

import androidx.room.*
import com.nexus.porsuk.data.local.entity.*
import com.nexus.porsuk.domain.model.FundType
import kotlinx.coroutines.flow.Flow

@Dao
interface FundIntelligenceDao {

    @Query("SELECT * FROM fund_intelligence WHERE code = :code")
    fun getFundIntelligence(code: String): Flow<FundIntelligenceEntity?>

    @Query("SELECT * FROM fund_performance WHERE fund_code = :code")
    fun getFundPerformance(code: String): Flow<FundPerformanceEntity?>

    @Query("SELECT * FROM fund_allocations WHERE fund_code = :code")
    fun getFundAllocation(code: String): Flow<FundAllocationEntity?>

    @Query("SELECT * FROM fund_risk_metrics WHERE fund_code = :code")
    fun getFundRisk(code: String): Flow<FundRiskEntity?>

    @Query("SELECT * FROM fund_intelligence WHERE type = :type")
    fun getFundsByType(type: FundType): Flow<List<FundIntelligenceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFundIntelligence(entity: FundIntelligenceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFundPerformance(entity: FundPerformanceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFundAllocation(entity: FundAllocationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFundRisk(entity: FundRiskEntity)

    @Transaction
    suspend fun insertFullFundIntelligence(
        intelligence: FundIntelligenceEntity,
        performance: FundPerformanceEntity,
        allocation: FundAllocationEntity,
        risk: FundRiskEntity
    ) {
        insertFundIntelligence(intelligence)
        insertFundPerformance(performance)
        insertFundAllocation(allocation)
        insertFundRisk(risk)
    }

    @Query("SELECT * FROM fund_intelligence WHERE name LIKE '%' || :query || '%' OR code LIKE '%' || :query || '%'")
    fun searchFunds(query: String): Flow<List<FundIntelligenceEntity>>
}
