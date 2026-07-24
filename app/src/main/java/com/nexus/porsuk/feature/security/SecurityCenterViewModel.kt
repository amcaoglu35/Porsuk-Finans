package com.nexus.porsuk.feature.security

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.domain.model.PrivacyConsentModel
import com.nexus.porsuk.domain.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Porsuk Security Center — ViewModel
 *
 * Güvenlik skorunu, biyometrik ayarları, bütünlük kontrollerini ve denetim günlüklerini yönetir.
 */
@HiltViewModel
class SecurityCenterViewModel @Inject constructor(
    private val securityRepository: SecurityRepository,
    private val privacyRepository: PrivacyRepository,
    private val authenticationRepository: AuthenticationRepository,
    private val auditRepository: AuditRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SecurityCenterUiState())
    val uiState: StateFlow<SecurityCenterUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun toggleBiometrics(enabled: Boolean) {
        viewModelScope.launch {
            authenticationRepository.setBiometricsEnabled(enabled)
            _uiState.update { current ->
                current.copy(metrics = current.metrics.copy(isBiometricEnabled = enabled))
            }
        }
    }

    fun updatePrivacyConsent(consent: PrivacyConsentModel) {
        viewModelScope.launch {
            privacyRepository.updateConsent(consent)
            _uiState.update { it.copy(privacyConsents = consent) }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            launch {
                securityRepository.getSecurityMetrics().collect { metrics ->
                    _uiState.update { it.copy(metrics = metrics, isLoading = false) }
                }
            }

            launch {
                privacyRepository.getPrivacyConsents().collect { consents ->
                    _uiState.update { it.copy(privacyConsents = consents) }
                }
            }

            launch {
                auditRepository.getAuditLogs().collect { logs ->
                    _uiState.update { it.copy(auditLogs = logs) }
                }
            }
        }
    }
}
