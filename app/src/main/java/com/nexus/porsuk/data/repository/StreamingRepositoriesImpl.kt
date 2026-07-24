package com.nexus.porsuk.data.repository

import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StreamingRepositoryImpl @Inject constructor(
    private val tickRepository: TickRepository,
    private val subscriptionRepository: StreamSymbolSubscriptionRepository
) : StreamingRepository {

    private val scope = CoroutineScope(Dispatchers.Default)
    private val _tickSharedFlow = MutableSharedFlow<MarketTickEvent>(extraBufferCapacity = 100)
    private val _candleSharedFlow = MutableSharedFlow<LiveCandle>(extraBufferCapacity = 50)
    private val healthState = MutableStateFlow(StreamHealthMetrics())

    init {
        startSimulatedStream()
    }

    override fun getMarketTickStream(): SharedFlow<MarketTickEvent> = _tickSharedFlow

    override fun getLiveCandleStream(): SharedFlow<LiveCandle> = _candleSharedFlow

    override fun getStreamHealth(): Flow<StreamHealthMetrics> = healthState.asStateFlow()

    private fun startSimulatedStream() {
        scope.launch {
            val sampleSymbols = listOf("THYAO.IS", "GARAN.IS", "AAPL", "BTC-USD", "EUR/TRY")
            while (true) {
                delay(1200)
                val targetSymbol = sampleSymbols.random()
                val basePrice = when (targetSymbol) {
                    "THYAO.IS" -> 310.50
                    "GARAN.IS" -> 118.20
                    "AAPL" -> 224.80
                    "BTC-USD" -> 64500.00
                    else -> 35.80
                }
                val randomOffset = (-15..15).random() / 100.0
                val lastPrice = (basePrice * (1 + randomOffset / 100)).toTwoDecimals()
                val changePct = (randomOffset * 2).toTwoDecimals()

                val tick = MarketTickEvent(
                    symbol = targetSymbol,
                    lastPrice = lastPrice,
                    changeAmount = (lastPrice - basePrice).toTwoDecimals(),
                    changePct = changePct,
                    volume = (1000..50000).random().toLong(),
                    bidPrice = (lastPrice - 0.05).toTwoDecimals(),
                    askPrice = (lastPrice + 0.05).toTwoDecimals(),
                    timestamp = System.currentTimeMillis()
                )

                tickRepository.cacheTick(tick)
                _tickSharedFlow.emit(tick)

                val candle = LiveCandle(
                    symbol = targetSymbol,
                    open = basePrice,
                    high = lastPrice + 0.5,
                    low = basePrice - 0.5,
                    close = lastPrice,
                    volume = tick.volume
                )
                _candleSharedFlow.emit(candle)

                healthState.update { current ->
                    current.copy(
                        latencyMs = (10..22).random().toLong(),
                        tickRatePerSec = (35..55).random().toDouble()
                    )
                }
            }
        }
    }

    private fun Double.toTwoDecimals() = String.format(java.util.Locale.US, "%.2f", this).toDouble()
}

@Singleton
class WebSocketRepositoryImpl @Inject constructor() : WebSocketRepository {
    private val connectionState = MutableStateFlow(ConnectionState.CONNECTED)

    override fun getConnectionState(): Flow<ConnectionState> = connectionState.asStateFlow()

    override suspend fun connect(provider: StreamingProviderType) {
        connectionState.value = ConnectionState.CONNECTING
        delay(400)
        connectionState.value = ConnectionState.CONNECTED
    }

    override suspend fun disconnect() {
        connectionState.value = ConnectionState.DISCONNECTED
    }

    override suspend fun sendHeartbeatPing(): Boolean {
        return connectionState.value == ConnectionState.CONNECTED
    }
}

@Singleton
class StreamSymbolSubscriptionRepositoryImpl @Inject constructor() : StreamSymbolSubscriptionRepository {
    private val subscriptionsState = MutableStateFlow(setOf("THYAO.IS", "GARAN.IS", "AAPL", "BTC-USD", "EUR/TRY"))

    override fun getSubscribedSymbols(): Flow<Set<String>> = subscriptionsState.asStateFlow()

    override suspend fun subscribeSymbol(symbol: String): Boolean {
        subscriptionsState.update { it + symbol }
        return true
    }

    override suspend fun unsubscribeSymbol(symbol: String): Boolean {
        subscriptionsState.update { it - symbol }
        return true
    }

    override suspend fun batchSubscribe(symbols: List<String>): Boolean {
        subscriptionsState.update { it + symbols }
        return true
    }
}

@Singleton
class TickRepositoryImpl @Inject constructor() : TickRepository {
    private val tickCache = mutableMapOf<String, MarketTickEvent>()

    override fun getLastKnownTick(symbol: String): MarketTickEvent? = tickCache[symbol]

    override fun getAllLastKnownTicks(): Map<String, MarketTickEvent> = tickCache.toMap()

    override suspend fun cacheTick(tick: MarketTickEvent) {
        tickCache[tick.symbol] = tick
    }
}
