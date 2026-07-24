package com.nexus.porsuk.domain.model

/**
 * 5 Bulut Sağlayıcı Türü (Cloud Provider Types)
 */
enum class CloudProviderType(val displayName: String, val iconEmoji: String) {
    FIREBASE("Firebase Cloud Storage", "🔥"),
    SUPABASE("Supabase Sync", "⚡"),
    AWS("Amazon Web Services (AWS)", "☁️"),
    AZURE("Microsoft Azure Cloud", "🔷"),
    SELF_HOSTED("Self-Hosted Server", "🖥️");
}

/**
 * Senkronizasyon Durumu (Sync Status States)
 */
enum class SyncStatusState(val displayName: String, val colorHex: Long) {
    SYNCED("Senkronize Edildi 🟢", 0xFF00C853),
    SYNCING("Senkronize Ediliyor 🔄", 0xFF00B0FF),
    PENDING("Beklemede 🟡", 0xFFFFB300),
    OFFLINE("Çevrimdışı (Offline) ⚪", 0xFF757575),
    ERROR("Hata Oluştu 🔴", 0xFFD50000),
    CONFLICT("Çakışma Var 🟠", 0xFFFF6D00);
}

/**
 * 13 Senkronize Edilebilir Modül (Sync Module Types)
 */
enum class SyncModuleType(val displayName: String, val iconEmoji: String) {
    PORTFOLIO("Portföy Verileri", "💼"),
    WATCHLIST("Takip Listeleri", "⭐"),
    ALERTS("Fiyat Alarmları", "🔔"),
    AI_CHAT("Orakul AI Sohbetleri", "💬"),
    PROMPT_LIBRARY("Prompt Kütüphanesi", "📝"),
    SETTINGS("Uygulama Ayarları", "⚙️"),
    FAVORITES("Favori Varlıklar", "❤️"),
    NOTES("Yatırım Notları", "📌"),
    DIVIDEND_WATCHLIST("Temettü Takip Listesi", "💰"),
    STRATEGY("Stratejiler", "📈"),
    BACKTEST_RESULTS("Backtest Sonuçları", "📊"),
    SCANNER_PRESETS("Tarama Filtreleri", "🔍"),
    CALCULATION_HISTORY("Hesaplama Geçmişi", "🧮");
}
