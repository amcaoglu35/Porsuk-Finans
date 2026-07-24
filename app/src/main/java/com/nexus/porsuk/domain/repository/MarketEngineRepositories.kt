package com.nexus.porsuk.domain.repository

import com.nexus.porsuk.core.common.NetworkResult
import com.nexus.porsuk.data.websocket.MarketTick
import com.nexus.porsuk.domain.model.AssetCategory
import com.nexus.porsuk.domain.model.MarketQuote
import kotlinx.coroutines.flow.Flow

/**
 * 1. Genel Piyasa Veri Deposu Sözleşmesi
 */
interface MarketRepository {
    fun getQuotesByCategory(category: AssetCategory): Flow<List<MarketQuote>>
    fun searchMarketInstruments(query: String): Flow<List<MarketQuote>>
    suspend fun refreshQuote(symbol: String): NetworkResult<MarketQuote>
}

/**
 * 2. Canlı Fiyat ve Anlık Akış Veri Deposu Sözleşmesi
 */
interface PriceRepository {
    fun getLiveQuote(symbol: String): Flow<MarketQuote?>
    fun subscribeRealtimePriceTicks(symbols: List<String>): Flow<MarketTick>
    suspend fun refreshPrice(symbol: String): NetworkResult<MarketQuote>
}

/**
 * 3. Borsa Endeksleri Veri Deposu Sözleşmesi (BIST100, S&P500, NASDAQ100, DAX)
 */
interface IndexRepository {
    fun getAllIndices(): Flow<List<MarketQuote>>
    suspend fun getIndexQuote(symbol: String): NetworkResult<MarketQuote>
}

/**
 * 4. Döviz Kurları Veri Deposu Sözleşmesi (USD/TRY, EUR/TRY vb.)
 */
interface CurrencyRepository {
    fun getAllCurrencies(): Flow<List<MarketQuote>>
    suspend fun getCurrencyPair(pair: String): NetworkResult<MarketQuote>
}

/**
 * 5. Emtia Veri Deposu Sözleşmesi (Altın, Gümüş, Petrol, Doğalgaz)
 */
interface CommodityRepository {
    fun getAllCommodities(): Flow<List<MarketQuote>>
    suspend fun getCommodityQuote(symbol: String): NetworkResult<MarketQuote>
}

/**
 * 6. Kripto Paralar Veri Deposu Sözleşmesi (Bitcoin, Ethereum vb.)
 */
interface CryptoRepository {
    fun getTopCryptoAssets(): Flow<List<MarketQuote>>
    suspend fun getCryptoQuote(symbol: String): NetworkResult<MarketQuote>
}
