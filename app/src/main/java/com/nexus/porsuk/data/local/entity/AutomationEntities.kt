package com.nexus.porsuk.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Otomasyon Kural Tablosu (AutomationRuleEntity)
 */
@Entity(
    tableName = "engine_automation_rules",
    indices = [Index(value = ["title"])]
)
data class AutomationRuleEntity(
    @PrimaryKey
    @ColumnInfo(name = "rule_id")
    val ruleId: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "category_name")
    val categoryName: String, // SYSTEM, CUSTOM, TEMPLATE

    @ColumnInfo(name = "if_condition")
    val ifCondition: String, // Logic expression or JSON for conditions

    @ColumnInfo(name = "action_text")
    val actionText: String, // NOTIFY, REPORT, WATCHLIST, ALARM, PDF

    @ColumnInfo(name = "trigger_type")
    val triggerType: String = "DAILY", // HOURLY, DAILY, WEEKLY, MONTHLY

    @ColumnInfo(name = "priority_name")
    val priorityName: String,

    @ColumnInfo(name = "is_enabled")
    val isEnabled: Boolean = true,

    @ColumnInfo(name = "last_run_at")
    val lastRunAt: Long = 0L
)

/**
 * Otomasyon Çalışma Geçmişi (AutomationHistoryEntity)
 */
@Entity(
    tableName = "engine_automation_history",
    indices = [Index(value = ["rule_id"])]
)
data class AutomationHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "rule_id")
    val ruleId: String,

    @ColumnInfo(name = "execution_time")
    val executionTime: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "duration_ms")
    val durationMs: Long,

    @ColumnInfo(name = "status")
    val status: String, // SUCCESS, FAILED, SKIPPED

    @ColumnInfo(name = "result_summary")
    val resultSummary: String,

    @ColumnInfo(name = "suggestions")
    val suggestions: String? = null
)

/**
 * AI Otomasyon Önerileri (AiAutomationSuggestionEntity)
 */
@Entity(tableName = "engine_ai_automation_suggestions")
data class AiAutomationSuggestionEntity(
    @PrimaryKey
    val suggestionId: String,

    val title: String,
    val description: String,
    val type: String, // NEW_RULE, OPTIMIZATION
    val isApplied: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Bildirim Merkezi Tablosu (NotificationCenterEntity)
 */
@Entity(
    tableName = "engine_notification_center_history",
    indices = [Index(value = ["category_name"])]
)
data class NotificationCenterEntity(
    @PrimaryKey
    @ColumnInfo(name = "notification_id")
    val notificationId: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "message")
    val message: String,

    @ColumnInfo(name = "category_name")
    val categoryName: String,

    @ColumnInfo(name = "priority_name")
    val priorityName: String,

    @ColumnInfo(name = "is_read")
    val isRead: Boolean = false,

    @ColumnInfo(name = "is_pinned")
    val isPinned: Boolean = false,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
