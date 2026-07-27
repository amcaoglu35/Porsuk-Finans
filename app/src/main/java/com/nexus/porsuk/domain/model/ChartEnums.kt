package com.nexus.porsuk.domain.model

/**
 * Advanced Chart Studio — Grafik Türleri
 */
enum class ChartType(val label: String, val icon: String) {
    CANDLESTICK("Mum Grafik", "🕯️"),
    LINE("Çizgi Grafik", "📈"),
    AREA("Alan Grafik", "🟦"),
    BAR("Bar Grafik", "📊"),
    HEIKIN_ASHI("Heikin Ashi", "🏮"),
    HOLLOW_CANDLES("Boş Mumlar", "🕯️")
}

/**
 * Advanced Chart Studio — Zaman Aralıkları
 */
enum class ChartTimeFrame(val code: String, val displayName: String) {
    ONE_MIN("1m", "1 dk"),
    FIVE_MIN("5m", "5 dk"),
    FIFTEEN_MIN("15m", "15 dk"),
    THIRTY_MIN("30m", "30 dk"),
    ONE_HOUR("1h", "1 sa"),
    FOUR_HOUR("4h", "4 sa"),
    DAILY("1D", "Günlük"),
    WEEKLY("1W", "Haftalık"),
    MONTHLY("1M", "Aylık")
}

/**
 * Advanced Chart Studio — Teknik İndikatörler
 */
enum class IndicatorType(val label: String, val category: String) {
    RSI("RSI", "Oscillator"),
    MACD("MACD", "Momentum"),
    BOLLINGER("Bollinger Bands", "Volatility"),
    EMA("EMA", "Trend"),
    SMA("SMA", "Trend"),
    VWAP("VWAP", "Volume"),
    ATR("ATR", "Volatility"),
    ADX("ADX", "Trend"),
    STOCH_RSI("Stoch RSI", "Oscillator"),
    ICHIMOKU("Ichimoku Cloud", "Trend"),
    OBV("OBV", "Volume"),
    SUPERTREND("SuperTrend", "Trend")
}

/**
 * Advanced Chart Studio — Çizim Araçları
 */
enum class DrawingToolType(val code: String, val displayName: String, val iconEmoji: String) {
    CROSSHAIR("CROSS", "İmleç", "🖱️"),
    TREND_LINE("TREND", "Trend Çizgisi", "📏"),
    HORIZONTAL_LINE("HORIZ", "Yatay Çizgi", "➖"),
    VERTICAL_LINE("VERT", "Dikey Çizgi", "📍"),
    CHANNEL("CHANNEL", "Kanal", "🛤️"),
    FIBONACCI("FIBO", "Fibonacci", "🌀"),
    RECTANGLE("RECT", "Bölge", "⬛"),
    TEXT("TEXT", "Not", "📝")
}

enum class OverlayMarkerType(val displayName: String, val colorHex: Long) {
    BUY("Alış", 0xFF00C853), 
    SELL("Satış", 0xFFD50000), 
    DIVIDEND("Temettü", 0xFFFFB300)
}
