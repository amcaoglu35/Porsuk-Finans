package com.nexus.porsuk.feature.globalmarkets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.domain.model.MarketRegion
import com.nexus.porsuk.domain.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Porsuk Global Markets Center — ViewModel
 *
 * 8 Küresel bölgeyi, borsa açılış/kapanış durumlarını, 10 sektörü ve dünya ısı haritasını yönetir.
 */
@HiltViewModel
class GlobalMarketsViewModel @Inject constructor(
    private val globalMarketRepository: GlobalMarketRepository,
    private val exchangeRepository: ExchangeRepository,
    private val sectorRepository: SectorRepository,
    private val globalIndexRepository: GlobalIndexRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GlobalMarketsUiState())
    val uiState: StateFlow<GlobalMarketsUiState> = _uiState.asStateFlow()

    init {
        loadRegionData(_uiState.value.selectedRegion)
        loadSectorsAndHeatMap()
    }

    fun selectRegion(region: MarketRegion) {
        _uiState.update { it.copy(selectedRegion = region) }
        loadRegionData(region)
    }

    private fun loadRegionData(region: MarketRegion) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            launch {
                globalMarketRepository.getMarketTickers(region).collect { tickers ->
                    _uiState.update { it.copy(tickers = tickers, isLoading = false) }
                }
            }

            launch {
                exchangeRepository.getExchangeStatus(region).collect { status ->
                    _uiState.update { it.copy(exchangeStatus = status) }
                }
            }
        }
    }

    private fun loadSectorsAndHeatMap() {
        viewModelScope.launch {
            launch {
                sectorRepository.getSectorPerformances().collect { list ->
                    _uiState.update { it.copy(sectors = list) }
                }
            }

            launch {
                globalIndexRepository.getWorldHeatMap().collect { map ->
                    _uiState.update { it.copy(heatMapData = map) }
                }
            }
        }
    }
}
