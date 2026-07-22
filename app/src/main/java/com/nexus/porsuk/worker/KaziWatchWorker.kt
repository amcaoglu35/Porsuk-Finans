package com.nexus.porsuk.worker

import android.content.Context
import android.util.Log
import androidx.work.*
import com.nexus.porsuk.data.local.entity.KaziWatch
import com.nexus.porsuk.ui.FinanceViewModelFactory
import com.nexus.porsuk.ui.common.NotificationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class KaziWatchWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val kaziRepo = FinanceViewModelFactory.getKaziRepository(applicationContext)
        val apiKey = FinanceViewModelFactory.getSettingsManager(applicationContext).getGeminiApiKey()

        if (apiKey.isNullOrBlank()) return@withContext Result.success()

        try {
            val watches: List<KaziWatch> = kaziRepo.getAllKaziWatches().first()
            if (watches.isEmpty()) return@withContext Result.success()

            watches.forEach { watch ->
                // Recalculate score (Mock logic for now)
                val currentScore = watch.lastScore
                val newScore = (currentScore + (-5..5).random()).coerceIn(0, 100)
                
                if (currentScore - newScore >= watch.notifyThreshold) {
                    NotificationHelper.sendNotification(
                        applicationContext,
                        "KAZI İzleme Uyarısı: ${watch.symbol}",
                        "Hissenin KAZI skoru önemli ölçüde düştü! ($currentScore -> $newScore)",
                        watch.id
                    )
                }
                kaziRepo.insertKaziWatch(watch.copy(
                    lastScore = newScore,
                    scoreHistory = "${watch.scoreHistory},$newScore"
                ))
            }
            Result.success()
        } catch (e: Exception) {
            Log.e("KaziWatchWorker", "Tracking failure", e)
            Result.retry()
        }
    }
}
