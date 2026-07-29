package com.nexus.porsuk.data.remote.datasource

import com.nexus.porsuk.core.common.NetworkResult
import com.nexus.porsuk.core.network.BaseRemoteDataSource
import com.nexus.porsuk.core.network.ErrorHandler
import com.nexus.porsuk.data.local.entity.CompanyEntity
import com.nexus.porsuk.data.local.entity.MarketQuoteEntity
import com.nexus.porsuk.data.remote.api.FinnhubApi
import com.nexus.porsuk.data.remote.api.FinnhubEconomicCalendarDto
import com.nexus.porsuk.data.remote.dto.FinnhubCompanyProfileDto
import com.nexus.porsuk.data.remote.dto.FinnhubQuoteDto
import com.nexus.porsuk.data.remote.dto.FinnhubSymbolDto
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Porsuk Data Center — Finnhub Uzak Veri Kaynağı Arayüzü
 */
interface FinnhubRemoteDataSource {
    suspend fun fetchSymbols(exchange: String = "US"): NetworkResult<List<FinnhubSymbolDto>>
    suspend fun fetchCompanyProfile(symbol: String): NetworkResult<FinnhubCompanyProfileDto>
    suspend fun fetchCompanyQuote(symbol: String): NetworkResult<FinnhubQuoteDto>
    suspend fun fetchEconomicCalendar(): NetworkResult<FinnhubEconomicCalendarDto>
}

@Singleton
class FinnhubRemoteDataSourceImpl @Inject constructor(
    private val finnhubApi: FinnhubApi,
    errorHandler: ErrorHandler
) : BaseRemoteDataSource(errorHandler), FinnhubRemoteDataSource {

    override suspend fun fetchSymbols(exchange: String): NetworkResult<List<FinnhubSymbolDto>> {
        return safeApiCall { finnhubApi.getSymbols(exchange) }
    }

    override suspend fun fetchCompanyProfile(symbol: String): NetworkResult<FinnhubCompanyProfileDto> {
        return safeApiCall { finnhubApi.getCompanyProfile(symbol) }
    }

    override suspend fun fetchCompanyQuote(symbol: String): NetworkResult<FinnhubQuoteDto> {
        return safeApiCall { finnhubApi.getQuote(symbol) }
    }

    override suspend fun fetchEconomicCalendar(): NetworkResult<FinnhubEconomicCalendarDto> {
        return safeApiCall { finnhubApi.getEconomicCalendar() }
    }
}
