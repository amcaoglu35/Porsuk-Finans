package com.nexus.porsuk.data.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nexus.porsuk.data.provider.MarketDataProviderRouter
import com.nexus.porsuk.domain.model.AssetCategory
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Porsuk Market Engine — WorkManager Arka Plan Fiyat Güncelleme Görevi
 */
@HiltWorker
class MarketDataSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val providerRouter: MarketDataProviderRouter,
    private val marketHoursManager: MarketHoursManager
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        // Piyasa kapalıysa arka plan güncellemesini atla ve tasarruf sağla
        if (!marketHoursManager.isMarketOpen(AssetCategory.BIST_STOCK) &&
            !marketHoursManager.isMarketOpen(AssetCategory.NASDAQ_STOCK)
        ) {
            return Result.success()
        }

        val trackSymbols = listOf("THYAO.IS", "GARAN.IS", "AAPL", "MSFT", "BTCUSDT", "USDTRY", "GAU")
        trackSymbols.forEach { symbol ->
            providerRouter.fetchQuote(symbol)
        }

        return Result.success()
    }
}
