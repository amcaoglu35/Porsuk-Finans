package com.nexus.porsuk.feature.broker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.domain.model.BrokerProviderType
import com.nexus.porsuk.domain.repository.BrokerRepository
import com.nexus.porsuk.domain.repository.PortfolioSyncRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Porsuk Broker Integration Hub — ViewModel
 *
 * 7 Aracı kurum bağlantısını, portföy senkronizasyonunu ve emir altyapısını yönetir.
 */
@HiltViewModel
class BrokerHubViewModel @Inject constructor(
    private val brokerRepository: BrokerRepository,
    private val syncRepository: PortfolioSyncRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BrokerHubUiState())
    val uiState: StateFlow<BrokerHubUiState> = _uiState.asStateFlow()

    init {
        loadAccounts()
        loadHoldings(_uiState.value.selectedProvider)
    }

    fun selectProvider(provider: BrokerProviderType) {
        _uiState.update { it.copy(selectedProvider = provider, isLoading = true) }
        loadHoldings(provider)
    }

    private fun loadAccounts() {
        viewModelScope.launch {
            brokerRepository.getConnectedAccounts().collect { list ->
                _uiState.update { it.copy(accounts = list) }
            }
        }
    }

    private fun loadHoldings(provider: BrokerProviderType) {
        viewModelScope.launch {
            syncRepository.syncHoldings(provider).collect { list ->
                _uiState.update { it.copy(holdings = list, isLoading = false) }
            }
        }
    }
}
