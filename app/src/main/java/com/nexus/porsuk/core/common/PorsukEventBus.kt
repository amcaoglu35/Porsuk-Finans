package com.nexus.porsuk.core.common

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Porsuk Finans Centralized Event Bus.
 * Enables decoupled communication between different modules (e.g., Markets -> AI, Portfolio -> Automation).
 */
@Singleton
class PorsukEventBus @Inject constructor() {

    private val _events = MutableSharedFlow<PorsukEvent>(extraBufferCapacity = 64)
    val events = _events.asSharedFlow()

    /**
     * Publishes a new event to the bus.
     */
    suspend fun publish(event: PorsukEvent) {
        _events.emit(event)
    }
}

/**
 * Base sealed class for all system events.
 */
sealed class PorsukEvent {
    data class PriceUpdated(val symbol: String, val newPrice: Double, val changePct: Double) : PorsukEvent()
    data class PortfolioChanged(val totalValue: Double) : PorsukEvent()
    data class NewsUpdated(val symbol: String, val latestHeadlines: List<String>) : PorsukEvent()
    data class AlarmTriggered(val alertId: Int, val title: String) : PorsukEvent()
    data class AnalysisFinished(val symbol: String, val score: Int) : PorsukEvent()
    data object InternetConnectionLost : PorsukEvent()
    data object InternetConnectionRestored : PorsukEvent()
}
