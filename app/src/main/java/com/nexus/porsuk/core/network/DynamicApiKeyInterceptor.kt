package com.nexus.porsuk.core.network

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DynamicApiKeyInterceptor @Inject constructor(
    private val apiKeyProvider: ApiKeyProvider
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val host = request.url.host
        
        val apiKey = when {
            host.contains("finnhub.io") -> apiKeyProvider.getFinnhubKey()
            host.contains("financialmodelingprep.com") -> apiKeyProvider.getFmpKey()
            host.contains("newsapi.org") -> apiKeyProvider.getNewsApiKey()
            host.contains("api.stlouisfed.org") -> apiKeyProvider.getFredKey()
            host.contains("api.exchangerate.host") -> apiKeyProvider.getExchangeRateKey()
            else -> null
        }

        return if (apiKey != null) {
            val newUrl = when {
                host.contains("finnhub.io") -> {
                    request.url.newBuilder().addQueryParameter("token", apiKey).build()
                }
                host.contains("financialmodelingprep.com") -> {
                    request.url.newBuilder().addQueryParameter("apikey", apiKey).build()
                }
                host.contains("newsapi.org") -> {
                    request.url.newBuilder().addQueryParameter("apiKey", apiKey).build()
                }
                host.contains("api.stlouisfed.org") -> {
                    request.url.newBuilder().addQueryParameter("api_key", apiKey).build()
                }
                host.contains("api.exchangerate.host") -> {
                    request.url.newBuilder().addQueryParameter("access_key", apiKey).build()
                }
                else -> request.url
            }
            chain.proceed(request.newBuilder().url(newUrl).build())
        } else {
            chain.proceed(request)
        }
    }
}

interface ApiKeyProvider {
    fun getFinnhubKey(): String
    fun getFmpKey(): String
    fun getNewsApiKey(): String
    fun getFredKey(): String
    fun getExchangeRateKey(): String
}
