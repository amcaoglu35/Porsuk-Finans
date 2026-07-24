package com.nexus.porsuk.data.local.dao

import androidx.room.*
import com.nexus.porsuk.data.local.entity.MasterScoreHistoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * Porsuk Master Score Engine — Room DAO Sorguları
 */
@Dao
interface MasterScoreDao {

    @Query("SELECT * FROM engine_master_score_history WHERE symbol = :symbol ORDER BY timestamp DESC")
    fun getScoreHistoryForSymbol(symbol: String): Flow<List<MasterScoreHistoryEntity>>

    @Query("SELECT * FROM engine_master_score_history WHERE symbol = :symbol ORDER BY timestamp DESC LIMIT 1")
    fun getLatestScoreForSymbol(symbol: String): Flow<MasterScoreHistoryEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScoreHistory(score: MasterScoreHistoryEntity)

    @Query("DELETE FROM engine_master_score_history WHERE symbol = :symbol")
    suspend fun deleteScoreHistoryForSymbol(symbol: String)
}
