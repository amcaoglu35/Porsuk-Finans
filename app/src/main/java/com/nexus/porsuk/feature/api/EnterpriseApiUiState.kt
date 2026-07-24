package com.nexus.porsuk.feature.api

import com.nexus.porsuk.domain.model.*

/**
 * Porsuk Enterprise API & Automation Platform — UI Ekran Durumu (EnterpriseApiUiState)
 */
data class EnterpriseApiUiState(
    val activeProtocol: ApiProtocolType = ApiProtocolType.REST,
    val selectedVersion: ApiVersion = ApiVersion.V1,
    val apiKeys: List<ApiKeyItem> = emptyList(),
    val webhooks: List<WebhookSubscription> = emptyList(),
    val automations: List<AutomationIntegration> = emptyList(),
    val endpointStats: List<EndpointStat> = emptyList(),
    val futureStubs: EnterpriseApiFutureStubs = EnterpriseApiFutureStubs(),
    val newKeyNameInput: String = "",
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
