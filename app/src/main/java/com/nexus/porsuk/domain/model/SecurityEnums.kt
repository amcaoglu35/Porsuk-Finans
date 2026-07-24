package com.nexus.porsuk.domain.model

/**
 * Güvenlik Seviyesi (Security Levels)
 */
enum class SecurityLevel(val displayName: String, val colorHex: Long) {
    OPTIMAL("Mükemmel Güvenlik 🟢", 0xFF00C853),
    HIGH("Yüksek Güvenlik 🔵", 0xFF00B0FF),
    MEDIUM("Orta Seviye Güvenlik 🟡", 0xFFFFB300),
    CRITICAL("Kritik Tehdit 🔴", 0xFFD50000);
}

/**
 * Güvenlik Denetim Kategorisi (Audit Categories)
 */
enum class AuditCategory(val displayName: String, val iconEmoji: String) {
    AUTH("Kimlik Doğrulama", "🔑"),
    SECURITY("Sistem Güvenliği", "🛡️"),
    PERMISSION("Erişim İzinleri", "🔒"),
    SYNC("Bulut Senkronizasyon", "☁️"),
    BROKER("Borsa/Broker Baglantısı", "🏛️"),
    AI_ACCESS("AI Erişim Denetimi", "🤖");
}

/**
 * Uygulama Bütünlüğü Kontrol Türü (Integrity Check Types)
 */
enum class IntegrityCheckType(val displayName: String) {
    ROOT_DETECTION("Rooted Device Detection"),
    EMULATOR_DETECTION("Emulator Instance Detection"),
    DEBUG_DETECTION("Debugger Attached Check"),
    TAMPER_DETECTION("App Signature & Tamper Check");
}
