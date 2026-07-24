package com.nexus.porsuk.data.local.cache

import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Porsuk Data Center — Önbellek Yöneticisi (Multi-Level Cache Manager)
 *
 * API isteklerini bellekte (In-Memory ConcurrentHashMap) tutarak gereksiz ağ trafiğini ve
 * sunucu kotası (Rate Limit) aşımlarını engeller. Verilerin tazeliğini TTL (Time-To-Live)
 * süresi ile kontrol eder.
 */
@Singleton
class CacheManager @Inject constructor() {

    private data class CacheEntry<T>(
        val data: T,
        val timestamp: Long,
        val ttlMs: Long
    ) {
        fun isExpired(): Boolean = System.currentTimeMillis() - timestamp > ttlMs
    }

    private val inMemoryCache = ConcurrentHashMap<String, CacheEntry<Any>>()

    /**
     * Önbelleğe veri yazar.
     *
     * @param key Önbellek anahtarı (Örn: "company_THYAO.IS")
     * @param value Saklanacak nesne
     * @param ttlMs Yaşam süresi (milisaniye). Varsayılan: 5 dakika (300.000 ms)
     */
    fun <T : Any> put(key: String, value: T, ttlMs: Long = 5 * 60 * 1000L) {
        inMemoryCache[key] = CacheEntry(data = value, timestamp = System.currentTimeMillis(), ttlMs = ttlMs)
    }

    /**
     * Önbellekten veri okur. Eğer veri yoksa veya TTL süresi dolmuşsa null döner.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> get(key: String): T? {
        val entry = inMemoryCache[key] ?: return null
        if (entry.isExpired()) {
            inMemoryCache.remove(key)
            return null
        }
        return entry.data as? T
    }

    /**
     * Belirtilen anahtar için geçerli önbellek verisi var mı kontrol eder.
     */
    fun isCacheValid(key: String): Boolean {
        val entry = inMemoryCache[key] ?: return false
        return !entry.isExpired()
    }

    /**
     * Belirli bir önbellek anahtarını siler.
     */
    fun evict(key: String) {
        inMemoryCache.remove(key)
    }

    /**
     * Tüm önbelleği temizler.
     */
    fun clearAll() {
        inMemoryCache.clear()
    }
}
