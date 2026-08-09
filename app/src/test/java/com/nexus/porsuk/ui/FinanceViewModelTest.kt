package com.nexus.porsuk.ui

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.nexus.porsuk.data.local.SettingsManager
import com.nexus.porsuk.data.repository.FinanceRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FinanceViewModelTest {

    private val repository: FinanceRepository = mockk(relaxed = true)
    private val settingsManager: SettingsManager = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: FinanceViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        
        // Mocking requirements for init block
        coEvery { repository.getAllCompaniesDirect() } returns emptyList()
        every { settingsManager.isSampleSeeded } returns flowOf(true)
        every { repository.allBaskets } returns flowOf(emptyList())
        every { repository.watchlist } returns flowOf(emptyList())
        every { repository.allBasketItems } returns flowOf(emptyList())
        every { repository.allCompanies } returns flowOf(emptyList())
        every { repository.getAllCachedInfo() } returns flowOf(emptyList())
        every { repository.getPortfolioHistory() } returns flowOf(emptyList())
        every { repository.getStockHistory(any()) } returns flowOf(emptyList())
        every { repository.getConsolidatedAssetsFlow() } returns flowOf(emptyList())
        every { settingsManager.numberFormat } returns flowOf("TR")

        viewModel = FinanceViewModel(repository, settingsManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `profitMode defaults to NOMINAL`() = runTest {
        viewModel.profitMode.test {
            val initialState = awaitItem()
            assertThat(initialState).isEqualTo(FinanceViewModel.ProfitCalculationMode.NOMINAL)
        }
    }

    @Test
    fun `setProfitMode updates profitMode state`() = runTest {
        viewModel.profitMode.test {
            assertThat(awaitItem()).isEqualTo(FinanceViewModel.ProfitCalculationMode.NOMINAL)
            
            viewModel.setProfitMode(FinanceViewModel.ProfitCalculationMode.USD_ADJUSTED)
            assertThat(awaitItem()).isEqualTo(FinanceViewModel.ProfitCalculationMode.USD_ADJUSTED)
        }
    }

    @Test
    fun `refreshExchangeRates calls repository refreshExchangeRates`() = runTest {
        // Act
        viewModel.refreshExchangeRates()
        advanceUntilIdle()

        // Assert
        coVerify { repository.refreshExchangeRates() }
    }
}
