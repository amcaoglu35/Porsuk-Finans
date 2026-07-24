package com.nexus.porsuk.domain.model

/**
 * 10 Akıllı Filtre Paketi Kategorisi (Smart Filter Preset Categories)
 */
enum class SmartFilterPresetCategory(val displayName: String, val iconEmoji: String) {
    VALUE_INVESTORS("Değer Yatırımcıları (Value)", "💎"),
    DIVIDEND_INVESTORS("Temettü Yatırımcıları (Dividend)", "💰"),
    GROWTH_STOCKS("Büyüme Hisseleri (Growth)", "🚀"),
    MOMENTUM_STOCKS("Momentum Hisseleri", "⚡"),
    LOW_RISK("Düşük Riskli Limanlar", "🛡️"),
    HIGH_QUALITY("Yüksek Kalite Şirketler", "⭐"),
    STRONG_BALANCE_SHEET("Güçlü Bilanço (Z > 3.0)", "🏛️"),
    NEW_HIGH_52W("Yeni Zirve (52W High)", "🏔️"),
    DIP_REBOUND("Dipten Dönenler", "🔄"),
    TURNAROUND("Turnaround Şirketler", "🔥");
}
