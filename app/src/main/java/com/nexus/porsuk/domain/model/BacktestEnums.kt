package com.nexus.porsuk.domain.model

/**
 * 4 Backtest Simülasyon Modu (Backtest Modes)
 */
enum class BacktestMode(val displayName: String) {
    SINGLE_ASSET("Tek Varlık Simülasyonu"),
    MULTI_ASSET("Çoklu Varlık Simülasyonu"),
    PORTFOLIO("Portföy Simülasyonu"),
    BENCHMARK_COMPARISON("Benchmark Karşılaştırma");
}

/**
 * 7 İşlem Emri Türü (Trade Order Types)
 */
enum class TradeOrderType(val displayName: String, val colorHex: Long) {
    BUY("Alış", 0xFF00C853),
    SELL("Satış", 0xFFD50000),
    STOP_LOSS("Stop Loss (Zarar Kes)", 0xFFFF6D00),
    TAKE_PROFIT("Take Profit (Kâr Al)", 0xFF00B0FF),
    TRAILING_STOP("Izsüren Stop (Trailing)", 0xFFAB47BC),
    PARTIAL_SELL("Kısmi Satış", 0xFFFFB300),
    TIME_EXIT("Süre Sonu Çıkış", 0xFF757575);
}
