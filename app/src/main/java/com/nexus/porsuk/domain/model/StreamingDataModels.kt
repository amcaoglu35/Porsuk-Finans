package com.nexus.porsuk.domain.model

/**
 * Streaming Sağlayıcı Türü (StreamingProviderType)
 */
enum class StreamingProviderType(val displayName: String, val iconEmoji: String) {
    FINNHUB("Finnhub WebSocket", "⚡"),
    POLYGON("Polygon.io Stream", "📊"),
    ALPACA("Alpaca Market Stream", "🦙"),
    BINANCE("Binance Crypto Stream", "🟡"),
    COINBASE("Coinbase Pro Stream", "🔵");
}

/**
 * WebSocket Bağlantı Durumu (ConnectionState)
 */
enum class ConnectionState(val displayName: String, val colorHex: Long) {
    DISCONNECTED("Bağlantı Kapalı", 0xFF9E9E9E),
    CONNECTING("Bağlanıyor...", 0xFFFFB300),
    CONNECTED("Canlı Akış (Connected 🟢)", 0xFF00C853),
    RECONNECTING("Yeniden Bağlanılıyor 🔄", 0xFFFF6D00),
    ERROR("Bağlantı Hatası 🔴", 0xFFD50000);
}

/**
 * Piyasa Veri Kanalı (StreamingChannel)
 */
enum class StreamingChannel(val displayName: String) {
    STOCKS("Hisse Senetleri (Stocks)"),
    ETFS("Borsa Yatırım Fonları (ETFs)"),
    FOREX("Döviz & Kurlar (Forex)"),
    CRYPTO("Kripto Varlıklar (Crypto)"),
    COMMODITIES("Emtia & Madenler"),
    INDICES("Piyasa Endeksleri");
}

/**
 * Piyasa Etkinlik Türü (MarketEventType)
 */
enum class MarketEventType(val eventName: String) {
    TRADE_TICK("Son İşlem Tıkı (Trade)"),
    QUOTE_TICK("Teklif Güncellemesi (Quote)"),
    BID_UPDATE("Alış Fiyatı (Bid)"),
    ASK_UPDATE("Satış Fiyatı (Ask)"),
    MARKET_STATUS("Piyasa Durumu");
}

/**
 * Gerçek Zamanlı Fiyat Tıkı (MarketTickEvent)
 */
data class MarketTickEvent(
    val symbol: String,
    val lastPrice: Double,
    val changeAmount: Double,
    val changePct: Double,
    val volume: Long,
    val bidPrice: Double,
    val askPrice: Double,
    val provider: StreamingProviderType = StreamingProviderType.FINNHUB,
    val channel: StreamingChannel = StreamingChannel.STOCKS,
    val eventType: MarketEventType = MarketEventType.TRADE_TICK,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Canlı Mum Çubuğu (LiveCandle)
 */
data class LiveCandle(
    val symbol: String,
    val timeframe: String = "1m",
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Long,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Akış Sağlık ve Gecikme Metrikleri (StreamHealthMetrics)
 */
data class StreamHealthMetrics(
    val connectionState: ConnectionState = ConnectionState.CONNECTED,
    val latencyMs: Long = 14L,
    val tickRatePerSec: Double = 42.8,
    val packetLossPct: Double = 0.0,
    val reconnectCount: Int = 0,
    val activeSubscribedSymbolsCount: Int = 5,
    val isHeartbeatActive: Boolean = true
)

/**
 * Geleceğe Hazır Derinlik & Opsiyon Akış Stub Modeli (StreamingFutureStubs)
 */
data class StreamingFutureStubs(
    val isLevel2MarketDepthSupported: Boolean = true,
    val isOrderBookStreamingSupported: Boolean = true,
    val isOptionsStreamingSupported: Boolean = true,
    val isMultiProviderFailoverActive: Boolean = true,
    val isUltraLowLatencyModeEnabled: Boolean = false
)
