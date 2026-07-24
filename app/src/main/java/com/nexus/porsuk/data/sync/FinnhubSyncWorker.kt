package com.nexus.porsuk.data.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Porsuk Finnhub Engine — WorkManager Arka Plan Senkronizasyon Çalıştırıcısı
 *
 * Uygulama kapalı olsa dahi şirket veri tabanının güncel kalması için periyodik senkronizasyonu tetikler.
 */
@HiltWorker
class FinnhubSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val syncService: FinnhubCompanySyncService
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val report = syncService.syncAllExchanges()
        return if (report.isSuccess) {
            Result.success()
        } else {
            Result.retry()
        }
    }
}
