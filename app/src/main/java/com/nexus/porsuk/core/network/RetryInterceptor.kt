package com.nexus.porsuk.core.network

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetryInterceptor @Inject constructor() : Interceptor {

    companion object {
        private const val MAX_RETRIES = 3
        private const val INITIAL_BACKOFF_MS = 1000L
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var response: Response? = null
        var exception: IOException? = null
        var tryCount = 0

        while (tryCount < MAX_RETRIES) {
            try {
                response?.close()
                response = chain.proceed(request)
                
                // If successful or 4xx error (client error, no point retrying), break
                if (response.isSuccessful || (response.code in 400..499 && response.code != 429)) {
                    return response
                }
            } catch (e: IOException) {
                exception = e
            }

            tryCount++
            if (tryCount < MAX_RETRIES) {
                try {
                    Thread.sleep(INITIAL_BACKOFF_MS * tryCount)
                } catch (_: InterruptedException) {
                    Thread.currentThread().interrupt()
                    break
                }
            }
        }

        if (response != null) {
            return response
        }
        throw exception ?: IOException("Network request failed after $MAX_RETRIES retries")
    }
}
