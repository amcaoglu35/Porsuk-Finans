package com.nexus.porsuk.data.logging

import android.util.Log
import com.nexus.porsuk.data.local.dao.SyncLogDao
import com.nexus.porsuk.data.local.entity.SyncLogEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Porsuk Data Center — Merkezi Günlükleme Sistemi (Logging System)
 *
 * Senkronizasyon süreçlerini, API hatalarını ve performans çalışma sürelerini (Performance metrics)
 * hem Logcat'e hem de yerel `SyncLogEntity` Room veritabanına kaydeder.
 */
@Singleton
class DataLogger @Inject constructor(
    private val syncLogDao: SyncLogDao
) {

    private val scope = CoroutineScope(Dispatchers.IO)

    /**
     * Senkronizasyon adımını veya olayını kaydeder.
     */
    fun logSyncEvent(tag: String, message: String, isSuccess: Boolean = true) {
        Log.i("PorsukDataSync", "[$tag] $message")
        persistLog(tag = tag, message = message, type = "SYNC", status = if (isSuccess) "SUCCESS" else "FAILED")
    }

    /**
     * API veya sistem hatalarını kaydeder.
     */
    fun logError(tag: String, message: String, throwable: Throwable? = null) {
        Log.e("PorsukDataError", "[$tag] $message", throwable)
        persistLog(tag = tag, message = "$message | ${throwable?.message ?: ""}", type = "ERROR", status = "FAILED")
    }

    /**
     * Bir işlemin performans ve çalışma süresini loglar.
     */
    fun logPerformance(tag: String, operationName: String, timeTakenMs: Long) {
        Log.d("PorsukDataPerf", "[$tag] $operationName tamamlandı. Süre: ${timeTakenMs}ms")
        if (timeTakenMs > 2000) { // 2 saniyeden uzun süren yavaş işlemleri kaydet
            persistLog(tag = tag, message = "$operationName yavaş çalıştı: ${timeTakenMs}ms", type = "PERFORMANCE", status = "WARNING")
        }
    }

    private fun persistLog(tag: String, message: String, type: String, status: String) {
        scope.launch {
            try {
                syncLogDao.insertLog(
                    SyncLogEntity(
                        tag = tag,
                        message = message,
                        type = type,
                        status = status,
                        timestamp = System.currentTimeMillis()
                    )
                )
            } catch (e: Exception) {
                Log.w("PorsukDataLogger", "Log DB kaydı başarısız: ${e.message}")
            }
        }
    }
}
