package com.nexus.porsuk.domain.model

/**
 * Güvenlik Skoru ve Metrikleri (SecurityScoreMetrics)
 */
data class SecurityScoreMetrics(
    val score: Int = 98,
    val securityLevel: SecurityLevel = SecurityLevel.OPTIMAL,
    val isBiometricEnabled: Boolean = true,
    val isPinSet: Boolean = true,
    val isRooted: Boolean = false,
    val isEmulator: Boolean = false,
    val isDebuggerAttached: Boolean = false,
    val activeSessionsCount: Int = 2
)

/**
 * KVKK / GDPR Gizlilik İzinleri Modeli (PrivacyConsentModel)
 */
data class PrivacyConsentModel(
    val analyticsConsent: Boolean = true,
    val aiPersonalizationConsent: Boolean = true,
    val marketingConsent: Boolean = false,
    val crashReportingConsent: Boolean = true,
    val lastUpdatedTimestamp: Long = System.currentTimeMillis()
)

/**
 * Güvenlik Denetim Günlüğü (SecurityAuditLog)
 */
data class SecurityAuditLog(
    val logId: String = "log_${System.currentTimeMillis()}",
    val category: AuditCategory = AuditCategory.SECURITY,
    val title: String,
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)
