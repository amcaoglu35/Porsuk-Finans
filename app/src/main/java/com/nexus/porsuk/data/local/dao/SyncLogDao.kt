package com.nexus.porsuk.data.local.dao

import androidx.room.*
import com.nexus.porsuk.data.local.entity.SyncLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SyncLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: SyncLogEntity)

    @Query("SELECT * FROM sync_logs ORDER BY timestamp DESC LIMIT 200")
    fun getRecentLogs(): Flow<List<SyncLogEntity>>
}
