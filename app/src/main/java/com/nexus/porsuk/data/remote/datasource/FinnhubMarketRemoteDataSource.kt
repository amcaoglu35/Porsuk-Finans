package com.nexus.porsuk.data.remote.datasource

import com.nexus.porsuk.core.common.NetworkResult
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
    private val finnhubApi: FinnhubApi
) : FinnhubMarketRemoteDataSource {

    override suspend fun getSymbolsForExchange(exchangeCode: String): NetworkResult<List<FinnhubSymbolDto>> {
        return try {
            val response = finnhubApi.getSymbols(exchange = exchangeCode)
            NetworkResult.Success(response)
        } catch (e: Throwable) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun getCompanyProfile(symbol: String): NetworkResult<FinnhubCompanyProfileDto> {
        return try {
            val profile = finnhubApi.getCompanyProfile(symbol = symbol)
            NetworkResult.Success(profile)
        } catch (e: Throwable) {
            NetworkResult.Exception(e)
        }
    }

    override suspend fun getMarketStatus(exchangeCode: String): NetworkResult<FinnhubMarketStatusDto> {
        return try {
            val status = finnhubApi.getMarketStatus(exchange = exchangeCode)
            NetworkResult.Success(status)
        } catch (e: Throwable) {
            NetworkResult.Exception(e)
        }
    }
}
