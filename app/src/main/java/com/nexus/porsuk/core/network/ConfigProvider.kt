package com.nexus.porsuk.core.network

import com.nexus.porsuk.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

interface ConfigProvider {
    fun getFinnhubKey(): String
    fun getFmpKey(): String
    fun getNewsApiKey(): String
    fun getFredKey(): String
    fun getExchangeRateKey(): String
    fun getYahooRapidApiKey(): String
}

@Singleton
class ConfigProviderImpl @Inject constructor() : ConfigProvider {
    override fun getFinnhubKey(): String = validate(BuildConfig.FINNHUB_API_KEY, "FINNHUB_API_KEY")
    override fun getFmpKey(): String = validate(BuildConfig.FMP_API_KEY, "FMP_API_KEY")
    override fun getNewsApiKey(): String = validate(BuildConfig.NEWS_API_KEY, "NEWS_API_KEY")
    override fun getFredKey(): String = validate(BuildConfig.FRED_API_KEY, "FRED_API_KEY")
    override fun getExchangeRateKey(): String = validate(BuildConfig.EXCHANGE_RATE_API_KEY, "EXCHANGE_RATE_API_KEY")
    override fun getYahooRapidApiKey(): String = validate(BuildConfig.YAHOO_RAPIDAPI_KEY, "YAHOO_RAPIDAPI_KEY")

    private fun validate(value: String?, key: String): String {
        return value?.takeIf { it.isNotBlank() }
            ?: throw IllegalStateException("Missing $key — check local.properties and build.gradle.kts")
    }
}
