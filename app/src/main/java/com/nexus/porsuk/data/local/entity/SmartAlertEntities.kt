package com.nexus.porsuk.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Porsuk Smart Alert Engine — Akıllı Alarmlar Tablosu (SmartAlertEntity)
 */
@Entity(
    tableName = "engine_smart_alerts",
    indices = [
        Index(value = ["alert_id"], unique = true),
        Index(value = ["symbol"]),
        Index(value = ["is_enabled"])
    ]
)
data class SmartAlertEntity(
    @PrimaryKey
    @ColumnInfo(name = "alert_id")
    val alertId: String,

    @ColumnInfo(name = "symbol")
    val symbol: String,

    @ColumnInfo(name = "category")
    val category: String, // PRICE, PERCENT_CHANGE, VOLUME, DIVIDEND, EARNINGS, NEWS, ECONOMIC_CALENDAR, PORTFOLIO, WATCHLIST, AI_ORAKUL_STUB

    @ColumnInfo(name = "condition")
    val condition: String, // ABOVE, BELOW, EQUAL, PERCENT_INCREASE, PERCENT_DECREASE, VOLUME_SPIKE

    @ColumnInfo(name = "target_value")
    val targetValue: Double,

    @ColumnInfo(name = "note")
    val note: String? = null,

    @ColumnInfo(name = "is_enabled")
    val isEnabled: Boolean = true,

    @ColumnInfo(name = "is_muted")
    val isMuted: Boolean = false,

    @ColumnInfo(name = "last_triggered_at")
    val lastTriggeredAt: Long? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Porsuk Smart Alert Engine — Bildirim Geçmişi Günlüğü Tablosu (NotificationHistoryEntity)
 */
@Entity(
    tableName = "engine_notification_history",
    indices = [
        Index(value = ["notification_id"], unique = true),
        Index(value = ["is_read"])
    ]
)
data class NotificationHistoryEntity(
    @PrimaryKey
    @ColumnInfo(name = "notification_id")
    val notificationId: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "message")
    val message: String,

    @ColumnInfo(name = "category")
    val category: String,

    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "is_read")
    val isRead: Boolean = false
)
