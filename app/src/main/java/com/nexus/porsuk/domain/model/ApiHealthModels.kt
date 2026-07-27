package com.nexus.porsuk.domain.model

/**
 * API Sağlık ve Performans Metrikleri
 */
data class ApiHealthMetrics(
    val pluginId: String,
    val latencyMs: Long,
    val successRate: Double, // 0.0 - 1.0
    val lastError: String? = null,
    val totalRequests: Long,
    val failedRequests: Long,
    val lastChecked: Long = System.currentTimeMillis()
)

/**
 * API Anahtarı ve Güvenlik Yapılandırması
 */
data class ApiConfig(
    val pluginId: String,
    val apiKey: String?,
    val baseUrl: String?,
    val isEncrypted: Boolean = true,
    val backupPluginId: String? = null // Failover provider
)
