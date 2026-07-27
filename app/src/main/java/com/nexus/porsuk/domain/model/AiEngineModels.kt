package com.nexus.porsuk.domain.model

/**
 * AI Çalışma Modu (AI Operation Mode)
 */
enum class AiOperationMode(val displayName: String, val iconEmoji: String) {
    CLOUD_ONLY("Sadece Bulut AI", "☁️"),
    LOCAL_ONLY("Sadece Yerel AI", "📱"),
    HYBRID("Hibrit (Yerel + Bulut)", "🔄");
}

/**
 * Yerel AI Model Bilgisi (Local AI Model Info)
 */
data class LocalAiModel(
    val modelId: String,
    val name: String,
    val version: String,
    val sizeMb: Double,
    val ramUsageMb: Double,
    val cpuUsagePct: Double,
    val isDownloaded: Boolean = false,
    val isCurrent: Boolean = false,
    val lastUpdated: Long = 0L
)

/**
 * AI Kalite Kontrol Metrikleri (AI Quality Metrics)
 */
data class AiQualityMetrics(
    val similarityScore: Double, // 0.0 - 1.0 (Cloud vs Local)
    val consistencyRate: Double, // 0.0 - 1.0
    val localConfidence: Int,    // 0 - 100
    val lastComparisonDate: Long = System.currentTimeMillis()
)

/**
 * AI Motoru Sağlık Durumu (AI Engine Health)
 */
data class AiEngineStatus(
    val mode: AiOperationMode,
    val isCloudAvailable: Boolean,
    val isLocalReady: Boolean,
    val activeModelId: String?,
    val lastError: String? = null
)
