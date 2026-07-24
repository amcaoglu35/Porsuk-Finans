package com.nexus.porsuk.data.local.dao

import androidx.room.*
import com.nexus.porsuk.data.local.entity.AiWorkspaceEntity
import kotlinx.coroutines.flow.Flow

/**
 * Porsuk AI Lab Platform — Room DAO Sorguları
 */
@Dao
interface AiWorkspaceDao {

    @Query("SELECT * FROM engine_ai_workspace_records ORDER BY created_at DESC")
    fun getAllSavedRecords(): Flow<List<AiWorkspaceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: AiWorkspaceEntity)

    @Query("DELETE FROM engine_ai_workspace_records WHERE record_id = :recordId")
    suspend fun deleteRecord(recordId: String)
}
