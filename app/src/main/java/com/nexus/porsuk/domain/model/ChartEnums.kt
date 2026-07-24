package com.nexus.porsuk.domain.model

/**
 * 7 Grafik Türü (Chart Types)
 */
enum class ChartType(val displayName: String) {
    CANDLESTICK("Mum Grafik (Candlestick)"),
    LINE("Çizgi Grafik (Line)"),
    AREA("Alan Grafik (Area)"),
    BAR("Bar Grafik (Bar)"),
    HEIKIN_ASHI("Heikin Ashi"),
    HOLLOW_CANDLES("Hollow Candles"),
    BASELINE("Baseline Chart");
}

/**
 * 10 Zaman Dilimi (Chart TimeFrames)
 */
enum class ChartTimeFrame(val code: String, val displayName: String) {
    ONE_MIN("1m", "1 Dakika"),
    FIVE_MIN("5m", "5 Dakika"),
    FIFTEEN_MIN("15m", "15 Dakika"),
    THIRTY_MIN("30m", "30 Dakika"),
    ONE_HOUR("1h", "1 Saat"),
    FOUR_HOUR("4h", "4 Saat"),
    DAILY("1D", "Günlük"),
    WEEKLY("1W", "Haftalık"),
    MONTHLY("1M", "Aylık"),
    YEARLY("1Y", "Yıllık");
}

/**
 * Çizim Araçları Türü (Drawing Tool Types)
 */
enum class DrawingToolType(val displayName: String, val iconEmoji: String) {
    SELECT("Seç", "👆"),
    CROSSHAIR("Crosshair", "🎯"),
    TREND_LINE("Trend Çizgisi", "📈"),
    HORIZONTAL_LINE("Yatay Destek/Direnç", "➖"),
    VERTICAL_LINE("Dikey Çizgi", "│"),
    RECTANGLE("Dikdörtgen Alan", "▭"),
    TEXT("Metin Notu", "📝"),
    MEASURE("Fiyat/Zaman Ölçüm", "📏");
}
