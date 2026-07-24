package com.nexus.porsuk.domain.model

/**
 * Porsuk Economic Calendar & Events Engine — 4 Etkinlik Kategorisi
 */
enum class CalendarEventCategory(val displayName: String) {
    ALL("Tüm Etkinlikler"),
    MACRO("Merkez Bankaları & Makro"),
    ECONOMIC_DATA("Ekonomik Veriler (TÜFE/GDP)"),
    EARNINGS("Bilanço Açıklamaları"),
    DIVIDEND("Temettü & Hak Kullanımı"),
    STOCK_IPO("Halka Arzlar & Borsa");

    companion object {
        fun fromString(name: String?): CalendarEventCategory {
            return entries.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: ALL
        }
    }
}

/**
 * Etkinlik Piyasa Etki Seviyesi (Impact Level)
 */
enum class CalendarImpactLevel(val displayName: String, val colorHex: Long) {
    HIGH("Yüksek Etki 🔴", 0xFFD50000),
    MEDIUM("Orta Etki 🟡", 0xFFFFB300),
    LOW("Düşük Etki ⚪", 0xFF757575)
}

/**
 * Takvim Ekranı Görünüm Modları
 */
enum class CalendarViewMode(val displayName: String) {
    DAILY("Günlük"),
    WEEKLY("Haftalık"),
    MONTHLY("Aylık"),
    LIST("Liste");
}
