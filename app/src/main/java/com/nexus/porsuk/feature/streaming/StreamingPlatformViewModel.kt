package com.nexus.porsuk.feature.streaming

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Porsuk Real-Time Streaming Data Platform — ViewModel
 *
 * Gerçek zamanlı borsa fiyat tıkları, WebSocket bağlantı sağlığı ve sembol aboneliklerini yönetir.
 */
@HiltViewModel
class StreamingPlatformViewModel @Inject constructor(
    private val streamingRepository: StreamingRepository,
    private val webSocketRepository: WebSocketRepository,
    private val subscriptionRepository: StreamSymbolSubscriptionRepository,
    private val tickRepository: TickRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StreamingPlatformUiState())
    val uiState: StateFlow<StreamingPlatformUiState> = _uiState.asStateFlow()

    init {
        loadStreamingData()
    }

    fun selectProvider(provider: StreamingProviderType) {
        viewModelScope.launch {
            _uiState.update { it.copy(activeProvider = provider) }
            webSocketRepository.connect(provider)
        }
    }

    fun subscribeSymbol(symbol: String) {
        viewModelScope.launch {
            subscriptionRepository.subscribeSymbol(symbol)
        }
    }

    fun unsubscribeSymbol(symbol: String) {
        viewModelScope.launch {
            subscriptionRepository.unsubscribeSymbol(symbol)
        }
    }

    fun toggleConnection() {
        viewModelScope.launch {
            if (_uiState.value.connectionState == ConnectionState.CONNECTED) {
                webSocketRepository.disconnect()
            } else {
                webSocketRepository.connect(_uiState.value.activeProvider)
            }
        }
    }

    private fun loadStreamingData() {
        viewModelScope.launch {
            launch {
                webSocketRepository.getConnectionState().collect { conn ->
                    _uiState.update { it.copy(connectionState = conn, isLoading = false) }
                }
            }

            launch {
                subscriptionRepository.getSubscribedSymbols().collect { symbols ->
                    _uiState.update { it.copy(subscribedSymbols = symbols) }
                }
            }

            launch {
                streamingRepository.getStreamHealth().collect { health ->
                    _uiState.update { it.copy(streamHealth = health) }
                }
            }

            launch {
                streamingRepository.getMarketTickStream().collect { newTick ->
                    _uiState.update { current ->
                        val updatedList = (listOf(newTick) + current.latestTicks).take(20)
                        current.copy(latestTicks = updatedList)
                    }
                }
            }
        }
    }
}
