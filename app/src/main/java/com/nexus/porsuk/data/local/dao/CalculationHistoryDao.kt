package com.nexus.porsuk.data.local.dao

import androidx.room.*
import com.nexus.porsuk.data.local.entity.CalculationHistoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * Porsuk Financial Calculators Center — Room DAO Sorguları
 */
@Dao
interface CalculationHistoryDao {

    @Query("SELECT * FROM engine_calculator_history ORDER BY created_at DESC")
    fun getAllCalculationHistory(): Flow<List<CalculationHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(item: CalculationHistoryEntity)

    @Query("DELETE FROM engine_calculator_history WHERE history_id = :historyId")
    suspend fun deleteHistory(historyId: Long)
}
