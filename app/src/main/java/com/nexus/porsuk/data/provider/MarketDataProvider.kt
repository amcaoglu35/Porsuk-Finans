package com.nexus.porsuk.data.provider

import com.nexus.porsuk.core.common.NetworkResult
import com.nexus.porsuk.domain.model.AssetCategory
import com.nexus.porsuk.domain.model.MarketQuote
import com.nexus.porsuk.domain.model.ProviderType

/**
 * Porsuk Market Engine — Sağlayıcı Arayüzü (Market Data Provider Contract)
 *
 * Farklı finansal veri sağlayıcılarının (Finnhub, Alpha Vantage, Polygon, Twelve Data)
 * sisteme standart bir interface ile bağlanmasını sağlar.
 */
interface MarketDataProvider {
    val providerType: ProviderType
    val supportedCategories: List<AssetCategory>

    suspend fun getQuote(symbol: String): NetworkResult<MarketQuote>
    suspend fun getQuotes(symbols: List<String>): NetworkResult<List<MarketQuote>>
    fun supportsCategory(category: AssetCategory): Boolean = supportedCategories.contains(category)
}
