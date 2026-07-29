package com.nexus.porsuk.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nexus.porsuk.domain.model.CalendarImpactLevel
import com.nexus.porsuk.domain.repository.CalendarRepository
import com.nexus.porsuk.ui.common.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class MacroNotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val calendarRepository: CalendarRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        try {
            calendarRepository.refreshEvents()
            val highImpactEvents = calendarRepository.getAllEvents().first()
                .filter { it.impactLevel == CalendarImpactLevel.HIGH }
                .filter { it.eventTime > System.currentTimeMillis() && it.eventTime < System.currentTimeMillis() + 3600000 } // Next 1 hour

            highImpactEvents.forEach { event ->
                NotificationHelper.sendNotification(
                    applicationContext,
                    "🔥 Kritik Ekonomik Veri: ${event.title}",
                    "${event.country} piyasalarında yüksek hareketlilik beklentisi! Saat: ${formatTime(event.eventTime)}",
                    event.eventId.hashCode()
                )
            }
            return Result.success()
        } catch (e: Exception) {
            return Result.retry()
        }
    }

    private fun formatTime(time: Long): String {
        return java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date(time))
    }
}
