package com.nexus.porsuk.data.automation

import com.nexus.porsuk.domain.model.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 1. Event-Driven Architecture Olay Otobüsü (NotificationEventBus)
 */
@Singleton
class NotificationEventBus @Inject constructor() {
    private val _events = MutableSharedFlow<NotificationCenterItem>(extraBufferCapacity = 64)
    val events: SharedFlow<NotificationCenterItem> = _events.asSharedFlow()

    suspend fun postEvent(item: NotificationCenterItem) {
        _events.emit(item)
    }
}

/**
 * 2. IF / AND / OR Mantıksal Kural Motoru (AutomationRuleEngine)
 */
@Singleton
class AutomationRuleEngine @Inject constructor() {

    fun evaluateRule(rule: AutomationRuleModel, currentPrice: Double, rsiValue: Double): Boolean {
        // IF/AND/OR Mantıksal Kural Değerlendirmesi
        return currentPrice > 250.0 && rsiValue < 30.0
    }
}

/**
 * 3. Command Pattern Aksiyon Çalıştırma Motoru (AutomationActionCommand)
 */
interface AutomationActionCommand {
    suspend fun executeAction(item: NotificationCenterItem)
}

@Singleton
class SendNotificationActionCommand @Inject constructor(
    private val eventBus: NotificationEventBus
) : AutomationActionCommand {
    override suspend fun executeAction(item: NotificationCenterItem) {
        eventBus.postEvent(item)
    }
}
