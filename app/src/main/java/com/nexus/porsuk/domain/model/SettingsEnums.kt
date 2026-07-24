package com.nexus.porsuk.domain.model

/**
 * Uygulama Tema Modu (AppThemeMode)
 */
enum class AppThemeMode(val displayName: String, val iconEmoji: String) {
    SYSTEM("Sistem Varsayılanı", "📱"),
    LIGHT("Açık Tema (Light)", "☀️"),
    DARK("Karanlık Tema (Dark)", "🌙"),
    AMOLED("Saf Siyah (AMOLED)", "🖤");
}

/**
 * Uygulama Dili (AppLanguage)
 */
enum class AppLanguage(val displayName: String, val code: String) {
    TURKISH("Türkçe 🇹🇷", "tr"),
    ENGLISH("English 🇺🇸", "en");
}

/**
 * Varsayılan Para Birimi (DefaultCurrency)
 */
enum class DefaultCurrency(val displayName: String, val symbol: String) {
    TRY("Türk Lirası (₺)", "₺"),
    USD("Amerikan Doları ($)", "$"),
    EUR("Euro (€)", "€");
}
