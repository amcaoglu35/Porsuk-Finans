package com.nexus.porsuk.domain.repository

import com.nexus.porsuk.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * 1. Alternatif Veri Deposu Sözleşmesi (AlternativeDataRepository)
 */
interface AlternativeDataRepository {
    fun getAlternativeIndicators(): Flow<List<AlternativeIndicatorItem>>
    fun getSupportedProviders(): List<AlternativeDataProviderType>
}

/**
 * 2. Uydu Görüntüleme Deposu Sözleşmesi (SatelliteRepository)
 */
interface SatelliteRepository {
    fun getSatelliteActivities(): Flow<List<SatelliteActivityItem>>
}

/**
 * 3. Denizcilik & Liman Deposu Sözleşmesi (ShippingRepository)
 */
interface ShippingRepository {
    fun getVesselShippingData(): Flow<List<VesselShippingItem>>
}

/**
 * 4. Havacılık & Uçuş Deposu Sözleşmesi (AviationRepository)
 */
interface AviationRepository {
    fun getAviationTrafficData(): Flow<List<AviationTrafficItem>>
}

/**
 * 5. Perakende & Tüketim Deposu Sözleşmesi (RetailRepository)
 */
interface RetailRepository {
    fun getRetailFootTrafficIndex(): Flow<Double>
}

/**
 * 6. Enerji & Tüketim Deposu Sözleşmesi (EnergyRepository)
 */
interface EnergyRepository {
    fun getEnergyConsumptionData(): Flow<List<EnergyConsumptionItem>>
}
