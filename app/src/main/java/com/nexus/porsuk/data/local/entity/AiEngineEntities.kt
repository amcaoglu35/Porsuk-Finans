package com.nexus.porsuk.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Yerel AI Model Kayıtları
 */
@Entity(tableName = "engine_local_models")
data class LocalAiModelEntity(
    @PrimaryKey
    val modelId: String,
    val name: String,
    val version: String,
    val sizeMb: Double,
    val isDownloaded: Boolean = false,
    val localPath: String? = null,
    val lastUpdated: Long = 0L
)

/**
 * AI Analiz Önbelleği (Offline Desteği İçin)
 */
@Entity(tableName = "engine_ai_cache")
data class AiAnalysisCacheEntity(
    @PrimaryKey
    val cacheKey: String, // hash(type + symbol + timestamp)
    val resultText: String,
    val modelUsed: String, // CLOUD, LOCAL
    val timestamp: Long = System.currentTimeMillis(),
    val expiresAt: Long
)
