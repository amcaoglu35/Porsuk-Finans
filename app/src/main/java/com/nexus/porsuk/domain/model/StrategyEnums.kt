package com.nexus.porsuk.domain.model

/**
 * 12 Strateji Türü (Strategy Types)
 */
enum class StrategyType(val displayName: String, val iconEmoji: String) {
    TREND_FOLLOWING("Trend Takibi (Trend Following)", "📈"),
    MOMENTUM("Momentum Stratejisi", "⚡"),
    MEAN_REVERSION("Ortalamaya Dönüş (Mean Reversion)", "🔄"),
    BREAKOUT("Kırılım Stratejisi (Breakout)", "💥"),
    SWING_TRADING("Swing Trading", "🌊"),
    POSITION_TRADING("Pozisyon Yatırımı (Position)", "⏳"),
    DIVIDEND_STRATEGY("Temettü Büyümesi", "💰"),
    VALUE_INVESTING("Değer Yatırımı (Value)", "💎"),
    GROWTH_INVESTING("Büyüme Yatırımı (Growth)", "🚀"),
    ETF_ROTATION("ETF Rotasyonu", "🔄"),
    DCA("Dolar Maliyet Ortalaması (DCA)", "📆"),
    CUSTOM("Özel Strateji (Custom)", "🛠️");
}

/**
 * Mantıksal Koşul Operatörleri (Condition Operator)
 */
enum class ConditionOperator(val symbolText: String) {
    GREATER_THAN("> (Büyüktür)"),
    LESS_THAN("< (Küçüktür)"),
    CROSSES_ABOVE("Yukarı Kesti (Crosses Above)"),
    CROSSES_BELOW("Aşağı Kesti (Crosses Below)"),
    EQUALS("Eşittir (=)");
}
