package com.nexus.porsuk.feature.api

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Porsuk Enterprise API & Automation Platform — ViewModel
 *
 * REST/GraphQL API anahtarlarını, Webhook teslimatlarını, Zapier/Make otomasyon entegrasyonlarını ve latency istatistiklerini yönetir.
 */
@HiltViewModel
class EnterpriseApiViewModel @Inject constructor(
    private val apiRepository: EnterpriseApiRepository,
    private val authenticationRepository: EnterpriseAuthenticationRepository,
    private val webhookRepository: EnterpriseWebhookRepository,
    private val automationRepository: EnterpriseAutomationRepository,
    private val usageRepository: EnterpriseUsageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EnterpriseApiUiState())
    val uiState: StateFlow<EnterpriseApiUiState> = _uiState.asStateFlow()

    init {
        loadApiPlatformData()
    }

    fun selectProtocol(protocol: ApiProtocolType) {
        _uiState.update { it.copy(activeProtocol = protocol) }
    }

    fun onNewKeyNameInputChange(name: String) {
        _uiState.update { it.copy(newKeyNameInput = name) }
    }

    fun createNewApiKey() {
        val name = uiState.value.newKeyNameInput
        if (name.isBlank()) return

        viewModelScope.launch {
            authenticationRepository.createApiKey(name, listOf("read:portfolio", "read:markets"))
            _uiState.update { it.copy(newKeyNameInput = "") }
        }
    }

    fun revokeApiKey(keyId: String) {
        viewModelScope.launch {
            authenticationRepository.revokeApiKey(keyId)
        }
    }

    private fun loadApiPlatformData() {
        viewModelScope.launch {
            launch {
                authenticationRepository.getActiveApiKeys().collect { keys ->
                    _uiState.update { it.copy(apiKeys = keys, isLoading = false) }
                }
            }

            launch {
                webhookRepository.getWebhookSubscriptions().collect { whs ->
                    _uiState.update { it.copy(webhooks = whs) }
                }
            }

            launch {
                automationRepository.getAutomationIntegrations().collect { autos ->
                    _uiState.update { it.copy(automations = autos) }
                }
            }

            launch {
                usageRepository.getEndpointStatistics().collect { stats ->
                    _uiState.update { it.copy(endpointStats = stats) }
                }
            }
        }
    }
}
