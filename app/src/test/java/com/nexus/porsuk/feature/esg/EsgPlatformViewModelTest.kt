package com.nexus.porsuk.feature.esg

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
 * Porsuk ESG & Sustainability Intelligence Platform — ViewModel Unit Testleri
 */
@OptIn(ExperimentalCoroutinesApi::class)
class EsgPlatformViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val fakeEsgRepository = object : ESGRepository {
        override fun getEsgScoreData(symbol: String) = flowOf(EsgScoreData(overallScore = 84))
        override fun getSupportedProviders() = EsgProviderType.entries.toList()
    }

    private val fakeSustainabilityRepository = object : SustainabilityRepository {
        override fun getControversyAlerts(symbol: String) = flowOf(listOf(EsgControversyAlert(title = "Karbon Emisyon %15 Azaltıldı")))
    }

    private val fakeClimateRepository = object : ClimateRepository {
        override fun getEnvironmentalPillar(symbol: String) = flowOf(EnvironmentalPillar(renewableEnergyUsagePct = 64.5))
    }

    private val fakeGovernanceRepository = object : GovernanceRepository {
        override fun getGovernancePillar(symbol: String) = flowOf(GovernancePillar(independentDirectorsPct = 68.0))
    }

    private val fakeSocialRepository = object : SocialRepository {
        override fun getSocialPillar(symbol: String) = flowOf(SocialPillar(genderDiversityPct = 42.5))
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
    fun `loadEsgPlatformData updates uiState with ESG scores, E-S-G pillars, and controversy alerts`() = runTest {
        val viewModel = EsgPlatformViewModel(
            esgRepository = fakeEsgRepository,
            sustainabilityRepository = fakeSustainabilityRepository,
            climateRepository = fakeClimateRepository,
            governanceRepository = fakeGovernanceRepository,
            socialRepository = fakeSocialRepository
        )

        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(EsgProviderType.MSCI_ESG, state.selectedProvider)
        assertEquals(84, state.esgScore.overallScore)
        assertEquals(64.5, state.environmentalPillar.renewableEnergyUsagePct, 0.01)
        assertEquals(42.5, state.socialPillar.genderDiversityPct, 0.01)
        assertEquals(68.0, state.governancePillar.independentDirectorsPct, 0.01)
        assertEquals(1, state.controversyAlerts.size)
        assertEquals(false, state.isLoading)
    }

    @Test
    fun `selectProvider updates selected provider in uiState`() = runTest {
        val viewModel = EsgPlatformViewModel(
            esgRepository = fakeEsgRepository,
            sustainabilityRepository = fakeSustainabilityRepository,
            climateRepository = fakeClimateRepository,
            governanceRepository = fakeGovernanceRepository,
            socialRepository = fakeSocialRepository
        )

        viewModel.selectProvider(EsgProviderType.SUSTAINALYTICS)
        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(EsgProviderType.SUSTAINALYTICS, state.selectedProvider)
    }
}
