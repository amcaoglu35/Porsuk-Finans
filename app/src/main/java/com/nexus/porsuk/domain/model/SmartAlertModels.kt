package com.nexus.porsuk.domain.model

/**
 * Porsuk Smart Alert Engine — Akıllı Alarm Domain Modeli
 */
data class SmartAlert(
    val alertId: String,
    val symbol: String,
    val category: AlertCategory,
    val condition: AlertCondition,
    val targetValue: Double,
    val note: String? = null,
    val isEnabled: Boolean = true,
    val isMuted: Boolean = false,
    val lastTriggeredAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Porsuk Smart Alert Engine — Uygulama İçi Geçmiş Bildirim Domain Modeli
 */
data class AppNotificationItem(
    val notificationId: String,
    val title: String,
    val message: String,
    val category: AlertCategory,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

/**
 * Geleceğe Hazır AI Orakul Alarm Stub Modeli
 */
data class AiSmartAlertStub(
    val alertId: String,
    val symbol: String,
    val targetAiScore: Int = 90, // Orakul Master Score 90+ olursa tetikle
    val isTechnicalPatternTrigger: Boolean = true // Destek/Direnç veya Trend değişimi
)
