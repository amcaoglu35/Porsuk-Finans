package com.nexus.porsuk.feature.calendar

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.nexus.porsuk.ui.common.NotificationHelper

class IpoAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val symbol = intent.getStringExtra("SYMBOL") ?: "YENI_ARZ"
        val companyName = intent.getStringExtra("COMPANY_NAME") ?: "Halka Arz Şirketi"
        val price = intent.getDoubleExtra("PRICE", 0.0)
        val distribution = intent.getStringExtra("DISTRIBUTION") ?: "Bireysele Eşit"

        val title = "🔔 Halka Arz Hatırlatıcı: $symbol"
        val message = "$companyName halka arz talep toplaması bugün başladı! \nFiyat: $price TL • Dağıtım: $distribution"

        NotificationHelper.sendNotification(
            context = context,
            title = title,
            message = message,
            notificationId = symbol.hashCode()
        )
    }
}
