package com.nexus.porsuk.data.remote

import java.util.concurrent.ConcurrentHashMap

/**
 * In-memory AI Cache Entry with TTL support.
 */
data class AiCacheEntry(
    val response: String,
    val timestamp: Long = System.currentTimeMillis(),
    val ttlMs: Long = DEFAULT_TTL_MS,
    val isPortfolioRelated: Boolean = false
) {
    fun isExpired(now: Long = System.currentTimeMillis()): Boolean {
        return (now - timestamp) > ttlMs
    }

    companion object {
        const val DEFAULT_TTL_MS = 5 * 60 * 1000L // 5 minutes
    }
}

/**
 * Thread-safe In-Memory AI Cache Manager for GeminiService.
 * Caches AI responses based on prompt, stock symbol, portfolio state hash, and news content hash.
 */
object AiCacheManager {

    private val cache = ConcurrentHashMap<String, AiCacheEntry>()

    /**
     * Retrieve cached response if present and not expired.
     */
    fun get(key: String): String? {
        val entry = cache[key] ?: return null
        if (entry.isExpired()) {
            cache.remove(key)
            return null
        }
        return entry.response
    }

    /**
     * Store response in memory cache if it's a valid response (not error message).
     */
    fun put(
        key: String,
        response: String,
        ttlMs: Long = AiCacheEntry.DEFAULT_TTL_MS,
        isPortfolioRelated: Boolean = false
    ) {
        if (response.isNotBlank() &&
            !response.startsWith("Hata") &&
            !response.startsWith("İstek Sınırı") &&
            !response.startsWith("Yapay Zeka Servis Hatası")
        ) {
            cache[key] = AiCacheEntry(
                response = response,
                timestamp = System.currentTimeMillis(),
                ttlMs = ttlMs,
                isPortfolioRelated = isPortfolioRelated
            )
        }
    }

    /**
     * Invalidate all portfolio-related cache entries when portfolio changes.
     */
    fun invalidatePortfolioCache() {
        cache.entries.removeIf { it.value.isPortfolioRelated }
    }

    /**
     * Clear all cached AI entries.
     */
    fun clearAll() {
        cache.clear()
    }

    /**
     * Generate structured, unique cache key combining prompt, stock, portfolio hash, and news hash.
     */
    fun generateKey(
        taskType: String,
        prompt: String = "",
        symbol: String = "",
        portfolioHash: Int = 0,
        newsHash: Int = 0
    ): String {
        val pHash = if (prompt.isNotBlank()) prompt.hashCode() else 0
        return "$taskType|sym:$symbol|pHash:$portfolioHash|nHash:$newsHash|prompt:$pHash"
    }
}
