package com.nexus.porsuk.core.common

/**
 * Porsuk Data Center — Domain Veri Hataları Hiyerarşisi (Typed Data Errors)
 *
 * Uygulamada oluşabilecek tüm veri, ağ, doğrulama ve senkronizasyon hatalarını
 * merkezi olarak gruplayan sealed interface.
 */
sealed interface DataError {

    /**
     * Uzak Sunucu / Ağ İlişkili Hatalar
     */
    enum class Network : DataError {
        REQUEST_TIMEOUT,
        UNAUTHORIZED,
        FORBIDDEN,
        NOT_FOUND,
        SERVER_ERROR,
        NO_INTERNET,
        RATE_LIMIT_EXCEEDED,
        UNKNOWN_NETWORK_ERROR
    }

    /**
     * Yerel Veritabanı / Room İlişkili Hatalar
     */
    enum class Database : DataError {
        READ_FAILED,
        WRITE_FAILED,
        CONSTRAINT_VIOLATION,
        DISK_FULL,
        DATA_CORRUPTED
    }

    /**
     * Veri Doğrulama (Validator) Hataları
     */
    enum class Validation : DataError {
        MISSING_REQUIRED_FIELDS,
        INVALID_PRICE_OR_VALUE,
        DUPLICATE_ENTRY,
        MALFORMED_IDENTIFIER
    }

    /**
     * Önbellek (Cache Manager) Hataları
     */
    enum class Cache : DataError {
        EXPIRED,
        NOT_FOUND,
        WRITE_FAILED
    }
}
