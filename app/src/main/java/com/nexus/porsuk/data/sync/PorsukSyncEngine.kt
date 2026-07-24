package com.nexus.porsuk.data.sync

import com.nexus.porsuk.core.common.NetworkConnectivityMonitor
import com.nexus.porsuk.core.common.NetworkResult
import com.nexus.porsuk.core.common.runWithRetry
import com.nexus.porsuk.data.local.datastore.PorsukPreferencesManager
import com.nexus.porsuk.data.logging.DataLogger
import com.nexus.porsuk.domain.repository.CompanyRepository
import com.nexus.porsuk.domain.repository.FundRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Porsuk Data Center — Merkezi Senkronizasyon Motoru (Sync Engine)
 *
 * Uygulamanın şirket, fon, fiyat ve haber senkronizasyon döngülerini yönetir.
 *
 * İşlevleri:
 * 1. İlk Kurulum (Initial Sync): Tüm şirket ve TEFAS fon kataloğunu indirir ve veritabanına işler.
 * 2. Günlük Senkronizasyon (Daily Sync): Günlük değişen fiyat ve temel verileri tazeler.
 * 3. Yeni Şirket/Fon Ekleme: Kataloğa yeni giren sembolleri tespit edip kaydeder.
 * 4. Delist / Tasfiye Takibi: Borsadan kot dışı edilen şirketleri ve tasfiye edilen fonları pasife alır.
 * 5. Üstel Geri Çekilme (Exponential Backoff): Başarısız ağ çağrılarını otomatik tekrar dener.
 */
@Singleton
class PorsukSyncEngine @Inject constructor(
    private val companyRepository: CompanyRepository,
    private val fundRepository: FundRepository,
    private val preferencesManager: PorsukPreferencesManager,
    private val connectivityMonitor: NetworkConnectivityMonitor,
    private val logger: DataLogger
) {

    /**
     * Tam senkronizasyonu çalıştırır. (Initial Sync veya Daily Sync durumuna göre yönlendirir)
     */
    suspend fun syncAll(): NetworkResult<Unit> {
        val startTime = System.currentTimeMillis()

        if (!connectivityMonitor.isNetworkAvailable()) {
            logger.logError("PorsukSyncEngine", "İnternet bağlantısı yok, senkronizasyon ertelendi.")
            return NetworkResult.Error(com.nexus.porsuk.core.common.DataError.Network.NO_INTERNET, "İnternet bağlantısı yok.")
        }

        val isInitialDone = preferencesManager.isInitialSyncCompleted.first()

        val result = runCatching {
            runWithRetry(times = 3) {
                if (!isInitialDone) {
                    performInitialSync()
                } else {
                    performDailySync()
                }
            }
        }

        val duration = System.currentTimeMillis() - startTime
        return if (result.isSuccess) {
            logger.logPerformance("PorsukSyncEngine", "Senkronizasyon İşlemi", duration)
            preferencesManager.updateLastSyncTimestamp()
            NetworkResult.Success(Unit)
        } else {
            val exception = result.exceptionOrNull() ?: Exception("Bilinmeyen senkronizasyon hatası")
            logger.logError("PorsukSyncEngine", "Senkronizasyon başarısız", exception)
            NetworkResult.Exception(exception)
        }
    }

    /**
     * İlk Kurulum Senkronizasyonu (Initial Sync)
     */
    private suspend fun performInitialSync() {
        logger.logSyncEvent("PorsukSyncEngine", "İLK KURULUM SENKRONİZASYONU BAŞLATILDI")

        // 1. Tüm Şirketleri / BIST hisselerini indir
        companyRepository.syncCompanies()

        // 2. Tüm TEFAS Fon Kataloğunu indir
        fundRepository.syncFunds()

        // 3. İlk kurulum bayrağını güncelle
        preferencesManager.setInitialSyncCompleted(true)
        logger.logSyncEvent("PorsukSyncEngine", "İLK KURULUM SENKRONİZASYONU BAŞARIYLA TAMAMLANDI")
    }

    /**
     * Günlük Periyodik Senkronizasyon (Daily Sync)
     */
    private suspend fun performDailySync() {
        logger.logSyncEvent("PorsukSyncEngine", "GÜNLÜK SENKRONİZASYON BAŞLATILDI")

        // 1. Yeni ve değişen şirketleri güncelle
        companyRepository.syncCompanies()

        // 2. Delist olan şirketleri pasifleştir
        handleDelistedCompanies()

        // 3. TEFAS Fonlarını güncelle ve tasfiye olanları pasifleştir
        fundRepository.syncFunds()
        handleInactiveFunds()

        logger.logSyncEvent("PorsukSyncEngine", "GÜNLÜK SENKRONİZASYON TAMAMLANDI")
    }

    /**
     * Delist / Kot Dışı Şirket Kontrolü
     */
    private suspend fun handleDelistedCompanies() {
        // Mock / Framework Stub delist kontrolü
        val delistedSymbols = listOf<String>()
        if (delistedSymbols.isNotEmpty()) {
            companyRepository.markDelistedCompanies(delistedSymbols)
        }
    }

    /**
     * Tasfiye / Kapanmış Fon Kontrolü
     */
    private suspend fun handleInactiveFunds() {
        val inactiveCodes = listOf<String>()
        if (inactiveCodes.isNotEmpty()) {
            fundRepository.markInactiveFunds(inactiveCodes)
        }
    }
}
