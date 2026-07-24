package com.nexus.porsuk.feature.alternative

import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

/**
 * Porsuk Alternative Data Intelligence Platform — ViewModel Unit Testleri
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AlternativeDataViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val fakeAlternativeDataRepository = object : AlternativeDataRepository {
        override fun getAlternativeIndicators() = flowOf(listOf(AlternativeIndicatorItem(name = "Tüketici Mobilite Endeksi")))
        override fun getSupportedProviders() = AlternativeDataProviderType.entries.toList()
    }

    private val fakeSatelliteRepository = object : SatelliteRepository {
        override fun getSatelliteActivities() = flowOf(listOf(SatelliteActivityItem(locationName = "Ambarlı Limanı")))
    }

    private val fakeShippingRepository = object : ShippingRepository {
        override fun getVesselShippingData() = flowOf(listOf(VesselShippingItem(portName = "Ambarlı Limanı")))
    }

    private val fakeAviationRepository = object : AviationRepository {
        override fun getAviationTrafficData() = flowOf(listOf(AviationTrafficItem(airportCode = "IST")))
    }

    private val fakeRetailRepository = object : RetailRepository {
        override fun getRetailFootTrafficIndex() = flowOf(112.5)
    }

    private val fakeEnergyRepository = object : EnergyRepository {
        override fun getEnergyConsumptionData() = flowOf(listOf(EnergyConsumptionItem(regionName = "Marmara")))
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadAlternativeData updates uiState with satellite, shipping, aviation, and energy data`() = runTest {
        val viewModel = AlternativeDataViewModel(
            alternativeDataRepository = fakeAlternativeDataRepository,
            satelliteRepository = fakeSatelliteRepository,
            shippingRepository = fakeShippingRepository,
            aviationRepository = fakeAviationRepository,
            retailRepository = fakeRetailRepository,
            energyRepository = fakeEnergyRepository
        )

        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(AlternativeDataProviderType.SATELLITE_IMAGERY, state.selectedProvider)
        assertEquals(1, state.alternativeIndicators.size)
        assertEquals("Tüketici Mobilite Endeksi", state.alternativeIndicators[0].name)
        assertEquals(1, state.satelliteActivities.size)
        assertEquals("Ambarlı Limanı", state.satelliteActivities[0].locationName)
        assertEquals(1, state.vesselShippingData.size)
        assertEquals(1, state.aviationTrafficData.size)
        assertEquals(112.5, state.retailFootTrafficIndex, 0.01)
        assertEquals(1, state.energyConsumptionData.size)
        assertEquals(false, state.isLoading)
    }

    @Test
    fun `selectProvider updates selected provider in uiState`() = runTest {
        val viewModel = AlternativeDataViewModel(
            alternativeDataRepository = fakeAlternativeDataRepository,
            satelliteRepository = fakeSatelliteRepository,
            shippingRepository = fakeShippingRepository,
            aviationRepository = fakeAviationRepository,
            retailRepository = fakeRetailRepository,
            energyRepository = fakeEnergyRepository
        )

        viewModel.selectProvider(AlternativeDataProviderType.SHIPPING_TRACKING)
        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(AlternativeDataProviderType.SHIPPING_TRACKING, state.selectedProvider)
    }
}
