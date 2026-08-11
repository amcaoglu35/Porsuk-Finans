package com.nexus.porsuk.core.common

import kotlinx.coroutines.delay

/**
 * Porsuk Data Center — Otomatik Tekrar Deneme Mekanizması (Retry Policy)
 *
 * Geçici ağ hatalarında ve sunucu kesintilerinde işlemleri Üstel Geri Çekilme (Exponential Backoff)
 * stratejisi ile otomatik olarak tekrar dener.
 *
 * @param times Toplam deneme sayısı (varsayılan: 3).
 * @param initialDelayMs İlk deneme gecikmesi (milisaniye).
 * @param maxDelayMs Maksimum bekleme süresi üst sınırı.
 * @param factor Gecikme katlama katsayısı.
 * @param block Çalıştırılacak askıya alınabilir (suspend) kod bloğu.
 */
suspend fun <T> runWithRetry(
    times: Int = 3,
    initialDelayMs: Long = 1000L,
    maxDelayMs: Long = 10000L,
    factor: Double = 2.0,
    shouldRetry: (Throwable) -> Boolean = { true },
    block: suspend () -> T
): T {
    var currentDelay = initialDelayMs
    repeat(times - 1) { attempt ->
        try {
            return block()
        } catch (e: Throwable) {
            if (!shouldRetry(e)) throw e
        }
        delay(currentDelay)
        currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelayMs)
    }
    return block() // Son deneme
}
