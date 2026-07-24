package com.nexus.porsuk.data.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nexus.porsuk.core.common.NetworkResult
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Porsuk Data Center — Android WorkManager Arka Plan Senkronizasyon Çalıştırıcısı
 *
 * Cihaz şarjdayken veya internete bağlandığında periyodik veri senkronizasyonunu güvenle çalıştırır.
 */
@HiltWorker
class PorsukSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val syncEngine: PorsukSyncEngine
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return when (syncEngine.syncAll()) {
            is NetworkResult.Success -> Result.success()
            is NetworkResult.Error -> Result.retry()
            is NetworkResult.Exception -> Result.failure()
            is NetworkResult.Loading -> Result.retry()
        }
    }
}
