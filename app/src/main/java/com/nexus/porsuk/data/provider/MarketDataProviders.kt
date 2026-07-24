package com.nexus.porsuk.data.provider

import com.nexus.porsuk.core.common.NetworkResult
import com.nexus.porsuk.data.remote.datasource.FinnhubMarketRemoteDataSource
import com.nexus.porsuk.domain.model.AssetCategory
import com.nexus.porsuk.domain.model.MarketQuote
import com.nexus.porsuk.domain.model.ProviderType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FinnhubDataProviderImpl @Inject constructor(
    private val remoteDataSource: FinnhubMarketRemoteDataSource
) : MarketDataProvider {

    override val providerType: ProviderType = ProviderType.FINNHUB
    override val supportedCategories: List<AssetCategory> = listOf(
        AssetCategory.BIST_STOCK,
        AssetCategory.NASDAQ_STOCK,
        AssetCategory.NYSE_STOCK,
        AssetCategory.EUROPE_STOCK,
        AssetCategory.ETF
    )

    override suspend fun getQuote(symbol: String): NetworkResult<MarketQuote> {
        return when (val result = remoteDataSource.getCompanyProfile(symbol)) {
            is NetworkResult.Success -> {
                val dto = result.data
                NetworkResult.Success(
                    MarketQuote(
                        symbol = symbol,
                        name = dto.name ?: symbol,
                        market = dto.exchange ?: "GLOBAL",
                        category = AssetCategory.fromSymbol(symbol),
                        currency = dto.currency ?: "USD",
                        lastPrice = 0.0,
                        dailyChange = 0.0,
                        dailyChangePct = 0.0,
                        marketCap = dto.marketCap,
                        lastUpdateTime = System.currentTimeMillis()
                    )
                )
            }
            is NetworkResult.Error -> result
            is NetworkResult.Exception -> result
            is NetworkResult.Loading -> NetworkResult.Loading
        }
    }

    override suspend fun getQuotes(symbols: List<String>): NetworkResult<List<MarketQuote>> {
        return NetworkResult.Success(emptyList())
    }
}

/**
 * Alpha Vantage Sağlayıcısı (Geleceğe Hazır / Mock & Stub)
 */
@Singleton
class AlphaVantageDataProviderImpl @Inject constructor() : MarketDataProvider {
    override val providerType: ProviderType = ProviderType.ALPHA_VANTAGE
    override val supportedCategories: List<AssetCategory> = listOf(AssetCategory.CURRENCY, AssetCategory.COMMODITY)

    override suspend fun getQuote(symbol: String): NetworkResult<MarketQuote> {
        return NetworkResult.Success(
            MarketQuote(
                symbol = symbol,
                name = symbol,
                market = "FOREX",
                category = AssetCategory.CURRENCY,
                currency = "TRY",
                lastPrice = 32.50,
                dailyChange = 0.10,
                dailyChangePct = 0.31
            )
        )
    }

    override suspend fun getQuotes(symbols: List<String>): NetworkResult<List<MarketQuote>> = NetworkResult.Success(emptyList())
}

/**
 * Polygon.io Sağlayıcısı (Geleceğe Hazır / Mock & Stub)
 */
@Singleton
class PolygonDataProviderImpl @Inject constructor() : MarketDataProvider {
    override val providerType: ProviderType = ProviderType.POLYGON
    override val supportedCategories: List<AssetCategory> = listOf(AssetCategory.CRYPTO, AssetCategory.NASDAQ_STOCK)

    override suspend fun getQuote(symbol: String): NetworkResult<MarketQuote> {
        return NetworkResult.Success(
            MarketQuote(
                symbol = symbol,
                name = "Bitcoin",
                market = "CRYPTO",
                category = AssetCategory.CRYPTO,
                currency = "USD",
                lastPrice = 65000.0,
                dailyChange = 1200.0,
                dailyChangePct = 1.88
            )
        )
    }

    override suspend fun getQuotes(symbols: List<String>): NetworkResult<List<MarketQuote>> = NetworkResult.Success(emptyList())
}

/**
 * Twelve Data Sağlayıcısı (Geleceğe Hazır / Mock & Stub)
 */
@Singleton
class TwelveDataDataProviderImpl @Inject constructor() : MarketDataProvider {
    override val providerType: ProviderType = ProviderType.TWELVE_DATA
    override val supportedCategories: List<AssetCategory> = listOf(AssetCategory.INDEX)

    override suspend fun getQuote(symbol: String): NetworkResult<MarketQuote> {
        return NetworkResult.Success(
            MarketQuote(
                symbol = symbol,
                name = "BIST 100",
                market = "BIST",
                category = AssetCategory.INDEX,
                currency = "TRY",
                lastPrice = 10800.0,
                dailyChange = 150.0,
                dailyChangePct = 1.41
            )
        )
    }

    override suspend fun getQuotes(symbols: List<String>): NetworkResult<List<MarketQuote>> = NetworkResult.Success(emptyList())
}
