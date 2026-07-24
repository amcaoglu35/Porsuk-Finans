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
    val categoryName: String,

    @ColumnInfo(name = "if_condition")
    val ifCondition: String,

    @ColumnInfo(name = "action_text")
    val actionText: String,

    @ColumnInfo(name = "priority_name")
    val priorityName: String,

    @ColumnInfo(name = "is_enabled")
    val isEnabled: Boolean = true
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
