package com.nexus.porsuk.feature.macro

import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
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
        override fun getEconomicIndicators() = flowOf(listOf(EconomicIndicator(name = "TÜFE", category = MacroIndicatorCategory.INFLATION)))
        override fun getIndicatorsByCategory(category: MacroIndicatorCategory) = flowOf(emptyList<EconomicIndicator>())
        override suspend fun refreshIndicators(): Result<Unit> = Result.success(Unit)
        override fun getIndicatorHistory(indicatorId: String): Flow<List<Double>> = flowOf(listOf(3.0, 3.2, 3.4))
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
    fun `loadMacroData updates uiState with indicators and AI outlook`() = runTest {
        val viewModel = MacroIntelligenceViewModel(
            macroRepository = fakeMacroRepository,
            indicatorRepository = fakeIndicatorRepository
        )

        val collectJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }

        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(MacroDashboardTab.INFLATION, state.activeTab)
        assertEquals(18.5, state.aiOutlook.recessionProbabilityPct, 0.01)
        assertEquals(1, state.indicators.size)
        assertEquals("TÜFE", state.indicators[0].name)
        assertEquals(false, state.isLoading)
    }
}
