package com.nexus.porsuk.data.repository

import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlternativeDataRepositoryImpl @Inject constructor() : AlternativeDataRepository {

    private val defaultIndicators = listOf(
        AlternativeIndicatorItem(name = "Tüketici Mobilite Endeksi", category = "Mobilite", currentValue = 114.2, changePct = +3.5),
        AlternativeIndicatorItem(name = "Lojistik & Kargo Trafiği Endeksi", category = "Tedarik Zinciri", currentValue = 128.0, changePct = +5.2),
        AlternativeIndicatorItem(name = "Endüstriyel Üretim Isı Haritası", category = "Sanayi", currentValue = 98.4, changePct = -1.1)
    )

    private val indicatorsState = MutableStateFlow(defaultIndicators)

    override fun getAlternativeIndicators(): Flow<List<AlternativeIndicatorItem>> = indicatorsState.asStateFlow()
    override fun getSupportedProviders(): List<AlternativeDataProviderType> = AlternativeDataProviderType.entries.toList()
}

@Singleton
class SatelliteRepositoryImpl @Inject constructor() : SatelliteRepository {

    private val defaultSatellite = listOf(
        SatelliteActivityItem(locationName = "Ambarlı Limanı & Otopark Kompleksi", occupancyRatePct = 84.5, factoryActivityScore = 92),
        SatelliteActivityItem(locationName = "Kocaeli Otomotiv Üretim Tesisleri", occupancyRatePct = 91.0, factoryActivityScore = 96)
    )

    private val satelliteState = MutableStateFlow(defaultSatellite)

    override fun getSatelliteActivities(): Flow<List<SatelliteActivityItem>> = satelliteState.asStateFlow()
}

@Singleton
class ShippingRepositoryImpl @Inject constructor() : ShippingRepository {

    private val defaultShipping = listOf(
        VesselShippingItem(portName = "Ambarlı / Kocaeli Limanı", containerVolumeTons = 124000L, portCongestionLevel = "MODERATE (%12 Bekleme)", activeVesselsCount = 18),
        VesselShippingItem(portName = "Mersin Uluslararası Limanı", containerVolumeTons = 98000L, portCongestionLevel = "LOW (%4 Bekleme)", activeVesselsCount = 12)
    )

    private val shippingState = MutableStateFlow(defaultShipping)

    override fun getVesselShippingData(): Flow<List<VesselShippingItem>> = shippingState.asStateFlow()
}

@Singleton
class AviationRepositoryImpl @Inject constructor() : AviationRepository {

    private val defaultAviation = listOf(
        AviationTrafficItem(airportCode = "IST / SAW (İstanbul)", commercialFlightsCount = 1420, cargoCapacityTons = 4200L, privateJetActivityLevel = "Yüksek")
    )

    private val aviationState = MutableStateFlow(defaultAviation)

    override fun getAviationTrafficData(): Flow<List<AviationTrafficItem>> = aviationState.asStateFlow()
}

@Singleton
class RetailRepositoryImpl @Inject constructor() : RetailRepository {
    private val retailState = MutableStateFlow(112.5)

    override fun getRetailFootTrafficIndex(): Flow<Double> = retailState.asStateFlow()
}

@Singleton
class EnergyRepositoryImpl @Inject constructor() : EnergyRepository {

    private val defaultEnergy = listOf(
        EnergyConsumptionItem(regionName = "Marmara Sanayi Bölgesi", electricityConsumptionGWh = 42.8, oilStorageOccupancyPct = 78.5)
    )

    private val energyState = MutableStateFlow(defaultEnergy)

    override fun getEnergyConsumptionData(): Flow<List<EnergyConsumptionItem>> = energyState.asStateFlow()
}
