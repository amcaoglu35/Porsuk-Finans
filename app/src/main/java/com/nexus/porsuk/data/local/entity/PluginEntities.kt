package com.nexus.porsuk.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Eklenti Yapılandırma ve API Anahtarı
 */
@Entity(tableName = "engine_plugin_config")
data class PluginConfigEntity(
    @PrimaryKey
    val pluginId: String,
    val apiKey: String?,
    val baseUrl: String?,
    val backupPluginId: String?
)

/**
 * Eklenti Sağlık İstatistikleri
 */
@Entity(tableName = "engine_plugin_health")
data class PluginHealthEntity(
    @PrimaryKey
    val pluginId: String,
    val latencyMs: Long,
    val successRate: Double,
    val lastError: String?,
    val totalRequests: Long,
    val failedRequests: Long,
    val lastChecked: Long = System.currentTimeMillis()
)
