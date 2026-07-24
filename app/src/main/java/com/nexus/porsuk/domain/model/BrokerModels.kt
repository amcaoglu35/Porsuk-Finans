package com.nexus.porsuk.domain.model

/**
 * 7 Aracı Kurum Sağlayıcı Türü (Broker Provider Types)
 */
enum class BrokerProviderType(val displayName: String, val countryEmoji: String) {
    MIDAS("Midas Menkul Değerler", "🇹🇷"),
    INTERACTIVE_BROKERS("Interactive Brokers (IBKR)", "🇺🇸"),
    ALPACA("Alpaca Securities", "🇺🇸"),
    BINANCE("Binance Global & TR", "🪙"),
    KRAKEN("Kraken Exchange", "🪙"),
    COINBASE("Coinbase Pro", "🪙"),
    ROBINHOOD("Robinhood Financial", "🇺🇸");
}

/**
 * Aracı Kurum Bağlantı Durumu (Broker Connection Status)
 */
enum class BrokerConnectionStatus(val displayName: String, val colorHex: Long) {
    CONNECTED("Bağlantı Aktif 🟢", 0xFF00C853),
    SYNCING("Senkronize Ediliyor 🔄", 0xFF00B0FF),
    DISCONNECTED("Bağlantı Kesildi 🔴", 0xFFD50000),
    TOKEN_EXPIRED("Token Süresi Doldu 🟡", 0xFFFFB300),
    AUTH_REQUIRED("Yeniden Yetkilendirme Gerekli", 0xFFAB47BC);
}

/**
 * Emir Türleri (Broker Order Types)
 */
enum class BrokerOrderType(val displayName: String) {
    MARKET_ORDER("Piyasa Emri (Market)"),
    LIMIT_ORDER("Limit Emir"),
    STOP_ORDER("Stop Emir"),
    STOP_LIMIT_ORDER("Stop Limit Emir");
}

/**
 * Aracı Kurum Hesap Modeli (BrokerAccount)
 */
data class BrokerAccount(
    val accountId: String,
    val provider: BrokerProviderType,
    val accountName: String,
    val status: BrokerConnectionStatus,
    val cashBalanceUsd: Double = 14500.0,
    val portfolioValueUsd: Double = 68400.0,
    val buyingPowerUsd: Double = 29000.0,
    val lastSyncTimestamp: Long = System.currentTimeMillis()
)

/**
 * Aracı Kurum Varlık Pozisyonu (BrokerHoldingItem)
 */
data class BrokerHoldingItem(
    val symbol: String,
    val quantity: Double,
    val averageCostUsd: Double,
    val currentPriceUsd: Double,
    val unrealizedPlUsd: Double
)

/**
 * Geleceğe Hazır Smart Order Routing Stub Modeli
 */
data class SmartOrderRoutingStub(
    val symbol: String = "NVDA",
    val recommendedBroker: BrokerProviderType = BrokerProviderType.ALPACA,
    val estimatedFeeUsd: Double = 0.0,
    val routingReasonText: String = "Alpaca platformunda komisyonsuz işlem ve en iyi fiyat eşleşmesi (NBBO)."
)
