package com.nexus.porsuk.data.local.dao

import androidx.room.*
import com.nexus.porsuk.data.local.entity.AutomationRuleEntity
import com.nexus.porsuk.data.local.entity.NotificationCenterEntity
import kotlinx.coroutines.flow.Flow

/**
 * Porsuk Notification & Automation Center — Room DAO Sorguları
 */
@Dao
interface NotificationAutomationDao {

    // Bildirim Sorguları
    @Query("SELECT * FROM engine_notification_center_history ORDER BY created_at DESC")
    fun getAllNotifications(): Flow<List<NotificationCenterEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(item: NotificationCenterEntity)

    @Query("DELETE FROM engine_notification_center_history WHERE notification_id = :notificationId")
    suspend fun deleteNotification(notificationId: String)

    // Otomasyon Kural Sorguları
    @Query("SELECT * FROM engine_automation_rules ORDER BY rule_id DESC")
    fun getAllRules(): Flow<List<AutomationRuleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRule(rule: AutomationRuleEntity)
}
