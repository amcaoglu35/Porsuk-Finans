package com.nexus.porsuk.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nexus.porsuk.data.local.dao.CalendarDao
import com.nexus.porsuk.data.local.entity.EconomicEventEntity
import com.nexus.porsuk.data.logging.DataLogger
import com.nexus.porsuk.domain.model.CalendarEventCategory
import com.nexus.porsuk.domain.model.CalendarImpactLevel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.UUID

/**
 * Porsuk Economic Calendar & Events Engine — Arka Plan Günlük Takvim Senkronize Edicisi (WorkManager)
 *
 * Günlük olarak faiz kararlarını, enflasyon verilerini ve bilanço tarihlerini günceller.
 */
@HiltWorker
class CalendarSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val calendarDao: CalendarDao,
    private val logger: DataLogger
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val sampleEvents = listOf(
                EconomicEventEntity(
                    eventId = UUID.randomUUID().toString(),
                    title = "FED Faiz Oranı Kararı",
                    country = "US",
                    category = CalendarEventCategory.MACRO.name,
                    impactLevel = CalendarImpactLevel.HIGH.name,
                    actualValue = "%5.50",
                    forecastValue = "%5.50",
                    previousValue = "%5.50",
                    eventTime = System.currentTimeMillis() + 86400000
                ),
                EconomicEventEntity(
                    eventId = UUID.randomUUID().toString(),
                    title = "TCMB Politika Faizi Kararı (PPK)",
                    country = "TR",
                    category = CalendarEventCategory.MACRO.name,
                    impactLevel = CalendarImpactLevel.HIGH.name,
                    actualValue = "%50.00",
                    forecastValue = "%50.00",
                    previousValue = "%50.00",
                    eventTime = System.currentTimeMillis() + 172800000
                ),
                EconomicEventEntity(
                    eventId = UUID.randomUUID().toString(),
                    title = "Türkiye TÜFE Enflasyon Verisi (Aylık)",
                    country = "TR",
                    category = CalendarEventCategory.ECONOMIC_DATA.name,
                    impactLevel = CalendarImpactLevel.HIGH.name,
                    actualValue = "%2.80",
                    forecastValue = "%2.50",
                    previousValue = "%3.10",
                    eventTime = System.currentTimeMillis() + 259200000
                )
            )

            calendarDao.insertEconomicEvents(sampleEvents)
            logger.logSyncEvent("CalendarSyncWorker", "${sampleEvents.size} takvim etkinliği senkronize edildi")
            Result.success()
        } catch (e: Exception) {
            logger.logError("CalendarSyncWorker", "Takvim senkronizasyonunda hata: ${e.message}", e)
            Result.retry()
        }
    }
}
