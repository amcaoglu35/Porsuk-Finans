package com.nexus.porsuk.data.local.dao

import androidx.room.*
import com.nexus.porsuk.data.local.entity.*
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

    @Query("DELETE FROM engine_automation_rules WHERE rule_id = :ruleId")
    suspend fun deleteRule(ruleId: String)

    // Çalışma Geçmişi Sorguları
    @Query("SELECT * FROM engine_automation_history ORDER BY execution_time DESC")
    fun getExecutionHistory(): Flow<List<AutomationHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: AutomationHistoryEntity)

    // AI Önerileri Sorguları
    @Query("SELECT * FROM engine_ai_automation_suggestions WHERE isApplied = 0 ORDER BY createdAt DESC")
    fun getAiSuggestions(): Flow<List<AiAutomationSuggestionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAiSuggestion(suggestion: AiAutomationSuggestionEntity)

    @Query("UPDATE engine_ai_automation_suggestions SET isApplied = 1 WHERE suggestionId = :suggestionId")
    suspend fun markSuggestionAsApplied(suggestionId: String)

    // Ajan Performans Sorguları
    @Query("SELECT * FROM engine_agent_performance")
    fun getAllAgentPerformance(): Flow<List<AgentPerformanceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateAgentPerformance(performance: AgentPerformanceEntity)
}
