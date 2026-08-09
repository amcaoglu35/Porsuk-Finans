package com.nexus.porsuk.worker

import android.content.Context
import androidx.work.*
import com.nexus.porsuk.domain.repository.IpoRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import androidx.hilt.work.HiltWorker
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

@HiltWorker
class IpoAlertWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: IpoRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        try {
            // Check for upcoming IPOs in the next 24 hours
            // This is a placeholder for actual logic
            return Result.success()
        } catch (e: Exception) {
            return Result.retry()
        }
    }

    companion object {
        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<IpoAlertWorker>(24, TimeUnit.HOURS)
                .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "IpoAlertWorker",
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }
}
