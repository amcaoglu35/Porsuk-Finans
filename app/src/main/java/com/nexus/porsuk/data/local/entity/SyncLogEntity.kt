package com.nexus.porsuk.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Porsuk Data Center — Senkronizasyon ve Hata Log Tablosu
 */
@Entity(tableName = "sync_logs")
data class SyncLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tag: String,
    val message: String,
    val type: String, // SYNC, ERROR, PERFORMANCE
    val status: String, // SUCCESS, FAILED, WARNING
    val timestamp: Long = System.currentTimeMillis()
)
