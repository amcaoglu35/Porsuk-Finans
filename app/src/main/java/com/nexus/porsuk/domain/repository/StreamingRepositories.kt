package com.nexus.porsuk.domain.repository

import com.nexus.porsuk.domain.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

/**
 * 1. Gerçek Zamanlı Akış Deposu Sözleşmesi (StreamingRepository)
 */
interface StreamingRepository {
    fun getMarketTickStream(): SharedFlow<MarketTickEvent>
    fun getLiveCandleStream(): SharedFlow<LiveCandle>
    fun getStreamHealth(): Flow<StreamHealthMetrics>
}

/**
 * 2. WebSocket Bağlantı Yöneticisi Deposu Sözleşmesi (WebSocketRepository)
 */
interface WebSocketRepository {
    fun getConnectionState(): Flow<ConnectionState>
    suspend fun connect(provider: StreamingProviderType)
    suspend fun disconnect()
    suspend fun sendHeartbeatPing(): Boolean
}

/**
 * 3. Sembol Abonelik Deposu Sözleşmesi (StreamSymbolSubscriptionRepository)
 */
interface StreamSymbolSubscriptionRepository {
    fun getSubscribedSymbols(): Flow<Set<String>>
    suspend fun subscribeSymbol(symbol: String): Boolean
    suspend fun unsubscribeSymbol(symbol: String): Boolean
    suspend fun batchSubscribe(symbols: List<String>): Boolean
}

/**
 * 4. Fiyat Tık Önbellek Deposu Sözleşmesi (TickRepository)
 */
interface TickRepository {
    fun getLastKnownTick(symbol: String): MarketTickEvent?
    fun getAllLastKnownTicks(): Map<String, MarketTickEvent>
    suspend fun cacheTick(tick: MarketTickEvent)
}
