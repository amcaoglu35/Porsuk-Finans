package com.nexus.porsuk.data.local.dao

import androidx.room.*
import com.nexus.porsuk.data.local.entity.StrategyEntity
import kotlinx.coroutines.flow.Flow

/**
 * Porsuk Strategy Builder Pro — Room DAO Sorguları
 */
@Dao
interface StrategyDao {

    @Query("SELECT * FROM engine_user_strategies ORDER BY created_at DESC")
    fun getAllSavedStrategies(): Flow<List<StrategyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStrategy(strategy: StrategyEntity)

    @Query("DELETE FROM engine_user_strategies WHERE strategy_id = :strategyId")
    suspend fun deleteStrategy(strategyId: String)
}
