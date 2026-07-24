package com.nexus.porsuk.data.repository

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.nexus.porsuk.data.local.dao.SmartAlertDao
import com.nexus.porsuk.data.local.entity.NotificationHistoryEntity
import com.nexus.porsuk.data.local.entity.SmartAlertEntity
import com.nexus.porsuk.data.logging.DataLogger
import com.nexus.porsuk.data.worker.SmartAlertWorker
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SmartAlertRepositoryImpl @Inject constructor(
    private val dao: SmartAlertDao,
    private val logger: DataLogger
) : SmartAlertRepository {

    override fun getAllSmartAlerts(): Flow<List<SmartAlert>> {
        return dao.getAllSmartAlerts().map { list -> list.map { it.toDomainModel() } }
    }

    override fun getActiveSmartAlerts(): Flow<List<SmartAlert>> {
        return dao.getActiveSmartAlerts().map { list -> list.map { it.toDomainModel() } }
    }

    override suspend fun createSmartAlert(
        symbol: String,
        category: AlertCategory,
        condition: AlertCondition,
        targetValue: Double,
        note: String?
    ): String {
        val newId = UUID.randomUUID().toString()
        val entity = SmartAlertEntity(
            alertId = newId,
            symbol = symbol,
            category = category.name,
            condition = condition.name,
            targetValue = targetValue,
            note = note
        )
        dao.insertSmartAlert(entity)
        logger.logSyncEvent("SmartAlertEngine", "Yeni alarm oluşturuldu: $symbol $targetValue ($newId)")
        return newId
    }

    override suspend fun updateSmartAlert(alert: SmartAlert) {
        dao.updateSmartAlert(alert.toEntityModel())
    }

    override suspend fun deleteSmartAlert(alertId: String) {
        val alert = dao.getSmartAlertById(alertId).first()
        if (alert != null) {
            dao.deleteSmartAlert(alert)
            logger.logSyncEvent("SmartAlertEngine", "Alarm silindi: $alertId")
        }
    }

    override suspend fun toggleAlertStatus(alertId: String, isEnabled: Boolean) {
        val alert = dao.getSmartAlertById(alertId).first()
        if (alert != null) {
            dao.updateSmartAlert(alert.copy(isEnabled = isEnabled))
        }
    }

    override suspend fun toggleAlertMute(alertId: String, isMuted: Boolean) {
        val alert = dao.getSmartAlertById(alertId).first()
        if (alert != null) {
            dao.updateSmartAlert(alert.copy(isMuted = isMuted))
        }
    }
}

@Singleton
class AppNotificationRepositoryImpl @Inject constructor(
    private val dao: SmartAlertDao
) : AppNotificationRepository {

    override fun getAllNotificationHistory(): Flow<List<AppNotificationItem>> {
        return dao.getAllNotificationHistory().map { list -> list.map { it.toDomainModel() } }
    }

    override suspend fun addNotificationItem(title: String, message: String, category: AlertCategory) {
        val entity = NotificationHistoryEntity(
            notificationId = UUID.randomUUID().toString(),
            title = title,
            message = message,
            category = category.name
        )
        dao.insertNotificationHistory(entity)
    }

    override suspend fun markAsRead(notificationId: String) {
        dao.markNotificationAsRead(notificationId)
    }

    override suspend fun clearAllNotifications() {
        dao.clearNotificationHistory()
    }
}

@Singleton
class SystemAlarmRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val logger: DataLogger
) : SystemAlarmRepository {

    companion object {
        private const val WORK_NAME = "porsuk_smart_alert_worker"
    }

    override suspend fun schedulePeriodicAlertWorker() {
        val request = PeriodicWorkRequestBuilder<SmartAlertWorker>(15, TimeUnit.MINUTES).build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
        logger.logSyncEvent("SystemAlarmRepo", "WorkManager periyodik alarm kontrolcüsü planlandı")
    }

    override suspend fun cancelPeriodicAlertWorker() {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        logger.logSyncEvent("SystemAlarmRepo", "WorkManager periyodik alarm kontrolcüsü iptal edildi")
    }
}

// Mappers
private fun SmartAlertEntity.toDomainModel() = SmartAlert(
    alertId = alertId,
    symbol = symbol,
    category = try { AlertCategory.valueOf(category) } catch (e: Exception) { AlertCategory.PRICE },
    condition = AlertCondition.valueOf(condition),
    targetValue = targetValue,
    note = note,
    isEnabled = isEnabled,
    isMuted = isMuted,
    lastTriggeredAt = lastTriggeredAt,
    createdAt = createdAt
)

private fun SmartAlert.toEntityModel() = SmartAlertEntity(
    alertId = alertId,
    symbol = symbol,
    category = category.name,
    condition = condition.name,
    targetValue = targetValue,
    note = note,
    isEnabled = isEnabled,
    isMuted = isMuted,
    lastTriggeredAt = lastTriggeredAt,
    createdAt = createdAt
)

private fun NotificationHistoryEntity.toDomainModel() = AppNotificationItem(
    notificationId = notificationId,
    title = title,
    message = message,
    category = try { AlertCategory.valueOf(category) } catch (e: Exception) { AlertCategory.PRICE },
    timestamp = timestamp,
    isRead = isRead
)
