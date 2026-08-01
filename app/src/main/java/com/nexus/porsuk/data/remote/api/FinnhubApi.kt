package com.nexus.porsuk.data.remote.api

import com.nexus.porsuk.data.remote.dto.FinnhubCompanyProfileDto
import com.nexus.porsuk.data.remote.dto.FinnhubMarketStatusDto
import com.nexus.porsuk.data.remote.dto.FinnhubSymbolDto
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Porsuk Finnhub Engine — Retrofit REST API Arayüzü
 */
interface FinnhubApi {

    /**
     * Borsa sembol kataloğunu çeker.
     * @param exchange "US" (NASDAQ, NYSE), "IS" (BIST) vb.
     */
    @GET("stock/symbol")
    suspend fun getSymbols(
        @Query("exchange") exchange: String,
        @Query("mic") mic: String? = null
    ): List<FinnhubSymbolDto>

    /**
     * Belirtilen şirketin profili ve künye bilgilerini getirir.
     * @param symbol Hisselerin ticker kodu (Örn: AAPL, THYAO.IS)
     */
    @GET("stock/profile2")
    suspend fun getCompanyProfile(
        @Query("symbol") symbol: String
    ): FinnhubCompanyProfileDto

    @GET("quote")
    suspend fun getQuote(
        @Query("symbol") symbol: String
    ): com.nexus.porsuk.data.remote.dto.FinnhubQuoteDto

    /**
     * Borsanın açık/kapalı durumunu sorgular.
     * @param exchange "US", "IS"
     */
    @GET("stock/market-status")
    suspend fun getMarketStatus(
        @Query("exchange") exchange: String,
        @Query("token") apiKey: String? = null
    ): FinnhubMarketStatusDto

    /**
     * Ekonomik takvim verilerini çeker.
     */
    @GET("calendar/economic")
    suspend fun getEconomicCalendar(
        @Query("token") apiKey: String? = null
    ): FinnhubEconomicCalendarDto
}

data class FinnhubEconomicCalendarDto(
    val economicCalendar: List<FinnhubEconomicEventDto>
)

data class FinnhubEconomicEventDto(
    val actual: Double?,
    val country: String,
    val estimate: Double?,
    val event: String,
    val impact: String,
    val prev: Double?,
    val time: String,
    val unit: String
)
