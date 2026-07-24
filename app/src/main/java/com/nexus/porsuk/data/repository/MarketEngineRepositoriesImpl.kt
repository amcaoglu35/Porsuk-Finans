package com.nexus.porsuk.data.repository

import com.nexus.porsuk.core.common.NetworkResult
import com.nexus.porsuk.data.local.cache.CacheManager
import com.nexus.porsuk.data.local.dao.MarketQuoteDao
import com.nexus.porsuk.data.local.entity.MarketQuoteEntity
import com.nexus.porsuk.data.provider.MarketDataProviderRouter
import com.nexus.porsuk.data.websocket.MarketTick
import com.nexus.porsuk.data.websocket.MarketWebSocketClient
import com.nexus.porsuk.domain.model.AssetCategory
import com.nexus.porsuk.domain.model.MarketQuote
import com.nexus.porsuk.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MarketEngineRepositoryImpl @Inject constructor(
    private val quoteDao: MarketQuoteDao,
    private val providerRouter: MarketDataProviderRouter,
    private val cacheManager: CacheManager
) : MarketRepository {

    override fun getQuotesByCategory(category: AssetCategory): Flow<List<MarketQuote>> {
        return quoteDao.getAllQuotes().map { list ->
            list.filter { it.marketType == category.name }.map { it.toDomainModel() }
        }
    }

    override fun searchMarketInstruments(query: String): Flow<List<MarketQuote>> {
        return quoteDao.getAllQuotes().map { list ->
            list.filter { it.symbol.contains(query, ignoreCase = true) }.map { it.toDomainModel() }
        }
    }

    override suspend fun refreshQuote(symbol: String): NetworkResult<MarketQuote> {
        val result = providerRouter.fetchQuote(symbol)
        if (result is NetworkResult.Success) {
            quoteDao.insertOrUpdateQuotes(listOf(result.data.toEntityModel()))
            cacheManager.put("quote_$symbol", result.data)
        }
        return result
    }
}

@Singleton
class PriceRepositoryImpl @Inject constructor(
    private val quoteDao: MarketQuoteDao,
    private val providerRouter: MarketDataProviderRouter,
    private val webSocketClient: MarketWebSocketClient
) : PriceRepository {

    override fun getLiveQuote(symbol: String): Flow<MarketQuote?> {
        return quoteDao.getAllQuotes().map { list ->
            list.firstOrNull { it.symbol == symbol }?.toDomainModel()
        }
    }

    override fun subscribeRealtimePriceTicks(symbols: List<String>): Flow<MarketTick> {
        return webSocketClient.subscribeTicks(symbols)
    }

    override suspend fun refreshPrice(symbol: String): NetworkResult<MarketQuote> {
        val result = providerRouter.fetchQuote(symbol)
        if (result is NetworkResult.Success) {
            quoteDao.insertOrUpdateQuotes(listOf(result.data.toEntityModel()))
        }
        return result
    }
}

@Singleton
class IndexRepositoryImpl @Inject constructor(
    private val marketRepository: MarketRepository
) : IndexRepository {
    override fun getAllIndices(): Flow<List<MarketQuote>> = marketRepository.getQuotesByCategory(AssetCategory.INDEX)
    override suspend fun getIndexQuote(symbol: String): NetworkResult<MarketQuote> = marketRepository.refreshQuote(symbol)
}

@Singleton
class CurrencyRepositoryImpl @Inject constructor(
    private val marketRepository: MarketRepository
) : CurrencyRepository {
    override fun getAllCurrencies(): Flow<List<MarketQuote>> = marketRepository.getQuotesByCategory(AssetCategory.CURRENCY)
    override suspend fun getCurrencyPair(pair: String): NetworkResult<MarketQuote> = marketRepository.refreshQuote(pair)
}

@Singleton
class CommodityRepositoryImpl @Inject constructor(
    private val marketRepository: MarketRepository
) : CommodityRepository {
    override fun getAllCommodities(): Flow<List<MarketQuote>> = marketRepository.getQuotesByCategory(AssetCategory.COMMODITY)
    override suspend fun getCommodityQuote(symbol: String): NetworkResult<MarketQuote> = marketRepository.refreshQuote(symbol)
}

@Singleton
class CryptoRepositoryImpl @Inject constructor(
    private val marketRepository: MarketRepository
) : CryptoRepository {
    override fun getTopCryptoAssets(): Flow<List<MarketQuote>> = marketRepository.getQuotesByCategory(AssetCategory.CRYPTO)
    override suspend fun getCryptoQuote(symbol: String): NetworkResult<MarketQuote> = marketRepository.refreshQuote(symbol)
}

// Mapper Mappings
private fun MarketQuoteEntity.toDomainModel() = MarketQuote(
    symbol = symbol,
    name = symbol,
    market = marketType,
    category = AssetCategory.fromSymbol(symbol),
    currency = "TRY",
    lastPrice = currentPrice,
    dailyChange = changeAmount,
    dailyChangePct = changePct,
    open = dayLow,
    high = dayHigh,
    low = dayLow,
    volume = volume,
    lastUpdateTime = lastUpdatedMs
)

private fun MarketQuote.toEntityModel() = MarketQuoteEntity(
    symbol = symbol,
    currentPrice = lastPrice,
    changeAmount = dailyChange,
    changePct = dailyChangePct,
    dayHigh = high,
    dayLow = low,
    volume = volume,
    marketType = category.name,
    lastUpdatedMs = lastUpdateTime
)
