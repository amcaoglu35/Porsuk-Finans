package com.nexus.porsuk.core.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Porsuk Finnhub Engine — 429 Rate Limit ve Üstel Geri Çekilme Interceptor'ı
 *
 * Sunucu 429 (Too Many Requests / Kota Aşımı) yanıtı döndüğünde isteği yakalar ve
 * otomatik olarak bekleme süresi (Backoff Delay) uygulayarak tekrar dener.
 */
@Singleton
class RateLimitInterceptor @Inject constructor() : Interceptor {

    companion object {
        private const val MAX_RETRIES = 3
        private const val INITIAL_BACKOFF_MS = 1000L
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        var response = chain.proceed(request)
        var tryCount = 0
        var backoffMs = INITIAL_BACKOFF_MS

        while (!response.isSuccessful && response.code == 429 && tryCount < MAX_RETRIES) {
            tryCount++
            Log.w("RateLimitInterceptor", "429 Rate Limit aşıldı. Deneme $tryCount/$MAX_RETRIES. $backoffMs ms bekleniyor...")

            // Retry-After header'ını kontrol et
            val retryAfterHeader = response.header("Retry-After")
            val delayDuration = retryAfterHeader?.toLongOrNull()?.times(1000L) ?: backoffMs

            try {
                Thread.sleep(delayDuration)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
                break
            }

            backoffMs *= 2 // Exponential backoff
            response.close()
            response = chain.proceed(request)
        }

        return response
    }
}
