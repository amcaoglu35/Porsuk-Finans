package com.nexus.porsuk.data.provider

import com.nexus.porsuk.core.common.NetworkResult
import com.nexus.porsuk.domain.model.AssetCategory
import com.nexus.porsuk.domain.model.MarketQuote
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Porsuk Market Engine — Akıllı Sağlayıcı Yönlendiricisi (Market Data Provider Router)
 *
 * İstekte bulunulan sembolün kategorisine (Hisse, Döviz, Emtia, Kripto, Endeks) göre
 * en uygun aktif sağlayıcıya (Finnhub, Alpha Vantage, Polygon, Twelve Data) isteği yönlendirir.
 * Bir sağlayıcı hata verirse otomatik olarak yedek (fallback) sağlayıcıya geçer.
 */
@Singleton
class MarketDataProviderRouter @Inject constructor(
    private val providers: Set<@JvmSuppressWildcards MarketDataProvider>
) {

    /**
     * Sembol için en uygun sağlayıcı üzerinden fiyat verisini çeker.
     */
    suspend fun fetchQuote(symbol: String): NetworkResult<MarketQuote> {
        val category = AssetCategory.fromSymbol(symbol)
        val matchingProviders = providers.filter { it.supportsCategory(category) }

        if (matchingProviders.isEmpty()) {
            // Varsayılan sağlayıcıya fallback yap
            val defaultProvider = providers.firstOrNull()
                ?: return NetworkResult.Error(com.nexus.porsuk.core.common.DataError.Network.NOT_FOUND, "Aktif veri sağlayıcısı bulunamadı.")
            return defaultProvider.getQuote(symbol)
        }

        for (provider in matchingProviders) {
            when (val result = provider.getQuote(symbol)) {
                is NetworkResult.Success -> return result
                else -> continue // Bir sağlayıcı hata verirse bir sonrakini dene (Failover)
            }
        }

        return NetworkResult.Error(com.nexus.porsuk.core.common.DataError.Network.SERVER_ERROR, "Tüm sağlayıcılar başarısız oldu.")
    }
}
