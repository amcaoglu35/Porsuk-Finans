package com.nexus.porsuk.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Finnhub Borsa Sembol Veri Transfer Nesnesi
 */
data class FinnhubSymbolDto(
    @SerializedName("symbol") val symbol: String,
    @SerializedName("displaySymbol") val displaySymbol: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("type") val type: String? = null, // Common Stock, ETF, ETN vb.
    @SerializedName("currency") val currency: String? = null,
    @SerializedName("mic") val mic: String? = null, // Exchange MIC code
    @SerializedName("isin") val isin: String? = null
)

/**
 * Finnhub Şirket Detay Profili Veri Transfer Nesnesi
 */
data class FinnhubCompanyProfileDto(
    @SerializedName("country") val country: String? = null,
    @SerializedName("currency") val currency: String? = null,
    @SerializedName("exchange") val exchange: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("ticker") val ticker: String? = null,
    @SerializedName("isin") val isin: String? = null,
    @SerializedName("ipo") val ipo: String? = null, // IPO Tarihi YYYY-MM-DD
    @SerializedName("marketCapitalization") val marketCap: Double? = null,
    @SerializedName("logo") val logo: String? = null,
    @SerializedName("weburl") val weburl: String? = null,
    @SerializedName("finnhubIndustry") val finnhubIndustry: String? = null
)

/**
 * Finnhub Piyasa Durumu Veri Transfer Nesnesi
 */
data class FinnhubMarketStatusDto(
    @SerializedName("exchange") val exchange: String,
    @SerializedName("isOpen") val isOpen: Boolean,
    @SerializedName("session") val session: String? = null,
    @SerializedName("timezone") val timezone: String? = null,
    @SerializedName("holiday") val holiday: String? = null
)
