package com.nexus.porsuk.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nexus.porsuk.core.notification.PorsukNotificationManager
import com.nexus.porsuk.data.local.dao.SmartAlertDao
import com.nexus.porsuk.data.local.entity.NotificationHistoryEntity
import com.nexus.porsuk.data.logging.DataLogger
import com.nexus.porsuk.domain.model.AlertCondition
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.UUID

/**
 * Porsuk Smart Alert Engine — Arka Plan Periyodik Alarm Kontrolcüsü (WorkManager)
 *
 * Uygulama kapalı olsa bile batarya dostu periyotlarla alarmları ve piyasa koşullarını denetler.
 */
@HiltWorker
class SmartAlertWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val alertDao: SmartAlertDao,
    private val notificationManager: PorsukNotificationManager,
    private val logger: DataLogger
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val activeAlerts = alertDao.getActiveSmartAlerts().first()

            activeAlerts.forEach { alert ->
                // Temsili fiyat kontrolü / Piyasa sorgusu
                val currentPrice = 285.50
                val isTriggered = when (AlertCondition.valueOf(alert.condition)) {
                    AlertCondition.ABOVE -> currentPrice >= alert.targetValue
                    AlertCondition.BELOW -> currentPrice <= alert.targetValue
                    else -> false
                }

                if (isTriggered) {
                    val title = "🚨 Alarm Tetiklendi: ${alert.symbol}"
                    val msg = "${alert.symbol} hedef fiyat seviyesine ulaştı! Anlık Fiyat: $currentPrice TL (Hedef: ${alert.targetValue})"

                    // 1. Android Sistem Bildirimi Gönder
                    notificationManager.sendNotification(
                        channelId = PorsukNotificationManager.CHANNEL_PRICE_ALERTS,
                        notificationId = alert.alertId.hashCode(),
                        title = title,
                        message = msg
                    )

                    // 2. Geçmiş Bildirim Günlüğüne Kaydet
                    alertDao.insertNotificationHistory(
                        NotificationHistoryEntity(
                            notificationId = UUID.randomUUID().toString(),
                            title = title,
                            message = msg,
                            category = alert.category
                        )
                    )

                    // 3. Alarmın son tetiklenme zamanını güncelle
                    alertDao.updateSmartAlert(alert.copy(lastTriggeredAt = System.currentTimeMillis()))
                    logger.logSyncEvent("SmartAlertWorker", "Alarm tetiklendi: ${alert.symbol}")
                }
            }

            Result.success()
        } catch (e: Exception) {
            logger.logError("SmartAlertWorker", "Arka plan kontrolünde hata: ${e.message}", e)
            Result.retry()
        }
    }
}
