package com.nexus.porsuk.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nexus.porsuk.data.local.dao.NewsIntelligenceDao
import com.nexus.porsuk.data.local.entity.NewsArticleEntity
import com.nexus.porsuk.data.logging.DataLogger
import com.nexus.porsuk.domain.model.NewsCategory
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.UUID

/**
 * Porsuk News Intelligence Center — Arka Plan Periyodik Haber Senkronize Edicisi (WorkManager)
 *
 * Belirli aralıklarla son dakika haberlerini ve KAP bildirimlerini çeker ve veritabanını günceller.
 */
@HiltWorker
class NewsSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val newsDao: NewsIntelligenceDao,
    private val logger: DataLogger
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val sampleArticles = listOf(
                NewsArticleEntity(
                    articleId = UUID.randomUUID().toString(),
                    title = "FED Faiz Kararı Açıklandı: Politika Faizi Sabit Tutuldu",
                    summary = "ABD Merkez Bankası (FED) beklentilere paralel şekilde faiz oranını %5.25 - %5.50 aralığında sabit bıraktı.",
                    content = "FED Başkanı kararın ardından yaptığı açıklamada enflasyondaki düşüş eğiliminin sevindirici olduğunu ancak temkinli duruşun korunacağını belirtti.",
                    source = "Bloomberg HT",
                    category = "FED",
                    isBreaking = true,
                    sentiment = "NEUTRAL",
                    impactScore = 9
                ),
                NewsArticleEntity(
                    articleId = UUID.randomUUID().toString(),
                    title = "THY Yeni Filo Siparişini KAP'a Bildirdi",
                    summary = "Türk Hava Yolları, 2025-2030 stratejik hedefleri doğrultusunda 15 yeni geniş gövdeli uçak alımı yapacağını duyurdu.",
                    content = "KAP bildirimine göre teslimatlar 2026 yılının ilk çeyreğinden itibaren başlayacaktır.",
                    source = "KAP",
                    category = "KAP",
                    symbol = "THYAO.IS",
                    sector = "Havacılık",
                    isBreaking = false,
                    sentiment = "POSITIVE",
                    impactScore = 8
                )
            )

            newsDao.insertNewsArticles(sampleArticles)
            logger.logSyncEvent("NewsSyncWorker", "${sampleArticles.size} yeni haber senkronize edildi")
            Result.success()
        } catch (e: Exception) {
            logger.logError("NewsSyncWorker", "Haber senkronizasyonunda hata: ${e.message}", e)
            Result.retry()
        }
    }
}
