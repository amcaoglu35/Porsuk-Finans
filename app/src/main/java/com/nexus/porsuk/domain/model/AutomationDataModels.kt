package com.nexus.porsuk.domain.model

/**
 * Otomasyon Kural Modeli (AutomationRuleModel)
 */
data class AutomationRuleModel(
    val ruleId: String = "rule_${System.currentTimeMillis()}",
    val title: String,
    val category: AlertCategory,
    val ifConditionText: String, // Örn: "RSI (14) < 30 VE Fiyat > 250"
    val actionText: String, // Örn: "Bildirim Gönder & Takip Listesine Ekle"
    val priority: NotificationPriority = NotificationPriority.HIGH,
    val isEnabled: Boolean = true
)

/**
 * Bildirim Merkezi Öge Modeli (NotificationCenterItem)
 */
data class NotificationCenterItem(
    val notificationId: String = "notif_${System.currentTimeMillis()}",
    val title: String,
    val message: String,
    val category: AlertCategory,
    val priority: NotificationPriority,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val isPinned: Boolean = false
)

/**
 * Otomasyon İş Akışı Modeli (AutomationWorkflow)
 */
data class AutomationWorkflow(
    val workflowId: String,
    val title: String,
    val cronScheduleText: String, // Örn: "Her Sabah 09:00"
    val description: String,
    val lastRunStatusText: String = "Başarılı"
)

/**
 * Otomasyon Çalışma Geçmişi Modeli
 */
data class AutomationHistoryModel(
    val id: Long = 0,
    val ruleId: String,
    val executionTime: Long,
    val durationMs: Long,
    val status: String,
    val resultSummary: String,
    val suggestions: String? = null
)

/**
 * AI Otomasyon Önerisi Modeli
 */
data class AiAutomationSuggestionModel(
    val suggestionId: String,
    val title: String,
    val description: String,
    val type: String,
    val isApplied: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Geleceğe Hazır AI Notification Prioritization & Digest Stub Modeli
 */
data class AiNotificationDigestStub(
    val summaryText: String = "Orakul AI Günlük Özeti: Portföyünüzdeki 3 hisse yeni zirve yaptı, TCMB faiz kararı saat 14:00'te açıklanacak.",
    val unreadCount: Int = 5,
    val aiFilterScore: Int = 98
)
