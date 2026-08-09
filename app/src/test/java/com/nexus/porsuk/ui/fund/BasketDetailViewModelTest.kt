package com.nexus.porsuk.ui.fund

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.nexus.porsuk.data.local.SettingsManager
import com.nexus.porsuk.data.local.entity.Basket
import com.nexus.porsuk.data.local.entity.BasketItem
import com.nexus.porsuk.data.local.entity.PriceSnapshot
import com.nexus.porsuk.data.remote.ScrapeResult
import com.nexus.porsuk.data.repository.FinanceRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BasketDetailViewModelTest {

    private val repository: FinanceRepository = mockk(relaxed = true)
    private val settingsManager: SettingsManager = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()
    
    private lateinit var viewModel: BasketDetailViewModel
    private val basketId = 1
    
    private val pricesFlow = MutableStateFlow<Map<String, PriceSnapshot>>(emptyMap())

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        
        val savedStateHandle = SavedStateHandle(mapOf("basketId" to basketId))
        
        // Default mocks
        every { repository.getBasketById(basketId) } returns flowOf(Basket(id = basketId, name = "Test Basket", market = "BIST"))
        every { repository.getBasketItems(basketId) } returns flowOf(emptyList())
        every { repository.prices } returns pricesFlow
        every { repository.allCompanies } returns flowOf(emptyList())
        every { settingsManager.targetAllocationJson } returns flowOf("")

        viewModel = BasketDetailViewModel(savedStateHandle, repository, settingsManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun uiState_emits_correct_data_when_basket_and_items_are_loaded() = runTest {
        // Arrange
        val items = listOf(
            BasketItem(id = 1, basketId = basketId, symbol = "THYAO", quantity = 10.0, buyPrice = 200.0, buyDate = 1000L)
        )
        val prices = mapOf("THYAO" to PriceSnapshot(symbol = "THYAO", price = 250.0, changePercent = 2.0, interval = "1d"))
        
        every { repository.getBasketItems(basketId) } returns flowOf(items)
        pricesFlow.value = prices

        // Act & Assert
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.basketName).isEqualTo("Test Basket")
            assertThat(state.totalValue).isEqualTo(2500.0)
            assertThat(state.totalCost).isEqualTo(2000.0)
            assertThat(state.holdings).hasSize(1)
            assertThat(state.holdings[0].symbol).isEqualTo("THYAO")
            assertThat(state.holdings[0].allocationPercent).isEqualTo(1.0f)
        }
    }

    @Test
    fun addStockToBasket_handles_duplicate_symbol_error() = runTest {
        // Arrange
        val items = listOf(
            BasketItem(id = 1, basketId = basketId, symbol = "THYAO", quantity = 10.0, buyPrice = 200.0, buyDate = 1000L)
        )
        every { repository.getBasketItems(basketId) } returns flowOf(items)

        // Act
        viewModel.addStockToBasket(PendingBasketItem("THYAO", 5.0, 210.0, 2000L))
        advanceUntilIdle()

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.errorMessage).contains("zaten bu sepette mevcut")
        }
        coVerify(exactly = 0) { repository.executeTransaction(any(), any(), any(), any(), any()) }
    }

    @Test
    fun addStockToBasket_calls_repository_executeTransaction_for_new_symbol() = runTest {
        // Act
        viewModel.addStockToBasket(PendingBasketItem("ASELS", 10.0, 50.0, 2000L))
        advanceUntilIdle()

        // Assert
        coVerify { repository.executeTransaction(basketId, "ASELS", 10.0, 50.0, true) }
    }

    @Test
    fun runBacktest_updates_backtestResult_on_success() = runTest {
        // Arrange
        val items = listOf(
            BasketItem(id = 1, basketId = basketId, symbol = "THYAO", quantity = 10.0, buyPrice = 200.0, buyDate = 1000L)
        )
        every { repository.getBasketItems(basketId) } returns flowOf(items)
        coEvery { repository.fetchHistoricalPrices("THYAO", any(), "1y", "1d") } returns ScrapeResult.Success(listOf(100.0, 200.0))
        coEvery { repository.fetchHistoricalPrices("XU100", "BIST", "1y", "1d") } returns ScrapeResult.Success(listOf(1000.0, 1500.0))
        coEvery { repository.fetchHistoricalPrices("USDTRY", "USD", "1y", "1d") } returns ScrapeResult.Success(listOf(20.0, 30.0))

        // Act
        viewModel.runBacktest("1y")
        advanceUntilIdle()

        // Assert
        viewModel.backtestResult.test {
            val result = awaitItem()
            assertThat(result).isNotNull()
            assertThat(result?.basketReturnPercent).isEqualTo(100.0) // (200-100)/100 * 100
            assertThat(result?.bistReturnPercent).isEqualTo(50.0) // (1500-1000)/1000 * 100
            assertThat(result?.usdReturnPercent).isEqualTo(50.0) // (30-20)/20 * 100
        }
    }

    @Test
    fun deleteBasket_calls_repository_deleteBasket_and_onSuccess() = runTest {
        // Arrange
        var successCalled = false
        val basket = Basket(id = basketId, name = "Test Basket", market = "BIST")
        every { repository.getBasketById(basketId) } returns flowOf(basket)

        // Act
        viewModel.deleteBasket { successCalled = true }
        advanceUntilIdle()

        // Assert
        coVerify { repository.deleteBasket(basket) }
        assertThat(successCalled).isTrue()
    }
}
