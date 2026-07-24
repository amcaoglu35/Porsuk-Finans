package com.nexus.porsuk.feature.derivatives

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
 * Porsuk Options, Futures & Derivatives Platform — ViewModel Unit Testleri
 */
@OptIn(ExperimentalCoroutinesApi::class)
class DerivativesPlatformViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val fakeDerivativesRepository = object : DerivativesRepository {
        override fun getSupportedProviders() = DerivativesProviderType.entries.toList()
        override fun getSupportedAssetTypes() = DerivativesAssetType.entries.toList()
    }

    private val fakeOptionsRepository = object : OptionsRepository {
        override fun getOptionChain(underlyingSymbol: String) = flowOf(
            listOf(
                OptionContract(
                    optionId = "opt_1",
                    symbol = "THYAO_C_350",
                    strikePrice = 350.0,
                    type = OptionType.CALL
                )
            )
        )
        override suspend fun getOptionDetails(optionId: String) = OptionContract()
    }

    private val fakeFuturesRepository = object : FuturesRepository {
        override fun getFuturesContracts(underlyingSymbol: String) = flowOf(
            listOf(FuturesContract(contractSymbol = "F_THYAO0826"))
        )
    }

    private val fakeGreeksRepository = object : GreeksRepository {
        override suspend fun calculateGreeks(contract: OptionContract, spotPrice: Double) = OptionGreeks(delta = 0.54)
    }

    private val fakeStrategyRepository = object : OptionStrategyRepository {
        override fun evaluateStrategyRisk(strategy: OptionStrategyType, strikePrice: Double) = OptionStrategyRisk(strategyType = strategy)
        override fun getAvailableStrategies() = OptionStrategyType.entries.toList()
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
    fun `loadDerivativesData updates uiState with option chain and futures contracts`() = runTest {
        val viewModel = DerivativesPlatformViewModel(
            derivativesRepository = fakeDerivativesRepository,
            optionsRepository = fakeOptionsRepository,
            futuresRepository = fakeFuturesRepository,
            greeksRepository = fakeGreeksRepository,
            strategyRepository = fakeStrategyRepository
        )

        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("THYAO.IS", state.selectedUnderlyingSymbol)
        assertEquals(1, state.optionChain.size)
        assertEquals("THYAO_C_350", state.optionChain[0].symbol)
        assertEquals(1, state.futuresContracts.size)
        assertEquals(false, state.isLoading)
    }

    @Test
    fun `inspectContractGreeks updates selectedContractGreeks in uiState`() = runTest {
        val viewModel = DerivativesPlatformViewModel(
            derivativesRepository = fakeDerivativesRepository,
            optionsRepository = fakeOptionsRepository,
            futuresRepository = fakeFuturesRepository,
            greeksRepository = fakeGreeksRepository,
            strategyRepository = fakeStrategyRepository
        )

        val targetContract = OptionContract(optionId = "opt_1", symbol = "THYAO_C_350")
        viewModel.inspectContractGreeks(targetContract)
        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.selectedContractGreeks)
        assertEquals(0.54, state.selectedContractGreeks.delta, 0.001)
    }
}
