package com.nexus.porsuk.domain.repository

import com.nexus.porsuk.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * 1. Güvenlik Deposu Sözleşmesi (SecurityRepository)
 */
interface SecurityRepository {
    fun getSecurityMetrics(): Flow<SecurityScoreMetrics>
}

/**
 * 2. Gizlilik Deposu Sözleşmesi (PrivacyRepository)
 */
interface PrivacyRepository {
    fun getPrivacyConsents(): Flow<PrivacyConsentModel>
    suspend fun updateConsent(consent: PrivacyConsentModel)
}

/**
 * 3. Kimlik Doğrulama Deposu Sözleşmesi (AuthenticationRepository)
 */
interface AuthenticationRepository {
    fun isBiometricsEnabled(): Flow<Boolean>
    suspend fun setBiometricsEnabled(enabled: Boolean)
}

/**
 * 4. Oturum Deposu Sözleşmesi (SessionRepository)
 */
interface SessionRepository {
    fun getActiveSessionsCount(): Flow<Int>
}

/**
 * 5. Denetim Günlüğü Deposu Sözleşmesi (AuditRepository)
 */
interface AuditRepository {
    fun getAuditLogs(): Flow<List<SecurityAuditLog>>
    suspend fun logSecurityEvent(title: String, description: String, category: AuditCategory)
}
