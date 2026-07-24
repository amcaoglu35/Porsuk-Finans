package com.nexus.porsuk.data.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Porsuk TEFAS Engine — WorkManager Arka Plan Senkronizasyon Çalıştırıcısı
 *
 * Her gün otomatik olarak çalışarak TEFAS yatırım fonlarını arka planda günceller.
 */
@HiltWorker
class TefasSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val syncService: TefasSyncService
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val report = syncService.syncAllFunds()
        return if (report.isSuccess) {
            Result.success()
        } else {
            Result.retry()
        }
    }
}
