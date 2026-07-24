package com.nexus.porsuk.data.local.dao

import androidx.room.*
import com.nexus.porsuk.data.local.entity.NotificationHistoryEntity
import com.nexus.porsuk.data.local.entity.SmartAlertEntity
import kotlinx.coroutines.flow.Flow

/**
 * Porsuk Smart Alert Engine — Room DAO Sorguları
 */
@Dao
interface SmartAlertDao {

    // Akıllı Alarm Sorguları
    @Query("SELECT * FROM engine_smart_alerts ORDER BY created_at DESC")
    fun getAllSmartAlerts(): Flow<List<SmartAlertEntity>>

    @Query("SELECT * FROM engine_smart_alerts WHERE is_enabled = 1 AND is_muted = 0")
    fun getActiveSmartAlerts(): Flow<List<SmartAlertEntity>>

    @Query("SELECT * FROM engine_smart_alerts WHERE alert_id = :alertId LIMIT 1")
    fun getSmartAlertById(alertId: String): Flow<SmartAlertEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSmartAlert(alert: SmartAlertEntity)

    @Update
    suspend fun updateSmartAlert(alert: SmartAlertEntity)

    @Delete
    suspend fun deleteSmartAlert(alert: SmartAlertEntity)

    // Geçmiş Bildirim Günlüğü Sorguları
    @Query("SELECT * FROM engine_notification_history ORDER BY timestamp DESC")
    fun getAllNotificationHistory(): Flow<List<NotificationHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotificationHistory(notification: NotificationHistoryEntity)

    @Query("UPDATE engine_notification_history SET is_read = 1 WHERE notification_id = :notificationId")
    suspend fun markNotificationAsRead(notificationId: String)

    @Query("DELETE FROM engine_notification_history")
    suspend fun clearNotificationHistory()
}
