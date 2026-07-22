package com.nexus.porsuk.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.nexus.porsuk.R
import com.nexus.porsuk.data.local.PorsukDatabase
import com.nexus.porsuk.data.remote.GoogleFinanceScraper
import com.nexus.porsuk.data.remote.ScrapeResult
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

class PriceAlertWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val CHANNEL_ID = "porsuk_price_alerts"
        const val CHANNEL_NAME = "Fiyat Alarmları"
        const val WORK_NAME = "PriceAlertWork"

        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<PriceAlertWorker>(15, TimeUnit.MINUTES)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }

    override suspend fun doWork(): Result {
        return try {
            val db = PorsukDatabase.getDatabase(applicationContext)
            val dao = db.assetDao()

            val activeAlerts = dao.getActivePriceAlerts()
            if (activeAlerts.isEmpty()) return Result.success()

            val scraper = GoogleFinanceScraper()
            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            createNotificationChannel(notificationManager)

            activeAlerts.forEach { alert ->
                val targetPrice = alert.targetPrice ?: return@forEach
                val result = scraper.fetchPrice(alert.symbol, alert.market)
                if (result is ScrapeResult.Success) {
                    val currentPrice = result.data.price
                    val triggered = if (alert.isAbove) {
                        currentPrice >= targetPrice
                    } else {
                        currentPrice <= targetPrice
                    }

                    if (triggered) {
                        val company = dao.getCompany(alert.symbol)
                        val companyName = company?.name ?: alert.symbol
                        val direction = if (alert.isAbove) "üzerine çıktı" else "altına indi"
                        val priceStr = String.format("%.2f", currentPrice)
                        val targetStr = String.format("%.2f", targetPrice)

                        sendNotification(
                            notificationManager = notificationManager,
                            notificationId = alert.id,
                            title = "🎯 Fiyat Alarmı: $companyName",
                            message = "${alert.symbol} hedef fiyatın ($targetStr) $direction! Güncel: $priceStr"
                        )

                        // Alarm tetiklendi, deaktive et
                        dao.updatePriceAlert(alert.copy(isActive = false))
                    }
                }
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Hisse fiyatı hedef seviyeye ulaştığında bildirim alın"
                enableLights(true)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(
        notificationManager: NotificationManager,
        notificationId: Int,
        title: String,
        message: String
    ) {
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.app_logo)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId, notification)
    }
}
