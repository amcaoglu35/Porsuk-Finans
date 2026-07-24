package com.nexus.porsuk.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Güvenlik Denetim Günlük Tablosu (SecurityAuditEntity)
 */
@Entity(
    tableName = "engine_security_audit_logs",
    indices = [Index(value = ["category"])]
)
data class SecurityAuditEntity(
    @PrimaryKey
    @ColumnInfo(name = "log_id")
    val logId: String,

    @ColumnInfo(name = "category")
    val category: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Aktif Güvenlik Oturumu Tablosu (SecuritySessionEntity)
 */
@Entity(
    tableName = "engine_security_sessions",
    indices = [Index(value = ["session_id"])]
)
data class SecuritySessionEntity(
    @PrimaryKey
    @ColumnInfo(name = "session_id")
    val sessionId: String,

    @ColumnInfo(name = "device_name")
    val deviceName: String,

    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
