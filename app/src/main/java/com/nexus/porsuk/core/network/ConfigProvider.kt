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
}

@Singleton
class ConfigProviderImpl @Inject constructor() : ConfigProvider {
    override fun getFinnhubKey(): String = getOrFallback("FINNHUB_API_KEY", "d98i3dhr01qijq5l27egd98i3dhr01qijq5l27f0")
    override fun getFmpKey(): String = getOrFallback("FMP_API_KEY", "x9gbGcq3Ye9tXvZZYEFEXFFHM0Lk68fN")
    override fun getNewsApiKey(): String = getOrFallback("NEWS_API_KEY", "fd01b15282104ca2b90c9bb34f6d56f0")
    override fun getFredKey(): String = getOrFallback("FRED_API_KEY", "abcdefghijklmnopqrstuvwxyz123456")
    override fun getExchangeRateKey(): String = getOrFallback("EXCHANGE_RATE_API_KEY", "363f31593a22c6d6754ee7ef23455301")

    private fun getOrFallback(key: String, fallback: String): String {
        return try {
            val field = BuildConfig::class.java.getField(key)
            val value = field.get(null) as? String
            if (!value.isNullOrBlank()) value else fallback
        } catch (_: Exception) {
            fallback
        }
    }
}
