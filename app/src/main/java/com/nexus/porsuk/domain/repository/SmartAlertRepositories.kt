package com.nexus.porsuk.domain.repository

import com.nexus.porsuk.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * 1. Akıllı Alarm Deposu Sözleşmesi (SmartAlertRepository)
 */
interface SmartAlertRepository {
    fun getAllSmartAlerts(): Flow<List<SmartAlert>>
    fun getActiveSmartAlerts(): Flow<List<SmartAlert>>
    suspend fun createSmartAlert(
        symbol: String,
        category: AlertCategory,
        condition: AlertCondition,
        targetValue: Double,
        note: String? = null
    ): String
    suspend fun updateSmartAlert(alert: SmartAlert)
    suspend fun deleteSmartAlert(alertId: String)
    suspend fun toggleAlertStatus(alertId: String, isEnabled: Boolean)
    suspend fun toggleAlertMute(alertId: String, isMuted: Boolean)
}

/**
 * 2. Geçmiş Bildirimler Deposu Sözleşmesi (AppNotificationRepository)
 */
interface AppNotificationRepository {
    fun getAllNotificationHistory(): Flow<List<AppNotificationItem>>
    suspend fun addNotificationItem(title: String, message: String, category: AlertCategory)
    suspend fun markAsRead(notificationId: String)
    suspend fun clearAllNotifications()
}

/**
 * 3. Sistem Alarmları Deposu Sözleşmesi (SystemAlarmRepository)
 */
interface SystemAlarmRepository {
    suspend fun schedulePeriodicAlertWorker()
    suspend fun cancelPeriodicAlertWorker()
}
