package com.nexus.porsuk.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Senkronizasyon Kuyruk Tablosu (CloudSyncQueueEntity)
 */
@Entity(
    tableName = "engine_cloud_sync_queue",
    indices = [Index(value = ["module_name"])]
)
data class CloudSyncQueueEntity(
    @PrimaryKey
    @ColumnInfo(name = "operation_id")
    val operationId: String,

    @ColumnInfo(name = "module_name")
    val moduleName: String,

    @ColumnInfo(name = "payload_json")
    val payloadJson: String,

    @ColumnInfo(name = "is_pending")
    val isPending: Boolean = true,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Kayıtlı Cihaz Tablosu (UserDeviceEntity)
 */
@Entity(
    tableName = "engine_user_devices",
    indices = [Index(value = ["device_id"])]
)
data class UserDeviceEntity(
    @PrimaryKey
    @ColumnInfo(name = "device_id")
    val deviceId: String,

    @ColumnInfo(name = "device_name")
    val deviceName: String,

    @ColumnInfo(name = "device_type")
    val deviceType: String,

    @ColumnInfo(name = "is_current")
    val isCurrent: Boolean = false,

    @ColumnInfo(name = "last_sync_timestamp")
    val lastSyncTimestamp: Long = System.currentTimeMillis()
)
