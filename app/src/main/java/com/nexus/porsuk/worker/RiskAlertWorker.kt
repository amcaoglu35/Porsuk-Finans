package com.nexus.porsuk.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.nexus.porsuk.data.repository.FinanceRepository
import com.nexus.porsuk.data.remote.PortfolioDoctorEngine
import com.nexus.porsuk.ui.common.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

@HiltWorker
class RiskAlertWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: FinanceRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        try {
            val items = repository.allBasketItems.first()
            val companies = repository.allCompanies.first()
            
            if (items.isEmpty()) return Result.success()
            
            val metrics = PortfolioDoctorEngine.analyze(items, companies)
            val riskScore = 100 - metrics.healthScore
            
            // Heuristic: If risk score > 75, send alert
            if (riskScore > 75) {
                NotificationHelper.sendNotification(
                    applicationContext,
                    "⚠️ Portföy Risk Uyarısı",
                    "Portföyünüzün risk skoru kritik seviyeye ($riskScore) ulaştı. Detaylar için AI Lab'ı ziyaret edin.",
                    1001
                )
            }
            
            return Result.success()
        } catch (_: Exception) {
            return Result.retry()
        }
    }

    companion object {
        private const val WORK_NAME = "RiskAlertWorker"

        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val request = PeriodicWorkRequestBuilder<RiskAlertWorker>(1, TimeUnit.HOURS)
                .setConstraints(constraints)
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
