package com.nexus.porsuk.domain.model

/**
 * Porsuk Orakul Core — 12 Modüler Teknik Gösterge Tipi (Technical Indicators)
 */
enum class TechnicalIndicatorType(val displayName: String, val category: String) {
    RSI("RSI (Göreceli Güç Endeksi)", "Momentum"),
    MACD("MACD (Hareketli Ortalamalar İvmesi)", "Trend & İvme"),
    EMA("EMA (Üstel Hareketli Ortalama)", "Trend"),
    SMA("SMA (Basit Hareketli Ortalama)", "Trend"),
    BOLLINGER("Bollinger Bands (Bantlar)", "Volatilite"),
    ATR("ATR (Ortalama Gerçek Aralık)", "Volatilite"),
    ADX("ADX (Ortalama Yönsel Endeks)", "Trend Gücü"),
    OBV("OBV (Denge İşlem Hacmi)", "Hacim"),
    VWAP("VWAP (Hacim Ağırlıklı Ortalama Fiyat)", "Hacim & Fiyat"),
    ICHIMOKU("Ichimoku Kinko Hyo (Bulut)", "Trend & Destek"),
    SUPERTREND("SuperTrend (Süper Trend)", "Trend Takibi"),
    FIBONACCI("Fibonacci Geri Çekilme Düzeyleri", "Destek & Direnç");
}

/**
 * Standart Orakul Analiz Raporu Modeli (OrakulAnalysisReport)
 */
data class OrakulAnalysisReport(
    val symbol: String,
    val executiveSummary: String,
    val strengths: List<String>,
    val weaknesses: List<String>,
    val risks: List<String>,
    val opportunities: List<String>,
    val keyWatchpoints: List<String>,
    val generatedAt: Long = System.currentTimeMillis()
)
