package com.nexus.porsuk.feature.alternative

import com.nexus.porsuk.domain.model.*

/**
 * Porsuk Alternative Data Intelligence Platform — UI Ekran Durumu (AlternativeDataUiState)
 */
data class AlternativeDataUiState(
    val selectedProvider: AlternativeDataProviderType = AlternativeDataProviderType.SATELLITE_IMAGERY,
    val alternativeIndicators: List<AlternativeIndicatorItem> = emptyList(),
    val satelliteActivities: List<SatelliteActivityItem> = emptyList(),
    val vesselShippingData: List<VesselShippingItem> = emptyList(),
    val aviationTrafficData: List<AviationTrafficItem> = emptyList(),
    val energyConsumptionData: List<EnergyConsumptionItem> = emptyList(),
    val retailFootTrafficIndex: Double = 112.5,
    val futureStubs: AlternativeDataFutureStubs = AlternativeDataFutureStubs(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
