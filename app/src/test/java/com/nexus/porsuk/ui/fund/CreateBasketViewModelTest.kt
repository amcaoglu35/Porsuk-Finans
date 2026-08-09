package com.nexus.porsuk.ui.fund

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.nexus.porsuk.data.local.entity.Basket
import com.nexus.porsuk.data.local.entity.BasketItem
import com.nexus.porsuk.data.repository.FinanceRepository
import com.nexus.porsuk.domain.repository.BasketRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CreateBasketViewModelTest {

    private val repository: FinanceRepository = mockk(relaxed = true)
    private val basketRepository: BasketRepository = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: CreateBasketViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = CreateBasketViewModel(repository, basketRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun onNameChange_updates_basketName_in_uiState() = runTest {
        viewModel.uiState.test {
            assertThat(awaitItem().basketName).isEmpty()
            
            viewModel.onNameChange("New Basket")
            assertThat(awaitItem().basketName).isEqualTo("New Basket")
        }
    }

    @Test
    fun addItem_adds_item_to_list_and_clears_error() = runTest {
        val item = PendingBasketItem("THYAO", 10.0, 200.0, 1000L)
        
        viewModel.addItem(item)
        
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.items).hasSize(1)
            assertThat(state.items[0].symbol).isEqualTo("THYAO")
            assertThat(state.errorMessage).isNull()
        }
    }

    @Test
    fun addItem_handles_duplicate_symbol_error() = runTest {
        val item1 = PendingBasketItem("THYAO", 10.0, 200.0, 1000L)
        val item2 = PendingBasketItem("thyao", 5.0, 210.0, 2000L)
        
        viewModel.addItem(item1)
        viewModel.addItem(item2)
        
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.items).hasSize(1)
            assertThat(state.errorMessage).contains("zaten eklenmiş")
        }
    }

    @Test
    fun removeItem_removes_item_from_list() = runTest {
        val item = PendingBasketItem("THYAO", 10.0, 200.0, 1000L)
        viewModel.addItem(item)
        
        viewModel.removeItem(item)
        
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.items).isEmpty()
        }
    }

    @Test
    fun saveBasket_calls_repository_add_methods_and_onSuccess() = runTest {
        // Arrange
        var successCalled = false
        viewModel.onNameChange("My Portfolio")
        viewModel.addItem(PendingBasketItem("THYAO", 10.0, 200.0, 1000L))
        
        // Mocking repository.addBasket to return a long ID
        coEvery { repository.addBasket(any()) } returns 1L

        // Act
        viewModel.saveBasket { successCalled = true }
        advanceUntilIdle()

        // Assert
        coVerify { basketRepository.createBasket("My Portfolio", "IST") }
        coVerify { basketRepository.addStockToBasket(any(), "THYAO", 10.0, 200.0) }
        assertThat(successCalled).isTrue()
    }
    
    @Test
    fun saveBasket_does_nothing_if_name_is_blank() = runTest {
        var successCalled = false
        viewModel.addItem(PendingBasketItem("THYAO", 10.0, 200.0, 1000L))
        
        viewModel.saveBasket { successCalled = true }
        advanceUntilIdle()
        
        assertThat(successCalled).isFalse()
        coVerify(exactly = 0) { basketRepository.createBasket(any(), any()) }
    }
}
