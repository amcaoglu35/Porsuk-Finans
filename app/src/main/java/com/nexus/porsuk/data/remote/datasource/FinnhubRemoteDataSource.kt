package com.nexus.porsuk.data.remote.datasource

import com.nexus.porsuk.core.common.NetworkResult
import com.nexus.porsuk.data.local.entity.CompanyEntity
import com.nexus.porsuk.data.local.entity.MarketQuoteEntity
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Porsuk Data Center — Finnhub ve BIST Uzak Veri Kaynağı Arayüzü (Remote Data Source Contract)
 */
interface FinnhubRemoteDataSource {
    suspend fun fetchAllCompanies(): NetworkResult<List<CompanyEntity>>
    suspend fun fetchCompanyQuote(symbol: String): NetworkResult<MarketQuoteEntity>
    suspend fun fetchMarketQuotes(symbols: List<String>): NetworkResult<List<MarketQuoteEntity>>
}

/**
 * FinnhubRemoteDataSource Somut Sınıfı (Architecture-Ready / Mock & Framework Stub)
 */
@Singleton
class FinnhubRemoteDataSourceImpl @Inject constructor() : FinnhubRemoteDataSource {

    override suspend fun fetchAllCompanies(): NetworkResult<List<CompanyEntity>> {
        // Gerçek API entegrasyonu aşamasında Retrofit / Ktor çağrısı buraya eklenecektir.
        return NetworkResult.Success(emptyList())
    }

    override suspend fun fetchCompanyQuote(symbol: String): NetworkResult<MarketQuoteEntity> {
        return NetworkResult.Success(
            MarketQuoteEntity(
                symbol = symbol,
                currentPrice = 0.0,
                changeAmount = 0.0,
                changePct = 0.0,
                marketType = "BIST"
            )
        )
    }

    override suspend fun fetchMarketQuotes(symbols: List<String>): NetworkResult<List<MarketQuoteEntity>> {
        return NetworkResult.Success(emptyList())
    }
}
