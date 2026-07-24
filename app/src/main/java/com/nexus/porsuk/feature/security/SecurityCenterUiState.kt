package com.nexus.porsuk.feature.security

import com.nexus.porsuk.domain.model.*

/**
 * Porsuk Security Center — UI Ekran Durumu (SecurityCenterUiState)
 */
data class SecurityCenterUiState(
    val metrics: SecurityScoreMetrics = SecurityScoreMetrics(),
    val privacyConsents: PrivacyConsentModel = PrivacyConsentModel(),
    val auditLogs: List<SecurityAuditLog> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
