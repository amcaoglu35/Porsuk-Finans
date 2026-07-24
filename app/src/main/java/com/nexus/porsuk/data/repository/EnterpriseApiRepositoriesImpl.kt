package com.nexus.porsuk.data.repository

import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EnterpriseApiRepositoryImpl @Inject constructor() : EnterpriseApiRepository {
    override fun getSupportedProtocols(): List<ApiProtocolType> = ApiProtocolType.entries.toList()
    override fun getSupportedAuthMethods(): List<ApiAuthMethod> = ApiAuthMethod.entries.toList()
}

@Singleton
class EnterpriseAuthenticationRepositoryImpl @Inject constructor() : EnterpriseAuthenticationRepository {

    private val defaultKeys = listOf(
        ApiKeyItem(
            keyId = "key_prod_bot",
            name = "Production Algo Trading Bot Key",
            keyPrefix = "pk_live_84a9****",
            scopes = listOf("read:portfolio", "read:markets", "write:alerts"),
            rateLimitRpm = 240
        ),
        ApiKeyItem(
            keyId = "key_dev_test",
            name = "Staging / Test Key",
            keyPrefix = "pk_test_12c4****",
            scopes = listOf("read:markets"),
            rateLimitRpm = 60
        )
    )

    private val apiKeysState = MutableStateFlow(defaultKeys)

    override fun getActiveApiKeys(): Flow<List<ApiKeyItem>> = apiKeysState.asStateFlow()

    override suspend fun createApiKey(name: String, scopes: List<String>): ApiKeyItem {
        val newKey = ApiKeyItem(
            name = name,
            keyPrefix = "pk_live_${(1000..9999).random()}****",
            scopes = scopes
        )
        apiKeysState.update { current -> listOf(newKey) + current }
        return newKey
    }

    override suspend fun revokeApiKey(keyId: String): Boolean {
        apiKeysState.update { current -> current.filter { it.keyId != keyId } }
        return true
    }
}

@Singleton
class EnterpriseWebhookRepositoryImpl @Inject constructor() : EnterpriseWebhookRepository {

    private val defaultWebhooks = listOf(
        WebhookSubscription(
            webhookId = "wh_zapier",
            targetUrl = "https://hooks.zapier.com/hooks/catch/98231/porsuk",
            eventTypes = listOf("portfolio.rebalanced", "alert.triggered")
        ),
        WebhookSubscription(
            webhookId = "wh_make",
            targetUrl = "https://hook.eu1.make.com/porsuk-finance-events",
            eventTypes = listOf("order.executed", "earnings.filing")
        )
    )

    private val webhooksState = MutableStateFlow(defaultWebhooks)

    override fun getWebhookSubscriptions(): Flow<List<WebhookSubscription>> = webhooksState.asStateFlow()

    override suspend fun registerWebhook(targetUrl: String, events: List<String>): WebhookSubscription {
        val newWh = WebhookSubscription(targetUrl = targetUrl, eventTypes = events)
        webhooksState.update { current -> listOf(newWh) + current }
        return newWh
    }

    override suspend fun testWebhookDelivery(webhookId: String): Boolean = true
}

@Singleton
class EnterpriseAutomationRepositoryImpl @Inject constructor() : EnterpriseAutomationRepository {

    private val defaultAutomations = listOf(
        AutomationIntegration(providerName = "Zapier Platform", iconEmoji = "⚡", isConnected = true, activeWorkflowsCount = 4),
        AutomationIntegration(providerName = "Make.com (Integromat)", iconEmoji = "🔮", isConnected = true, activeWorkflowsCount = 2),
        AutomationIntegration(providerName = "n8n Open Automation", iconEmoji = "⚙️", isConnected = false, activeWorkflowsCount = 0),
        AutomationIntegration(providerName = "Microsoft Power Automate", iconEmoji = "🟦", isConnected = false, activeWorkflowsCount = 0)
    )

    private val automationState = MutableStateFlow(defaultAutomations)

    override fun getAutomationIntegrations(): Flow<List<AutomationIntegration>> = automationState.asStateFlow()
}

@Singleton
class EnterpriseUsageRepositoryImpl @Inject constructor() : EnterpriseUsageRepository {

    private val defaultStats = listOf(
        EndpointStat(endpointPath = "/v1/markets/ticks", httpMethod = "GET", totalCallsCount = 142000L, avgLatencyMs = 24.5, errorRatePct = 0.01),
        EndpointStat(endpointPath = "/v1/portfolio/summary", httpMethod = "GET", totalCallsCount = 38400L, avgLatencyMs = 38.2, errorRatePct = 0.00),
        EndpointStat(endpointPath = "/v1/ai/copilot/prompt", httpMethod = "POST", totalCallsCount = 12500L, avgLatencyMs = 185.0, errorRatePct = 0.04),
        EndpointStat(endpointPath = "/v1/webhooks/trigger", httpMethod = "POST", totalCallsCount = 9800L, avgLatencyMs = 45.0, errorRatePct = 0.02)
    )

    private val usageState = MutableStateFlow(defaultStats)

    override fun getEndpointStatistics(): Flow<List<EndpointStat>> = usageState.asStateFlow()
}
