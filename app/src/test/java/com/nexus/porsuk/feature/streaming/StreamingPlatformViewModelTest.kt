package com.nexus.porsuk.feature.streaming

import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

/**
 * Porsuk Real-Time Streaming Data Platform — ViewModel Unit Testleri
 */
@OptIn(ExperimentalCoroutinesApi::class)
class StreamingPlatformViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val tickSharedFlow = MutableSharedFlow<MarketTickEvent>()

    private val fakeStreamingRepository = object : StreamingRepository {
        override fun getMarketTickStream() = tickSharedFlow
        override fun getLiveCandleStream() = MutableSharedFlow<LiveCandle>()
        override fun getStreamHealth() = flowOf(StreamHealthMetrics())
    }

    private val fakeWebSocketRepository = object : WebSocketRepository {
        override fun getConnectionState() = flowOf(ConnectionState.CONNECTED)
        override suspend fun connect(provider: StreamingProviderType) {}
        override suspend fun disconnect() {}
        override suspend fun sendHeartbeatPing() = true
    }

    private val fakeSubscriptionRepository = object : StreamSymbolSubscriptionRepository {
        override fun getSubscribedSymbols() = flowOf(setOf("THYAO.IS", "GARAN.IS"))
        override suspend fun subscribeSymbol(symbol: String) = true
        override suspend fun unsubscribeSymbol(symbol: String) = true
        override suspend fun batchSubscribe(symbols: List<String>) = true
    }

    private val fakeTickRepository = object : TickRepository {
        override fun getLastKnownTick(symbol: String): MarketTickEvent? = null
        override fun getAllLastKnownTicks() = emptyMap<String, MarketTickEvent>()
        override suspend fun cacheTick(tick: MarketTickEvent) {}
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadStreamingData updates uiState with connection state and subscribed symbols`() = runTest {
        val viewModel = StreamingPlatformViewModel(
            streamingRepository = fakeStreamingRepository,
            webSocketRepository = fakeWebSocketRepository,
            subscriptionRepository = fakeSubscriptionRepository,
            tickRepository = fakeTickRepository
        )

        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(ConnectionState.CONNECTED, state.connectionState)
        assertEquals(2, state.subscribedSymbols.size)
        assertEquals(false, state.isLoading)
        assertNotNull(state.streamHealth)
    }

    @Test
    fun `market tick stream emits new ticks and updates uiState latestTicks`() = runTest {
        val viewModel = StreamingPlatformViewModel(
            streamingRepository = fakeStreamingRepository,
            webSocketRepository = fakeWebSocketRepository,
            subscriptionRepository = fakeSubscriptionRepository,
            tickRepository = fakeTickRepository
        )

        testScheduler.advanceUntilIdle()

        val tick = MarketTickEvent(symbol = "THYAO.IS", lastPrice = 310.0, changeAmount = 2.0, changePct = 0.65, volume = 5000, bidPrice = 309.9, askPrice = 310.1)
        tickSharedFlow.emit(tick)
        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, state.latestTicks.size)
        assertEquals("THYAO.IS", state.latestTicks[0].symbol)
    }
}
