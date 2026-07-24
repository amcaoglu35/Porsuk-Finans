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

    /**
     * Borsanın açık/kapalı durumunu sorgular.
     * @param exchange "US", "IS"
     */
    @GET("stock/market-status")
    suspend fun getMarketStatus(
        @Query("exchange") exchange: String
    ): FinnhubMarketStatusDto
}
