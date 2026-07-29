package com.nexus.porsuk.data.remote.datasource

import com.nexus.porsuk.core.common.NetworkResult
import com.nexus.porsuk.core.network.BaseRemoteDataSource
import com.nexus.porsuk.core.network.ErrorHandler
import com.nexus.porsuk.data.remote.api.FmpApi
import com.nexus.porsuk.data.remote.api.FmpHistoricalResponseDto
import com.nexus.porsuk.data.remote.api.FmpProfileDto
import com.nexus.porsuk.data.remote.api.FmpQuoteDto
import javax.inject.Inject
import javax.inject.Singleton

interface FmpRemoteDataSource {
    suspend fun getCompanyProfile(symbol: String): NetworkResult<List<FmpProfileDto>>
    suspend fun getQuote(symbol: String): NetworkResult<List<FmpQuoteDto>>
    suspend fun getQuoteBatch(symbols: List<String>): NetworkResult<List<FmpQuoteDto>>
    suspend fun getHistoricalPrice(symbol: String, timeseries: Int = 30): NetworkResult<FmpHistoricalResponseDto>
}

@Singleton
class FmpRemoteDataSourceImpl @Inject constructor(
    private val fmpApi: FmpApi,
    errorHandler: ErrorHandler
) : BaseRemoteDataSource(errorHandler), FmpRemoteDataSource {

    override suspend fun getCompanyProfile(symbol: String): NetworkResult<List<FmpProfileDto>> {
        return safeApiCall { fmpApi.getCompanyProfile(symbol) }
    }

    override suspend fun getQuote(symbol: String): NetworkResult<List<FmpQuoteDto>> {
        return safeApiCall { fmpApi.getQuote(symbol) }
    }

    override suspend fun getQuoteBatch(symbols: List<String>): NetworkResult<List<FmpQuoteDto>> {
        val symbolsString = symbols.joinToString(",")
        return safeApiCall { fmpApi.getQuoteBatch(symbolsString) }
    }

    override suspend fun getHistoricalPrice(symbol: String, timeseries: Int): NetworkResult<FmpHistoricalResponseDto> {
        return safeApiCall { fmpApi.getHistoricalPrice(symbol, timeseries) }
    }
}
