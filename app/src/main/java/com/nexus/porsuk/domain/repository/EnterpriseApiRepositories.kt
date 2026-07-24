package com.nexus.porsuk.domain.repository

import com.nexus.porsuk.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * 1. Kurumsal API Deposu Sözleşmesi (EnterpriseApiRepository)
 */
interface EnterpriseApiRepository {
    fun getSupportedProtocols(): List<ApiProtocolType>
    fun getSupportedAuthMethods(): List<ApiAuthMethod>
}

/**
 * 2. API Kimlik Doğrulama Deposu Sözleşmesi (EnterpriseAuthenticationRepository)
 */
interface EnterpriseAuthenticationRepository {
    fun getActiveApiKeys(): Flow<List<ApiKeyItem>>
    suspend fun createApiKey(name: String, scopes: List<String>): ApiKeyItem
    suspend fun revokeApiKey(keyId: String): Boolean
}

/**
 * 3. Webhook Otomasyon Deposu Sözleşmesi (EnterpriseWebhookRepository)
 */
interface EnterpriseWebhookRepository {
    fun getWebhookSubscriptions(): Flow<List<WebhookSubscription>>
    suspend fun registerWebhook(targetUrl: String, events: List<String>): WebhookSubscription
    suspend fun testWebhookDelivery(webhookId: String): Boolean
}

/**
 * 4. Geliştirici Otomasyon Araçları Deposu Sözleşmesi (EnterpriseAutomationRepository)
 */
interface EnterpriseAutomationRepository {
    fun getAutomationIntegrations(): Flow<List<AutomationIntegration>>
}

/**
 * 5. API Kullanım Metrikleri Deposu Sözleşmesi (EnterpriseUsageRepository)
 */
interface EnterpriseUsageRepository {
    fun getEndpointStatistics(): Flow<List<EndpointStat>>
}
