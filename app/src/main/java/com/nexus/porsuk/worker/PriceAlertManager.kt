package com.nexus.porsuk.worker

import android.content.Context
import com.nexus.porsuk.core.common.PorsukEvent
import com.nexus.porsuk.core.common.PorsukEventBus
import com.nexus.porsuk.core.common.PorsukLogger
import com.nexus.porsuk.data.local.dao.AssetDao
import com.nexus.porsuk.ui.common.NotificationHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Porsuk Price Alert Manager.
 * Subscribes to PriceUpdated events from the EventBus and triggers local notifications
 * if target thresholds are met.
 */
@Singleton
class PriceAlertManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val eventBus: PorsukEventBus,
    private val assetDao: AssetDao
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        observePriceUpdates()
    }

    private fun observePriceUpdates() {
        scope.launch {
            eventBus.events.collect { event ->
                if (event is PorsukEvent.PriceUpdated) {
                    checkAlertsForStock(event.symbol, event.newPrice, event.changePct)
                }
            }
        }
    }

    private suspend fun checkAlertsForStock(symbol: String, currentPrice: Double, changePct: Double) {
        val activeAlerts = assetDao.getActivePriceAlerts().filter { it.symbol == symbol }
        if (activeAlerts.isEmpty()) return

        activeAlerts.forEach { alert ->
            var triggered = false
            var title = "🎯 Hedef Ulaşıldı"
            var message = ""

            when (alert.alertType) {
                "ABOVE" -> {
                    if (alert.targetPrice != null && currentPrice >= alert.targetPrice) {
                        triggered = true
                        title = "🎯 Fiyat Hedefi (Üst): $symbol"
                        message = "$symbol fiyatı hedeflediğiniz ${"%.2f".format(alert.targetPrice)} değerinin üzerine çıktı!"
                    }
                }
                "BELOW" -> {
                    if (alert.targetPrice != null && currentPrice <= alert.targetPrice) {
                        triggered = true
                        title = "🎯 Fiyat Hedefi (Alt): $symbol"
                        message = "$symbol fiyatı hedeflediğiniz ${"%.2f".format(alert.targetPrice)} değerinin altına düştü!"
                    }
                }
                "PERCENT_UP" -> {
                    if (alert.targetChangePct != null && changePct >= alert.targetChangePct) {
                        triggered = true
                        title = "📈 Yükseliş Alarmı: $symbol"
                        message = "$symbol bugün %${"%.1f".format(changePct)} yükseliş kaydetti!"
                    }
                }
                "PERCENT_DOWN" -> {
                    if (alert.targetChangePct != null && changePct <= -alert.targetChangePct) {
                        triggered = true
                        title = "📉 Düşüş Alarmı: $symbol"
                        message = "$symbol bugün %${"%.1f".format(Math.abs(changePct))} düşüş kaydetti!"
                    }
                }
            }

            if (triggered) {
                NotificationHelper.sendNotification(context, title, message, alert.id)
                assetDao.updatePriceAlert(alert.copy(isActive = false))
                PorsukLogger.i("Price Alert triggered for $symbol: $title")
            }
        }
    }
}
