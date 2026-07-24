package com.nexus.porsuk.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Porsuk Finans — Merkezi Bildirim Yöneticisi (PorsukNotificationManager)
 *
 * Android Notification Channels ve Sistem Bildirimlerini yönetir.
 */
@Singleton
class PorsukNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val CHANNEL_PRICE_ALERTS = "channel_price_alerts"
        const val CHANNEL_NEWS_ALERTS = "channel_news_alerts"
        const val CHANNEL_PORTFOLIO_ALERTS = "channel_portfolio_alerts"
        const val CHANNEL_AI_ORAKUL_ALERTS = "channel_ai_orakut_alerts"
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val priceChannel = NotificationChannel(
                CHANNEL_PRICE_ALERTS,
                "Porsuk Fiyat ve Hacim Alarmları",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Fiyat hedefi ve sıradışı hacim artış bildirimleri"
            }

            val newsChannel = NotificationChannel(
                CHANNEL_NEWS_ALERTS,
                "Porsuk Haber ve KAP Bildirimleri",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Şirket haberleri, SPK ve KAP duyuruları"
            }

            val portfolioChannel = NotificationChannel(
                CHANNEL_PORTFOLIO_ALERTS,
                "Porsuk Portföy ve Risk Alarmları",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Portföy kar/zarar ve risk seviyesi bildirimleri"
            }

            val aiChannel = NotificationChannel(
                CHANNEL_AI_ORAKUL_ALERTS,
                "Porsuk Orakul AI Akıllı Alarmlar",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Orakul AI Master Score ve teknik analiz değişim bildirimleri"
            }

            notificationManager.createNotificationChannels(
                listOf(priceChannel, newsChannel, portfolioChannel, aiChannel)
            )
        }
    }

    /**
     * Anlık sistem bildirimi tetikler.
     */
    fun sendNotification(
        channelId: String,
        notificationId: Int,
        title: String,
        message: String
    ) {
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        notificationManager.notify(notificationId, builder.build())
    }
}
