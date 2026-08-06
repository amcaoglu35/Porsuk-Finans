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
    override fun getFinnhubKey(): String = requireConfig("FINNHUB_API_KEY")
    override fun getFmpKey(): String = requireConfig("FMP_API_KEY")
    override fun getNewsApiKey(): String = requireConfig("NEWS_API_KEY")
    override fun getFredKey(): String = requireConfig("FRED_API_KEY")
    override fun getExchangeRateKey(): String = requireConfig("EXCHANGE_RATE_API_KEY")
    override fun getYahooRapidApiKey(): String = requireConfig("YAHOO_RAPIDAPI_KEY")

    private fun requireConfig(key: String): String {
        return try {
            val field = BuildConfig::class.java.getField(key)
            val value = field.get(null) as? String
            value?.takeIf { it.isNotBlank() }
                ?: throw IllegalStateException("Missing $key — check local.properties")
        } catch (e: NoSuchFieldException) {
            throw IllegalStateException("BuildConfig field $key not found — check build.gradle.kts", e)
        }
    }
}
