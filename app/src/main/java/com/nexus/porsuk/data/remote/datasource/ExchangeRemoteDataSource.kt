package com.nexus.porsuk.data.remote.datasource

import com.nexus.porsuk.core.common.NetworkResult
import com.nexus.porsuk.core.network.BaseRemoteDataSource
import com.nexus.porsuk.core.network.ErrorHandler
import com.nexus.porsuk.data.remote.api.ExchangeRateApi
import com.nexus.porsuk.data.remote.api.ExchangeRateResponseDto
import javax.inject.Inject
import javax.inject.Singleton

interface ExchangeRemoteDataSource {
    suspend fun getRates(base: String = "USD", symbols: String? = "TRY,EUR,GBP,JPY"): NetworkResult<ExchangeRateResponseDto>
}

@Singleton
class ExchangeRemoteDataSourceImpl @Inject constructor(
    private val exchangeRateApi: ExchangeRateApi,
    errorHandler: ErrorHandler
) : BaseRemoteDataSource(errorHandler), ExchangeRemoteDataSource {

    override suspend fun getRates(base: String, symbols: String?): NetworkResult<ExchangeRateResponseDto> {
        return safeApiCall { exchangeRateApi.getLatestRates(base = base, symbols = symbols) }
    }
}
