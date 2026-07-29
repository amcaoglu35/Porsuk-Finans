package com.nexus.porsuk.data.remote.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface FmpApi {

    @GET("v3/profile/{symbol}")
    suspend fun getCompanyProfile(@Path("symbol") symbol: String): List<FmpProfileDto>

    @GET("v3/quote/{symbol}")
    suspend fun getQuote(@Path("symbol") symbol: String): List<FmpQuoteDto>

    @GET("v3/quote/{symbols}")
    suspend fun getQuoteBatch(@Path("symbols") symbols: String): List<FmpQuoteDto>

    @GET("v3/historical-price-full/{symbol}")
    suspend fun getHistoricalPrice(
        @Path("symbol") symbol: String,
        @Query("timeseries") timeseries: Int = 30
    ): FmpHistoricalResponseDto
}

data class FmpQuoteDto(
    val symbol: String,
    val name: String?,
    val price: Double?,
    val changesPercentage: Double?,
    val change: Double?,
    val dayLow: Double?,
    val dayHigh: Double?,
    val yearHigh: Double?,
    val yearLow: Double?,
    val marketCap: Long?,
    val priceAvg50: Double?,
    val priceAvg200: Double?,
    val volume: Long?,
    val avgVolume: Long?,
    val open: Double?,
    val previousClose: Double?,
    val eps: Double?,
    val pe: Double?,
    val timestamp: Long?
)

data class FmpHistoricalResponseDto(
    val symbol: String,
    val historical: List<FmpHistoricalEntryDto>?
)

data class FmpHistoricalEntryDto(
    val date: String,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val adjClose: Double,
    val volume: Long,
    val unadjustedVolume: Long,
    val change: Double,
    val changePercent: Double,
    val vwap: Double,
    val label: String,
    val changeOverTime: Double
)

data class FmpProfileDto(
    val symbol: String,
    val price: Double,
    val beta: Double,
    val volAvg: Long,
    val mktCap: Long,
    val lastDiv: Double,
    val range: String,
    val changes: Double,
    val companyName: String,
    val currency: String,
    val isin: String,
    val cusip: String,
    val exchange: String,
    val exchangeShortName: String,
    val industry: String,
    val website: String,
    val description: String,
    val ceo: String,
    val sector: String,
    val country: String,
    val fullTimeEmployees: String,
    val phone: String,
    val address: String,
    val city: String,
    val state: String,
    val zip: String,
    val dcfDiff: Double,
    val dcf: Double,
    val image: String,
    val ipoDate: String,
    val defaultImage: Boolean,
    val isEtf: Boolean,
    val isActivelyTrading: Boolean,
    val isAdr: Boolean,
    val isFund: Boolean
)
