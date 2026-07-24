package com.nexus.porsuk.data.local.dao

import androidx.room.*
import com.nexus.porsuk.data.local.entity.CloudSyncQueueEntity
import com.nexus.porsuk.data.local.entity.UserDeviceEntity
import kotlinx.coroutines.flow.Flow

/**
 * Porsuk Cloud Sync Platform — Room DAO Sorguları
 */
@Dao
interface CloudSyncDao {

    // Senkronizasyon Kuyruğu
    @Query("SELECT * FROM engine_cloud_sync_queue WHERE is_pending = 1 ORDER BY created_at ASC")
    fun getPendingOperations(): Flow<List<CloudSyncQueueEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOperation(op: CloudSyncQueueEntity)

    @Query("DELETE FROM engine_cloud_sync_queue WHERE operation_id = :opId")
    suspend fun deleteOperation(opId: String)

    // Cihaz Sorguları
    @Query("SELECT * FROM engine_user_devices ORDER BY last_sync_timestamp DESC")
    fun getAllDevices(): Flow<List<UserDeviceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevice(dev: UserDeviceEntity)
}
