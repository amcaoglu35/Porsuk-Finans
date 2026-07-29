package com.nexus.porsuk.data.remote.api

import retrofit2.http.GET
import retrofit2.http.Query

interface ExchangeRateApi {

    @GET("v1/latest")
    suspend fun getLatestRates(
        @Query("base") base: String = "USD",
        @Query("symbols") symbols: String? = null
    ): ExchangeRateResponseDto

    @GET("v1/convert")
    suspend fun convertCurrency(
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("amount") amount: Double
    ): CurrencyConversionDto
}

data class ExchangeRateResponseDto(
    val success: Boolean?,
    val base: String?,
    val date: String?,
    val rates: Map<String, Double>?
)

data class CurrencyConversionDto(
    val success: Boolean?,
    val result: Double?,
    val info: ConversionInfoDto?
)

data class ConversionInfoDto(
    val rate: Double?
)
