package com.nexus.porsuk.feature.broker

import com.nexus.porsuk.domain.model.*

/**
 * Porsuk Broker Integration Hub — UI Ekran Durumu (BrokerHubUiState)
 */
data class BrokerHubUiState(
    val accounts: List<BrokerAccount> = emptyList(),
    val holdings: List<BrokerHoldingItem> = emptyList(),
    val selectedProvider: BrokerProviderType = BrokerProviderType.MIDAS,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
