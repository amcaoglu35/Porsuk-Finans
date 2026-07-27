package com.nexus.porsuk.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nexus.porsuk.data.remote.CalendarAiEngine
import com.nexus.porsuk.domain.repository.CalendarRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class DailySummaryWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface DailySummaryEntryPoint {
        fun calendarRepository(): CalendarRepository
        fun calendarAiEngine(): CalendarAiEngine
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val entryPoint = EntryPointAccessors.fromApplication(
                applicationContext,
                DailySummaryEntryPoint::class.java
            )
            val repository = entryPoint.calendarRepository()
            val aiEngine = entryPoint.calendarAiEngine()

            val events = repository.getAllEvents().first()
            val summary = aiEngine.generateDailySummary(events)
            
            // Logic to save summary to DataStore or Room for UI to show
            // For now, let's assume it's stored and UI will fetch it
            
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    companion object {
        fun schedule(context: Context) {
            val request = androidx.work.PeriodicWorkRequestBuilder<DailySummaryWorker>(
                24, java.util.concurrent.TimeUnit.HOURS
            ).build()

            androidx.work.WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "PorsukDailySummary",
                androidx.work.ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }
}
