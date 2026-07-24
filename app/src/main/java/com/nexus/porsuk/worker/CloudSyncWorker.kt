package com.nexus.porsuk.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nexus.porsuk.domain.repository.SyncRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

/**
 * Porsuk Cloud Sync Platform — WorkManager Arka Plan Senkronizasyon Çalıştırıcısı (CloudSyncWorker)
 */
@HiltWorker
class CloudSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val syncRepository: SyncRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            syncRepository.triggerManualSync().first()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
