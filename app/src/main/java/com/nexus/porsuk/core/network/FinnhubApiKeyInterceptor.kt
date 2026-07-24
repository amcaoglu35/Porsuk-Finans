package com.nexus.porsuk.core.network

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Porsuk Finnhub Engine — Finnhub API Key Interceptor
 *
 * Tüm Finnhub REST API isteklerine otomatik olarak `token` sorgu parametresi veya header ekler.
 */
@Singleton
class FinnhubApiKeyInterceptor @Inject constructor() : Interceptor {

    private val apiKey: String = "c8b8q2aad3ic7h0h5n9g" // Proje Varsayılan / Dynamic API Key

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalUrl = originalRequest.url

        val urlWithKey = originalUrl.newBuilder()
            .addQueryParameter("token", apiKey)
            .build()

        val requestWithKey = originalRequest.newBuilder()
            .url(urlWithKey)
            .addHeader("X-Finnhub-Token", apiKey)
            .build()

        return chain.proceed(requestWithKey)
    }
}
