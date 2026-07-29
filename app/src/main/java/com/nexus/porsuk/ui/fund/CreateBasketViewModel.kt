package com.nexus.porsuk.ui.fund

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.data.local.entity.Basket
import com.nexus.porsuk.data.local.entity.BasketItem
import com.nexus.porsuk.data.repository.FinanceRepository
import com.nexus.porsuk.data.remote.ScrapeResult
import com.nexus.porsuk.domain.repository.BasketRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class Region(val label: String, val flag: String, val market: String) {
    BIST("BIST", "🇹🇷", "IST"),
    NASDAQ("NASDAQ-NYSE", "🇺🇸", "NASDAQ"),
    EUROPE("Avrupa", "🇪🇺", "FRA")
}

data class PendingBasketItem(
    val symbol: String,
    val quantity: Double,
    val buyPrice: Double,
    val buyDate: Long
)

data class CreateBasketUiState(
    val basketName: String = "",
    val selectedRegion: Region = Region.BIST,
    val items: List<PendingBasketItem> = emptyList(),
    val isBottomSheetVisible: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)

class CreateBasketViewModel(
    private val repository: FinanceRepository,
    private val basketRepository: BasketRepository? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateBasketUiState())
    val uiState: StateFlow<CreateBasketUiState> = _uiState.asStateFlow()

    fun onNameChange(name: String) {
        _uiState.update { it.copy(basketName = name, errorMessage = null) }
    }

    fun onRegionSelect(region: Region) {
        _uiState.update { it.copy(selectedRegion = region) }
    }

    fun toggleBottomSheet(visible: Boolean) {
        _uiState.update { it.copy(isBottomSheetVisible = visible) }
    }

    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun addItem(item: PendingBasketItem) {
        val cleanSymbol = item.symbol.trim().uppercase()
        val exists = _uiState.value.items.any { it.symbol.equals(cleanSymbol, ignoreCase = true) }
        if (exists) {
            _uiState.update { 
                it.copy(errorMessage = "$cleanSymbol hisse senedi zaten eklenmiş. Mükerrer ekleme yapılamaz.") 
            }
            return
        }

        _uiState.update { 
            it.copy(
                items = it.items + item.copy(symbol = cleanSymbol), 
                isBottomSheetVisible = false,
                errorMessage = null
            ) 
        }
    }

    fun removeItem(item: PendingBasketItem) {
        _uiState.update { state ->
            state.copy(items = state.items.filter { it != item })
        }
    }

    suspend fun fetchPrice(symbol: String): Double? {
        val market = _uiState.value.selectedRegion.market
        return when (val result = repository.refreshPrice(symbol, market)) {
            is ScrapeResult.Success -> result.data.price
            is ScrapeResult.Error -> null
        }
    }

    fun saveBasket(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.basketName.isBlank() || state.items.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            if (basketRepository != null) {
                val newBasketId = basketRepository.createBasket(state.basketName, state.selectedRegion.market).toInt()
                state.items.forEach { item ->
                    basketRepository.addStockToBasket(
                        basketId = newBasketId,
                        symbol = item.symbol,
                        quantity = item.quantity,
                        buyPrice = item.buyPrice
                    )
                }
            } else {
                val result = repository.addBasket(
                    Basket(
                        name = state.basketName,
                        market = state.selectedRegion.market
                    )
                )
                val basketId = result.toInt()

                state.items.forEach { item ->
                    repository.addBasketItem(
                        BasketItem(
                            basketId = basketId,
                            symbol = item.symbol,
                            quantity = item.quantity,
                            buyPrice = item.buyPrice,
                            buyDate = item.buyDate
                        )
                    )
                }
            }

            onSuccess()
        }
    }
}
