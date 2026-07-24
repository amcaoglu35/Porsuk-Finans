package com.nexus.porsuk.domain.model

/**
 * Porsuk Smart Alert Engine — 10 Alarm Kategorisi
 */
enum class AlertCategory(val displayName: String, val iconEmoji: String = "🔔") {
    PRICE("Fiyat Alarmı", "🎯"),
    PERCENT_CHANGE("Yüzdelik Değişim Alarmı", "📊"),
    VOLUME("Hacim Alarmı", "📈"),
    DIVIDEND("Temettü Alarmı", "💰"),
    EARNINGS("Bilanço / EPS Alarmı", "📑"),
    NEWS("Haber ve KAP Alarmı", "📰"),
    ECONOMIC_CALENDAR("Ekonomik Takvim Alarmı", "📅"),
    PORTFOLIO("Portföy Alarmı", "💼"),
    WATCHLIST("Watchlist Alarmı", "⭐"),
    AI_ORAKUL_STUB("Orakul AI Smart Alarm (Gelecek)", "🔮");

    companion object {
        fun fromString(name: String?): AlertCategory {
            return entries.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: PRICE
        }
    }
}

/**
 * Porsuk Smart Alert Engine — Alarm Koşul Tipleri
 */
enum class AlertCondition(val symbolText: String, val displayName: String) {
    ABOVE(">", "Üstüne Çıkınca"),
    BELOW("<", "Altına İnince"),
    EQUAL("=", "Eşit Olunca"),
    PERCENT_INCREASE("+%", "Yüzdelik Artış"),
    PERCENT_DECREASE("-%", "Yüzdelik Düşüş"),
    VOLUME_SPIKE("📈", "Ani Hacim Artışı"),
    UNUSUAL_ACTIVITY("⚡", "Olağan Dışı Hareket")
}
