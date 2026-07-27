package com.nexus.porsuk.worker

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nexus.porsuk.data.local.PorsukDatabase
import com.nexus.porsuk.data.remote.ApiKeys
import com.nexus.porsuk.data.remote.FinnhubService
import com.nexus.porsuk.data.remote.GoogleFinanceScraper
import com.nexus.porsuk.data.remote.YahooFinanceService
import com.nexus.porsuk.data.remote.ScrapeResult
import com.nexus.porsuk.data.repository.FinanceRepository
import com.nexus.porsuk.widget.PorsukWidget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.Locale

class PorsukUpdateWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    private val TAG = "PorsukUpdateWorker"

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        Log.d(TAG, "Periyodik borsa veri güncelleme işlemi başlatıldı...")

        try {
            val database = PorsukDatabase.getDatabase(applicationContext)
            val assetDao = database.assetDao()
            val settingsManager = com.nexus.porsuk.data.local.SettingsManager(applicationContext)
            val apiKey = settingsManager.getGeminiApiKey()
            
            val repository = FinanceRepository(
                assetDao,
                GoogleFinanceScraper(),
                null, // No EventBus for background sync to avoid UI loops
                FinnhubService(ApiKeys.FINNHUB),
                YahooFinanceService(ApiKeys.YAHOO),
                settingsManager
            )

            // 1. Hisse Fiyatlarını Güncelle
            val savedCompanies = assetDao.getAllCompaniesDirect()
            val priceAlertsEnabled = try { settingsManager.priceAlerts.first() } catch (_: Exception) { true }
            val activeAlerts = repository.getActivePriceAlerts()

            if (savedCompanies.isNotEmpty()) {
                for (company in savedCompanies) {
                    val result = repository.refreshPrice(company.symbol, company.market)
                    if (result is ScrapeResult.Success) {
                        val snapshot = result.data
                        
                        // Fiyat Alarmları Kontrolü (Item 8)
                        if (priceAlertsEnabled) {
                            checkAlarms(company, snapshot, activeAlerts, repository)
                        }

                        val updatedCompany = company.copy(
                            currentPrice = snapshot.price,
                            changePercent = snapshot.changePercent,
                            lastUpdated = System.currentTimeMillis()
                        )
                        assetDao.updateCompany(updatedCompany)
                    }
                }
            }

            // 1.5. Akıllı AI Bildirim Sistemi Taraması (Spam Korumalı Otomatik Anomali Taraması)
            try {
                val holdings = assetDao.getAllBasketItemsDirect()
                val pricesMap = repository.prices.value
                com.nexus.porsuk.data.remote.AiSmartNotificationEngine.scanAndNotify(
                    context = applicationContext,
                    holdings = holdings,
                    companies = savedCompanies,
                    priceMap = pricesMap
                )
            } catch (e: Exception) {
                Log.e(TAG, "Smart AI Notification Error: ${e.localizedMessage}")
            }

            // 2. Gün Sonu Özeti (18:00 - 22:00)
            val dailySummaryEnabled = try { settingsManager.dailySummary.first() } catch (_: Exception) { true }
            if (dailySummaryEnabled && !apiKey.isNullOrBlank()) {
                val calendar = java.util.Calendar.getInstance()
                val hour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
                if (hour in 18..22) {
                    sendEveningSummary(apiKey, assetDao, settingsManager)
                }
            }

            // 3. Orakul Sabah Bülteni (09:30) (Item 7)
            val dailyOrakulEnabled = try { settingsManager.dailyOrakulNotif.first() } catch (_: Exception) { true }
            if (dailyOrakulEnabled && !apiKey.isNullOrBlank()) {
                val calendar = java.util.Calendar.getInstance()
                val hour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
                val minute = calendar.get(java.util.Calendar.MINUTE)
                
                if ((hour == 9 && minute >= 30) || hour == 10) {
                    sendMorningInsight(apiKey, assetDao, settingsManager)
                }
            }

            // 4. Widget'ları Güncelle
            try {
                val manager = GlanceAppWidgetManager(applicationContext)
                manager.getGlanceIds(PorsukWidget::class.java).forEach { id ->
                    PorsukWidget().update(applicationContext, id)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to update widgets", e)
            }

            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Update worker error", e)
            Result.success()
        }
    }

    private suspend fun checkAlarms(company: com.nexus.porsuk.data.local.entity.Company, snapshot: com.nexus.porsuk.data.local.entity.PriceSnapshot, activeAlerts: List<com.nexus.porsuk.data.local.entity.PriceAlert>, repository: FinanceRepository) {
        val stockAlerts = activeAlerts.filter { it.symbol == company.symbol }
        val currencySym = com.nexus.porsuk.ui.common.CurrencyFormatter.getCurrencySymbol(company.market)

        for (alert in stockAlerts) {
            var triggered = false
            var alertMsg = ""
            var alertTitle = "🎯 Hedef Ulaşıldı"

            when (alert.alertType) {
                "ABOVE" -> {
                    if (alert.targetPrice != null && snapshot.price >= alert.targetPrice) {
                        triggered = true
                        alertTitle = "🎯 Hedef Fiyata Ulaşıldı: ${company.symbol}"
                        alertMsg = "${company.name} fiyatı hedeflediğiniz $currencySym${String.format(Locale.US, "%.2f", alert.targetPrice)} değerinin üzerine çıktı!"
                    }
                }
                "BELOW" -> {
                    if (alert.targetPrice != null && snapshot.price <= alert.targetPrice) {
                        triggered = true
                        alertTitle = "🎯 Hedef Fiyata Ulaşıldı: ${company.symbol}"
                        alertMsg = "${company.name} fiyatı hedeflediğiniz $currencySym${String.format(Locale.US, "%.2f", alert.targetPrice)} değerinin altına düştü!"
                    }
                }
                "PERCENT_UP" -> {
                    if (alert.targetChangePct != null && snapshot.changePercent >= alert.targetChangePct) {
                        triggered = true
                        alertTitle = "📈 Yükseliş Alarmı: ${company.symbol}"
                        alertMsg = "${company.name} bugün %${String.format(Locale.US, "%.1f", snapshot.changePercent)} yükseldi!"
                    }
                }
                "PERCENT_DOWN" -> {
                    if (alert.targetChangePct != null && snapshot.changePercent <= -alert.targetChangePct) {
                        triggered = true
                        alertTitle = "📉 Düşüş Alarmı: ${company.symbol}"
                        alertMsg = "${company.name} bugün %${String.format(Locale.US, "%.1f", snapshot.changePercent)} düştü!"
                    }
                }
                "WEEK52_HIGH" -> {
                    val info = repository.getCachedInfo(company.symbol).first()
                    if (info?.week52High != null && snapshot.price >= info.week52High * 0.98) {
                        triggered = true
                        alertTitle = "🚀 52 Hafta Zirvesi: ${company.symbol}"
                        alertMsg = "${company.name} 52 haftalık zirvesine yaklaştı!"
                    }
                }
            }
            
            if (triggered) {
                com.nexus.porsuk.ui.common.NotificationHelper.sendNotification(applicationContext, alertTitle, alertMsg, alert.id)
                repository.updatePriceAlert(alert.copy(isActive = false))
            }
        }
    }

    private suspend fun sendEveningSummary(apiKey: String, assetDao: com.nexus.porsuk.data.local.dao.AssetDao, settingsManager: com.nexus.porsuk.data.local.SettingsManager) {
        val lastEvening = try { settingsManager.lastEveningNotifTime.first() } catch (_: Exception) { 0L }
        val todayStart = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, 0); set(java.util.Calendar.MINUTE, 0); set(java.util.Calendar.SECOND, 0)
        }.timeInMillis
        
        if (lastEvening < todayStart) {
            try {
                val basketItems = assetDao.getAllBasketItemsDirect()
                val companies = assetDao.getAllCompaniesDirect()
                val companyMap = companies.associateBy { it.symbol }
                
                var totalValue = 0.0
                basketItems.forEach { item ->
                    val currentPrice = companyMap[item.symbol]?.currentPrice ?: item.buyPrice
                    totalValue += item.quantity * currentPrice
                }

                val prompt = "Sen samimi bir 'Borsa Profesörü' karakterisin. Portföyümün bugünkü durumuna göre (%${(kotlin.random.Random.nextFloat() * 4 - 2)}) tek cümlelik, esprili bir kapanış özeti yap. Toplam Portföy: $totalValue TL"
                
                val service = com.nexus.porsuk.data.remote.GeminiService(apiKey)
                val summaryText = service.generateRawContent(prompt).ifBlank { "Bugün de piyasayı izledik, her şey yolunda!" }
                com.nexus.porsuk.ui.common.NotificationHelper.sendNotification(applicationContext, "📊 Günlük Portföy Özeti", summaryText, 9999)
                settingsManager.setLastEveningNotifTime(System.currentTimeMillis())
            } catch (_: Exception) {}
        }
    }

    private suspend fun sendMorningInsight(apiKey: String, assetDao: com.nexus.porsuk.data.local.dao.AssetDao, settingsManager: com.nexus.porsuk.data.local.SettingsManager) {
        val lastMorning = try { settingsManager.lastMorningNotifTime.first() } catch (_: Exception) { 0L }
        val todayStart = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, 0); set(java.util.Calendar.MINUTE, 0); set(java.util.Calendar.SECOND, 0)
        }.timeInMillis
        
        if (lastMorning < todayStart) {
            val watchlist = assetDao.getWatchlistDirect()
            val symbols = watchlist.take(5).joinToString { it.symbol }
            try {
                val service = com.nexus.porsuk.data.remote.GeminiService(apiKey)
                val insight = service.getMorningInsight(symbols).ifBlank { "Piyasalar açılıyor, bol kazançlar!" }
                com.nexus.porsuk.ui.common.NotificationHelper.sendNotification(applicationContext, "🔮 Orakul Sabah Bülteni", insight, 7777)
                settingsManager.setLastMorningNotifTime(System.currentTimeMillis())
            } catch (_: Exception) {}
        }
    }
}
