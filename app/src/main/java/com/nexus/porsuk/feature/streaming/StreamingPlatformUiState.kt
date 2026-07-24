package com.nexus.porsuk.feature.streaming

import com.nexus.porsuk.domain.model.*

/**
 * Porsuk Real-Time Streaming Data Platform — UI Ekran Durumu (StreamingPlatformUiState)
 */
data class StreamingPlatformUiState(
    val activeProvider: StreamingProviderType = StreamingProviderType.FINNHUB,
    val connectionState: ConnectionState = ConnectionState.CONNECTED,
    val subscribedSymbols: Set<String> = emptySet(),
    val latestTicks: List<MarketTickEvent> = emptyList(),
    val streamHealth: StreamHealthMetrics = StreamHealthMetrics(),
    val futureStubs: StreamingFutureStubs = StreamingFutureStubs(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
