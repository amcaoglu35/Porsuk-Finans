package com.nexus.porsuk.domain.model

/**
 * API İletişim Protokolü (ApiProtocolType)
 */
enum class ApiProtocolType(val displayName: String, val iconEmoji: String) {
    REST("REST API (OpenAPI 3.1)", "⚡"),
    GRAPHQL("GraphQL API", "🕸️"),
    INTERNAL_API("Internal Microservice API", "🔒"),
    PUBLIC_API("Public Developer API", "🌐"),
    PARTNER_API("Enterprise Partner API", "🤝");
}

/**
 * Kimlik Doğrulama Yöntemi (ApiAuthMethod)
 */
enum class ApiAuthMethod(val displayName: String) {
    OAUTH_2_0("OAuth 2.0 PKCE / Client Credentials"),
    OPENID_CONNECT("OpenID Connect (OIDC)"),
    API_KEY("API Keys (Bearer Token)"),
    JWT_BEARER("JSON Web Token (JWT)"),
    PERSONAL_ACCESS_TOKEN("Personal Access Token (PAT)");
}

/**
 * API Sürümü (ApiVersion)
 */
enum class ApiVersion(val code: String) {
    V1("v1.4 (Stable)"),
    V2("v2.0 (Beta)");
}

/**
 * API Anahtarı (ApiKeyItem)
 */
data class ApiKeyItem(
    val keyId: String = "key_${System.currentTimeMillis()}",
    val name: String = "Production Trading Bot Key",
    val keyPrefix: String = "pk_live_84a9****",
    val scopes: List<String> = listOf("read:portfolio", "read:markets", "write:alerts"),
    val rateLimitRpm: Int = 120,
    val createdAt: String = "24 Temmuz 2026",
    val isActive: Boolean = true
)

/**
 * Webhook Aboneliği (WebhookSubscription)
 */
data class WebhookSubscription(
    val webhookId: String = "wh_${System.currentTimeMillis()}",
    val targetUrl: String = "https://api.myapp.com/webhooks/porsuk",
    val eventTypes: List<String> = listOf("portfolio.rebalanced", "alert.triggered", "order.executed"),
    val isVerified: Boolean = true,
    val secretKey: String = "whsec_98f12a3d****"
)

/**
 * Otomasyon Entegrasyonu (AutomationIntegration)
 */
data class AutomationIntegration(
    val providerName: String = "Zapier Platform",
    val iconEmoji: String = "⚡",
    val isConnected: Boolean = true,
    val activeWorkflowsCount: Int = 4,
    val lastTriggerTimestamp: String = "2 dakika önce"
)

/**
 * Endpoint İstatistikleri (EndpointStat)
 */
data class EndpointStat(
    val endpointPath: String = "/v1/markets/ticks",
    val httpMethod: String = "GET",
    val totalCallsCount: Long = 142000L,
    val avgLatencyMs: Double = 24.5,
    val errorRatePct: Double = 0.02
)

/**
 * Geleceğe Hazır Enterprise API Stub Modeli (EnterpriseApiFutureStubs)
 */
data class EnterpriseApiFutureStubs(
    val isGrpcSupportReady: Boolean = true,
    val isWebSocketStreamApiActive: Boolean = true,
    val isMcpServerIntegrationReady: Boolean = true,
    val isAiToolCallingApiActive: Boolean = true,
    val isEnterpriseSsoReady: Boolean = false,
    val isBillingApiActive: Boolean = true
)
