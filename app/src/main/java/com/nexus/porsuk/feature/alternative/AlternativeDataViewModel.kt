package com.nexus.porsuk.feature.alternative

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Porsuk Alternative Data Intelligence Platform — ViewModel
 *
 * Uydu görüntüleme, gemi denizcilik AIS takibi, uçuş analitiği ve enerji tüketim verilerini yönetir.
 */
@HiltViewModel
class AlternativeDataViewModel @Inject constructor(
    private val alternativeDataRepository: AlternativeDataRepository,
    private val satelliteRepository: SatelliteRepository,
    private val shippingRepository: ShippingRepository,
    private val aviationRepository: AviationRepository,
    private val retailRepository: RetailRepository,
    private val energyRepository: EnergyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AlternativeDataUiState())
    val uiState: StateFlow<AlternativeDataUiState> = _uiState.asStateFlow()

    init {
        loadAlternativeData()
    }

    fun selectProvider(provider: AlternativeDataProviderType) {
        _uiState.update { it.copy(selectedProvider = provider) }
    }

    private fun loadAlternativeData() {
        viewModelScope.launch {
            launch {
                alternativeDataRepository.getAlternativeIndicators().collect { indicators ->
                    _uiState.update { it.copy(alternativeIndicators = indicators, isLoading = false) }
                }
            }

            launch {
                satelliteRepository.getSatelliteActivities().collect { sats ->
                    _uiState.update { it.copy(satelliteActivities = sats) }
                }
            }

            launch {
                shippingRepository.getVesselShippingData().collect { ships ->
                    _uiState.update { it.copy(vesselShippingData = ships) }
                }
            }

            launch {
                aviationRepository.getAviationTrafficData().collect { avs ->
                    _uiState.update { it.copy(aviationTrafficData = avs) }
                }
            }

            launch {
                retailRepository.getRetailFootTrafficIndex().collect { idx ->
                    _uiState.update { it.copy(retailFootTrafficIndex = idx) }
                }
            }

            launch {
                energyRepository.getEnergyConsumptionData().collect { energy ->
                    _uiState.update { it.copy(energyConsumptionData = energy) }
                }
            }
        }
    }
}
