package com.nexus.porsuk.domain.model

/**
 * Porsuk Watchlist Pro — 10 Akıllı Klasör Kategorisi (Smart Folders)
 */
enum class SmartCategory(val title: String) {
    DIVIDEND("Temettü Hisseleri"),
    GROWTH("Büyüme Hisseleri"),
    TECHNOLOGY("Teknoloji Devleri"),
    BANKING("Bankalar & Finans"),
    DEFENSE("Savunma Sanayii"),
    ENERGY("Yenilenebilir Enerji"),
    FUNDS("TEFAS & ETF Fonları"),
    CRYPTO("Kripto Varlıklar"),
    LONG_TERM("Uzun Vadeli Portföy"),
    SHORT_TERM("Kısa Vadeli Fırsatlar");

    companion object {
        fun fromString(name: String?): SmartCategory? {
            return entries.firstOrNull { it.name.equals(name, ignoreCase = true) }
        }
    }
}

/**
 * Porsuk Watchlist Pro — Geleceğe Hazır 7 Bildirim / Alarm Tipi (Alert Infrastructure)
 */
enum class AlertTypeStub(val displayName: String) {
    PRICE("Fiyat Hedefi Alarmı"),
    VOLUME("Sıradışı Hacim Alarmı"),
    NEWS("KAP ve Önemli Haber Alarmı"),
    DIVIDEND("Temettü Hak Kullanım Alarmı"),
    EARNINGS("Bilanço ve Kar Açıklama Alarmı"),
    AI_SCORE("Orakul AI Skor Değişim Alarmı"),
    RISK("Volatilite ve Risk Alarmı")
}
