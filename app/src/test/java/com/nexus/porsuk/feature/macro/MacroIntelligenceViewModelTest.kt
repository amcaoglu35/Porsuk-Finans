package com.nexus.porsuk.feature.macro

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
 * Porsuk Macro Intelligence Platform — ViewModel Unit Testleri
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MacroIntelligenceViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val fakeMacroRepository = object : MacroRepository {
        override fun getMacroAiOutlook() = flowOf(MacroAiOutlook(recessionProbabilityPct = 18.5))
        override fun getSupportedProviders() = MacroProviderType.entries.toList()
    }

    private val fakeIndicatorRepository = object : MacroIndicatorRepository {
        override fun getEconomicIndicators() = flowOf(listOf(EconomicIndicator(name = "TÜFE")))
        override fun getIndicatorsByCategory(category: MacroIndicatorCategory) = flowOf(emptyList<EconomicIndicator>())
    }

    private val fakeCentralBankRepository = object : CentralBankRepository {
        override fun getCentralBankPolicies() = flowOf(listOf(CentralBankPolicy(bankType = CentralBankType.TCMB, policyRatePct = 50.0)))
        override suspend fun getPolicyDetails(bank: CentralBankType) = CentralBankPolicy()
    }

    private val fakeBondRepository = object : BondRepository {
        override fun getGovernmentBondYields() = flowOf(listOf(BondYieldItem(bondSymbol = "US10Y")))
    }

    private val fakeFXRepository = object : FXRepository {
        override fun getMajorFxCrosses() = flowOf(mapOf("USD/TRY" to 32.85))
    }

    private val fakeCommodityRepository = object : MacroCommodityRepository {
        override fun getCommodityPrices() = flowOf(listOf(CommodityItem(commoditySymbol = "XAU-USD")))
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
    fun `loadMacroData updates uiState with indicators, central bank policies, and AI outlook`() = runTest {
        val viewModel = MacroIntelligenceViewModel(
            macroRepository = fakeMacroRepository,
            indicatorRepository = fakeIndicatorRepository,
            centralBankRepository = fakeCentralBankRepository,
            bondRepository = fakeBondRepository,
            fxRepository = fakeFXRepository,
            commodityRepository = fakeCommodityRepository
        )

        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(MacroDashboardTab.GLOBAL_HEATMAP, state.activeTab)
        assertEquals(18.5, state.aiOutlook.recessionProbabilityPct, 0.01)
        assertEquals(1, state.indicators.size)
        assertEquals("TÜFE", state.indicators[0].name)
        assertEquals(1, state.centralBankPolicies.size)
        assertEquals(50.0, state.centralBankPolicies[0].policyRatePct, 0.01)
        assertEquals(false, state.isLoading)
    }
}
