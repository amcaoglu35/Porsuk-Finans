package com.nexus.porsuk.data.remote.datasource

import com.nexus.porsuk.core.common.NetworkResult
import com.nexus.porsuk.core.network.BaseRemoteDataSource
import com.nexus.porsuk.core.network.ErrorHandler
import com.nexus.porsuk.data.remote.api.FinnhubApi
import com.nexus.porsuk.data.remote.dto.FinnhubCompanyProfileDto
import com.nexus.porsuk.data.remote.dto.FinnhubMarketStatusDto
import com.nexus.porsuk.data.remote.dto.FinnhubSymbolDto
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Finnhub Uzak Veri Kaynağı Arayüzü
 */
interface FinnhubMarketRemoteDataSource {
    suspend fun getSymbolsForExchange(exchangeCode: String): NetworkResult<List<FinnhubSymbolDto>>
    suspend fun getCompanyProfile(symbol: String): NetworkResult<FinnhubCompanyProfileDto>
    suspend fun getMarketStatus(exchangeCode: String): NetworkResult<FinnhubMarketStatusDto>
}

/**
 * Finnhub Market Remote Data Source Somut Uygulaması
 */
@Singleton
class FinnhubMarketRemoteDataSourceImpl @Inject constructor(
    private val finnhubApi: FinnhubApi,
    errorHandler: ErrorHandler
) : BaseRemoteDataSource(errorHandler), FinnhubMarketRemoteDataSource {

    override suspend fun getSymbolsForExchange(exchangeCode: String): NetworkResult<List<FinnhubSymbolDto>> {
        return safeApiCall { finnhubApi.getSymbols(exchange = exchangeCode) }
    }

    override suspend fun getCompanyProfile(symbol: String): NetworkResult<FinnhubCompanyProfileDto> {
        return safeApiCall { finnhubApi.getCompanyProfile(symbol = symbol) }
    }

    override suspend fun getMarketStatus(exchangeCode: String): NetworkResult<FinnhubMarketStatusDto> {
        return safeApiCall { finnhubApi.getMarketStatus(exchange = exchangeCode) }
    }
}
