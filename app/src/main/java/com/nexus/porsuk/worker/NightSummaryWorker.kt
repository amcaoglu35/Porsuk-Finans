package com.nexus.porsuk.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.nexus.porsuk.data.repository.FinanceRepository
import com.nexus.porsuk.ui.common.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.*
import java.util.concurrent.TimeUnit

@HiltWorker
class NightSummaryWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: FinanceRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        try {
            val assets = repository.getConsolidatedAssetsFlow().first()
            if (assets.isEmpty()) return Result.success()

            val totalValue = assets.sumOf { it.totalValue }
            val totalPnl = assets.sumOf { it.profitLoss }
            val pnlPct = if (totalValue > 0) (totalPnl / totalValue) * 100 else 0.0

            val message = "🌙 Portföy Günlük Özeti: Toplam Varlık ₺${"%,.2f".format(totalValue)}. " +
                    "Günlük Değişim: %${"%.2f".format(pnlPct)}. Detaylı rapor AI Lab'da."

            NotificationHelper.sendNotification(
                applicationContext,
                "📊 Porsuk Gece Özeti",
                message,
                1002
            )

            return Result.success()
        } catch (e: Exception) {
            return Result.failure()
        }
    }

    companion object {
        private const val WORK_NAME = "NightSummaryWorker"

        fun schedule(context: Context) {
            val currentDate = Calendar.getInstance()
            val dueDate = Calendar.getInstance()

            // Set Execution time to 21:00
            dueDate.set(Calendar.HOUR_OF_DAY, 21)
            dueDate.set(Calendar.MINUTE, 0)
            dueDate.set(Calendar.SECOND, 0)

            if (dueDate.before(currentDate)) {
                dueDate.add(Calendar.HOUR_OF_DAY, 24)
            }

            val timeDiff = dueDate.timeInMillis - currentDate.timeInMillis

            val request = PeriodicWorkRequestBuilder<NightSummaryWorker>(24, TimeUnit.HOURS)
                .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
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
}
